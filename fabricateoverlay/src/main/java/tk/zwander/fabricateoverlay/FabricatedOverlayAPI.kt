package tk.zwander.fabricateoverlay

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper

@SuppressLint("PrivateApi")
class FabricatedOverlayAPI private constructor(private val iomService: IBinder) {
    companion object {
        const val API_VERSION = 1

        private val connection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
                synchronized(instanceLock) {
                    binding = false

                    val service = IShizukuService.Stub.asInterface(binder)

                    instance = FabricatedOverlayAPI(service.iom)

                    callbacks.forEach { callback ->
                        callback(instance!!)
                    }
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                synchronized(instanceLock) {
                    binding = false

                    instance = null
                }
            }
        }

        private val callbacks = arrayListOf<(FabricatedOverlayAPI) -> Unit>()
        private val instanceLock = Any()

        @Volatile
        private var instance: FabricatedOverlayAPI? = null

        @Volatile
        private var binding = false

        fun getInstance(context: Context, callback: (FabricatedOverlayAPI) -> Unit) {
            synchronized(instanceLock) {
                if (instance != null) {
                    callback(instance!!)
                } else {
                    callbacks.add(callback)

                    if (!binding) {
                        binding = true

                        val serviceArgs = Shizuku.UserServiceArgs(
                            ComponentName(
                                context.packageName,
                                ShizukuService::class.java.name
                            )
                        ).processNameSuffix("service")
                            .debuggable(BuildConfig.DEBUG)
                            .version(API_VERSION)

                        Shizuku.bindUserService(serviceArgs, connection)
                    }
                }
            }
        }

        fun peekInstance(): FabricatedOverlayAPI? {
            return instance
        }
    }

    private val iomClass = Class.forName("android.content.om.IOverlayManager")
    private val iomsClass = Class.forName("android.content.om.IOverlayManager\$Stub")
    private val foClass = Class.forName("android.content.om.FabricatedOverlay")
    private val fobClass = Class.forName("android.content.om.FabricatedOverlay\$Builder")
    private val omtClass = Class.forName("android.content.om.OverlayManagerTransaction")
    private val omtbClass = Class.forName("android.content.om.OverlayManagerTransaction\$Builder")
    private val oiClass = Class.forName("android.content.om.OverlayIdentifier")

    fun registerFabricatedOverlay(overlay: FabricatedOverlay) {
        val fobInstance = fobClass.getConstructor(
            String::class.java,
            String::class.java,
            String::class.java
        ).newInstance(
            overlay.sourcePackage,
            overlay.overlayName,
            overlay.targetPackage
        )

        val setResourceValueMethod = fobClass.getMethod(
            "setResourceValue",
            String::class.java,
            Int::class.java,
            Int::class.java
        )

        overlay.entries.forEach { (_, entry) ->
            setResourceValueMethod.invoke(
                fobInstance,
                entry.resourceName,
                entry.resourceType,
                entry.resourceValue
            )
        }

        val foInstance = fobClass.getMethod("build")
            .invoke(fobInstance)

        val omtbInstance = omtbClass.newInstance()
        omtbClass.getMethod(
            "registerFabricatedOverlay",
            foClass
        ).invoke(
            omtbInstance, foInstance
        )

        val omtInstance = omtbClass.getMethod("build")
            .invoke(omtbInstance)

        val iomInstance = iomsClass.getMethod(
            "asInterface",
            IBinder::class.java
        ).invoke(
            null,
            ShizukuBinderWrapper(iomService)
        )

        iomClass.getMethod(
            "commit",
            omtClass
        ).invoke(
            iomInstance,
            omtInstance
        )
    }

    fun unregisterFabricatedOverlay(identifier: Any) {
        val omtbInstance = omtbClass.newInstance()
        omtbClass.getMethod(
            "unregisterFabricatedOverlay",
            oiClass
        ).invoke(omtbInstance, identifier)

        val omtInstance = omtbClass.getMethod(
            "build"
        ).invoke(omtbInstance)

        val iomInstance = iomsClass.getMethod(
            "asInterface",
            IBinder::class.java
        ).invoke(
            null,
            ShizukuBinderWrapper(iomService)
        )

        iomClass.getMethod(
            "commit",
            omtClass
        ).invoke(
            iomInstance,
            omtInstance
        )
    }
}