package tk.zwander.fabricateoverlay

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper

/**
 * The main API for registering and unregistering fabricated overlays.
 *
 * To use this, you must either have Java root/privileged access or
 * Shizuku set up and granted.
 *
 * There are a few ways to get an instance of this class.
 * 1. Use [getInstance] after Shizuku is up and running. This will retrieve
 *   an [android.content.om.IOverlayManager] instance, instantiate this class,
 *   and then fire the passed callback.
 * 2. Use [getInstanceDirect]. If you have your own way to get an
 *   [android.content.om.IOverlayManager] instance, you can use this
 *   method to pass it directly and not deal with callbacks.
 *
 * @see [ShizukuUtils]
 *
 * @param iomService an IBinder instance of [android.content.om.IOverlayManager].
 */
@SuppressLint("PrivateApi")
class FabricatedOverlayAPI private constructor(private val iomService: IBinder) {
    companion object {
        /**
         * The current API version. You probably don't need to worry about this.
         */
        const val API_VERSION = 1

        /**
         * The connection to the Shizuku service. Since IOverlayManager can only
         * be accessed by the shell user and users more privileged, the default
         * implementation needs to retrieve IOverlayManager as shell using Shizuku.
         */
        private val connection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
                synchronized(instanceLock) {
                    binding = false

                    val service = IShizukuService.Stub.asInterface(binder)

                    instance = FabricatedOverlayAPI(ShizukuBinderWrapper(service.iom))

                    callbacks.forEach { callback ->
                        callback(instance!!)
                    }
                    callbacks.clear()
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                synchronized(instanceLock) {
                    binding = false

                    instance = null
                }
            }
        }

        /**
         * Callbacks for getting the [FabricatedOverlayAPI] instance.
         */
        private val callbacks = arrayListOf<(FabricatedOverlayAPI) -> Unit>()

        /**
         * A thread lock.
         */
        private val instanceLock = Any()

        /**
         * The current [FabricatedOverlayAPI] instance.
         */
        @Volatile
        private var instance: FabricatedOverlayAPI? = null

        /**
         * Whether there's been a Shizuku Service bind request.
         */
        @Volatile
        private var binding = false

        /**
         * Get an instance of [FabricatedOverlayAPI] using Shizuku.
         *
         * @param context used to get the app's package name.
         * @param callback invoked once the [FabricatedOverlayAPI] instance is ready.
         */
        fun getInstance(context: Context, callback: (FabricatedOverlayAPI) -> Unit) {
            synchronized(instanceLock) {
                if (instance != null) {
                    //If we already have an instance, immediately invoke the callback.
                    callback(instance!!)
                } else {
                    //Otherwise, queue the callback.
                    callbacks.add(callback)

                    if (!binding) {
                        //If there's not already a bind request in progress, make one.
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

        /**
         * Directly get an instance of [FabricatedOverlayAPI] using your own instance
         * of [android.content.om.IOverlayManager].
         */
        fun getInstanceDirect(iOverlayManager: IBinder): FabricatedOverlayAPI {
            return instance ?: FabricatedOverlayAPI(iOverlayManager).apply {
                instance = this
            }
        }

        /**
         * If you've already retrieved an instance before, you can use
         * this to get it without a callback.
         */
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

    /**
     * Register a new [FabricatedOverlay]. The overlay should immediately be available
     * to enable, although it won't be enabled automatically.
     *
     * @param overlay overlay to register.
     */
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
            iomService
        )

        iomClass.getMethod(
            "commit",
            omtClass
        ).invoke(
            iomInstance,
            omtInstance
        )
    }

    /**
     * Unregister a [FabricatedOverlay].
     *
     * @param identifier the overlay identifier, retrieved using
     *   [FabricatedOverlay.identifier] or [FabricatedOverlay.generateOverlayIdentifier].
     */
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