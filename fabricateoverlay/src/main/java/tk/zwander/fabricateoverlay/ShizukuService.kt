package tk.zwander.fabricateoverlay

import android.os.IBinder
import rikka.shizuku.SystemServiceHelper

class ShizukuService : IShizukuService.Stub() {
    override fun destroy() {

    }

    override fun getIOM(): IBinder {
        return SystemServiceHelper.getSystemService("overlay")
    }
}