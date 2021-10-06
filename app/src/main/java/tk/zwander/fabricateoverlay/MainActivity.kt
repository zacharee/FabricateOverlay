package tk.zwander.fabricateoverlay

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

@SuppressLint("PrivateApi")
class MainActivity : AppCompatActivity(), Shizuku.OnRequestPermissionResultListener {
    private val iomClass = Class.forName("android.content.om.IOverlayManager")
    private val iomsClass = Class.forName("android.content.om.IOverlayManager\$Stub")
    private val foClass = Class.forName("android.content.om.FabricatedOverlay")
    private val fobClass = Class.forName("android.content.om.FabricatedOverlay\$Builder")
    private val omtClass = Class.forName("android.content.om.OverlayManagerTransaction")
    private val omtbClass = Class.forName("android.content.om.OverlayManagerTransaction\$Builder")

    private val serviceArgs = Shizuku.UserServiceArgs(
        ComponentName(
            BuildConfig.APPLICATION_ID,
            ShizukuService::class.java.name
        )
    )
        .processNameSuffix("service")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder) {
            val service = IShizukuService.Stub.asInterface(p1)

            initService(service)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HiddenApiBypass.setHiddenApiExemptions("L")

        if (!Shizuku.pingBinder()) {
            finish()
        }

        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Shizuku.addRequestPermissionResultListener(this)
            Shizuku.requestPermission(100)
        } else {
            init()
        }

        setContentView(R.layout.activity_main)
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            init()
        } else {
            finish()
        }
    }

    private fun init() {
        Shizuku.bindUserService(serviceArgs, connection)
    }

    private fun initService(service: IShizukuService) {
        val fobInstance = fobClass.getConstructor(
            String::class.java,
            String::class.java,
            String::class.java
        ).newInstance(
            "com.android.shell",
            "TestOverlay",
            "com.android.systemui"
        )

        fobClass.getMethod(
            "setResourceValue",
            String::class.java,
            Int::class.java,
            Int::class.java
        ).invoke(
            fobInstance,
            "com.android.systemui:integer/quick_settings_num_columns",
            0x10,
            0x03
        )

        val foInstance = fobClass.getMethod(
            "build"
        ).invoke(fobInstance)

        val omtbInstance = omtbClass.newInstance()
        omtbClass.getMethod(
            "registerFabricatedOverlay",
            foClass
        ).invoke(omtbInstance, foInstance)

        val omtInstance = omtbClass.getMethod(
            "build"
        ).invoke(omtbInstance)

        val iomInstance = iomsClass.getMethod(
            "asInterface",
            IBinder::class.java
        ).invoke(
            null,
            ShizukuBinderWrapper(service.iom)
        )

        Log.e("FabricateOverlay", "COMMITTING")
        iomClass.getMethod(
            "commit",
            omtClass
        ).invoke(
            iomInstance,
            omtInstance
        )
    }
}