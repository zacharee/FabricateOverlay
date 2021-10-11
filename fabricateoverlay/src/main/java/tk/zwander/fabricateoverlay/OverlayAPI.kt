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
 * There are also some extra overlay management features here.
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
class OverlayAPI private constructor(private val iomService: IBinder) {
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

                    instance = OverlayAPI(ShizukuBinderWrapper(service.iom))

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
         * Callbacks for getting the [OverlayAPI] instance.
         */
        private val callbacks = arrayListOf<(OverlayAPI) -> Unit>()

        /**
         * A thread lock.
         */
        private val instanceLock = Any()

        /**
         * The current [OverlayAPI] instance.
         */
        @Volatile
        private var instance: OverlayAPI? = null

        /**
         * Whether there's been a Shizuku Service bind request.
         */
        @Volatile
        private var binding = false

        /**
         * Get an instance of [OverlayAPI] using Shizuku.
         *
         * @param context used to get the app's package name.
         * @param callback invoked once the [OverlayAPI] instance is ready.
         */
        fun getInstance(context: Context, callback: (OverlayAPI) -> Unit) {
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
                            .daemon(false)

                        Shizuku.bindUserService(serviceArgs, connection)
                    }
                }
            }
        }

        /**
         * Directly get an instance of [OverlayAPI] using your own instance
         * of [android.content.om.IOverlayManager].
         */
        fun getInstanceDirect(iOverlayManager: IBinder): OverlayAPI {
            return instance ?: OverlayAPI(iOverlayManager).apply {
                instance = this
            }
        }

        /**
         * If you've already retrieved an instance before, you can use
         * this to get it without a callback.
         */
        fun peekInstance(): OverlayAPI? {
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

    private val iomInstance = iomsClass.getMethod(
        "asInterface",
        IBinder::class.java
    ).invoke(
        null,
        iomService
    )

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
            .invoke(omtbInstance)!!

        commit(omtInstance)
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
        ).invoke(omtbInstance)!!

        commit(omtInstance)
    }

    /*
        IOverlayManager.aidl wrapper methods.
     */
    fun getAllOverlays(userId: Int): Map<String, List<OverlayInfo>> {
        val platformResult = iomClass.getMethod(
            "getAllOverlays",
            Int::class.java
        ).invoke(
            iomInstance,
            userId
        ) as Map<*, *>

        return platformResult.map { entry -> entry.key.toString() to (entry.value as List<*>).map { OverlayInfo(it!!) } }.toMap()
    }

    fun getOverlayInfosForTarget(targetPackageName: String, userId: Int): List<OverlayInfo> {
        val platformResult = iomClass.getMethod(
            "getOverlayInfosForTarget",
            String::class.java,
            Int::class.java
        ).invoke(
            iomInstance,
            targetPackageName,
            userId
        ) as List<*>

        return platformResult.map { OverlayInfo(it!!) }
    }

    fun getOverlayInfo(packageName: String, userId: Int): OverlayInfo {
        val platformResult = iomClass.getMethod(
            "getOverlayInfo",
            String::class.java,
            Int::class.java
        ).invoke(
            iomInstance,
            packageName,
            userId
        )

        return OverlayInfo(platformResult!!)
    }

    /**
     * @param identifier should be retrieved using [FabricatedOverlay.generateOverlayIdentifier].
     */
    fun getOverlayInfoByIdentifier(identifier: Any, userId: Int): OverlayInfo {
        val platformResult = iomClass.getMethod(
            "getOverlayInfoByIdentifier",
            oiClass,
            Int::class.java
        ).invoke(
            iomInstance,
            identifier,
            userId
        )

        return OverlayInfo(platformResult!!)
    }

    /**
     * Use this for changing the state of fabricated overlays.
     */
    fun setEnabled(identifier: Any, enable: Boolean, userId: Int) {
        val omtbInstance = omtbClass.newInstance()
        omtbClass.getMethod(
            "setEnabled",
            oiClass,
            Boolean::class.java,
            Int::class.java
        ).invoke(omtbInstance, identifier, enable, userId)

        val omtInstance = omtbClass.getMethod(
            "build"
        ).invoke(omtbInstance)!!

        commit(omtInstance)
    }

    fun setEnabled(packageName: String, enable: Boolean, userId: Int): Boolean {
        return iomClass.getMethod(
            "setEnabled",
            String::class.java,
            Boolean::class.java,
            Int::class.java
        ).invoke(
            iomInstance,
            packageName,
            enable,
            userId
        ) as Boolean
    }

    fun setEnabledExclusive(packageName: String, enable: Boolean, userId: Int): Boolean {
        return iomClass.getMethod(
            "setEnabledExclusive",
            String::class.java,
            Boolean::class.java,
            Int::class.java
        ).invoke(
            iomInstance,
            packageName,
            enable,
            userId
        ) as Boolean
    }

    fun setEnabledExclusiveInCategory(packageName: String, userId: Int): Boolean {
        return iomClass.getMethod(
            "setEnabledExclusiveInCategory",
            String::class.java,
            Int::class.java
        ).invoke(
            iomInstance,
            packageName,
            userId
        ) as Boolean
    }

    fun setPriority(packageName: String, newParentPackageName: String, userId: Int): Boolean {
        return iomClass.getMethod(
            "setPriority",
            String::class.java,
            String::class.java,
            Int::class.java
        ).invoke(
            iomInstance,
            packageName,
            newParentPackageName,
            userId
        ) as Boolean
    }

    fun setHighestPriority(packageName: String, userId: Int): Boolean {
        return iomClass.getMethod(
            "setHighestPriority",
            String::class.java,
            Int::class.java
        ).invoke(
            iomInstance,
            packageName,
            userId
        ) as Boolean
    }

    fun setLowestPriority(packageName: String, userId: Int): Boolean {
        return iomClass.getMethod(
            "setLowestPriority",
            String::class.java,
            Int::class.java
        ).invoke(
            iomInstance,
            packageName,
            userId
        ) as Boolean
    }

    @Suppress("UNCHECKED_CAST")
    fun getDefaultOverlayPackages(): Array<String> {
        return iomClass.getMethod(
            "getDefaultOverlayPackages"
        ).invoke(
            iomInstance
        ) as Array<String>
    }

    fun invalidateCachesForOverlay(packageName: String, userId: Int) {
        iomClass.getMethod(
            "invalidateCachesForOverlay",
            String::class.java,
            Int::class.java
        ).invoke(
            iomInstance,
            packageName,
            userId
        )
    }

    /**
     * @param transaction should be an [android.content.om.OverlayManagerTransaction].
     */
    fun commit(transaction: Any) {
        iomClass.getMethod(
            "commit",
            omtClass
        ).invoke(
            iomInstance,
            transaction
        )
    }
}