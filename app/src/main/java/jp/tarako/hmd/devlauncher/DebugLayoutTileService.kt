package jp.tarako.hmd.devlauncher

import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.InvocationTargetException

class DebugLayoutTileService : TileService() {
    companion object {
        val TAG = DebugLayoutTileService::class.java.simpleName
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile.apply {
            qsTile.state = when {
                !developSettingsEnabled() -> Tile.STATE_UNAVAILABLE
                isShownLayoutBounds() -> Tile.STATE_ACTIVE
                else -> Tile.STATE_INACTIVE
            }
            updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        qsTile.apply {
            when (state) {
                Tile.STATE_ACTIVE -> {
                    setShowLayoutBounds(false)
                    state = Tile.STATE_INACTIVE
                }
                Tile.STATE_INACTIVE -> {
                    setShowLayoutBounds(true)
                    state = Tile.STATE_ACTIVE
                }
            }
            updateTile()
        }
    }

    private fun isShownLayoutBounds(): Boolean {
        return execShellWithResult("getprop debug.layout") == "true"
    }

    private fun setShowLayoutBounds(showLayoutBounds: Boolean) {
        val clazz = Class.forName("android.os.SystemProperties")
        val method = clazz.getDeclaredMethod("set", String::class.java, String::class.java)
        method.isAccessible = true

        try {
            // java.lang.reflect.InvocationTargetException で失敗する
            method.invoke(null, "debug.layout", "$showLayoutBounds")
        } catch (e: InvocationTargetException) {
            Log.d(TAG, "failed to invoke method ... ${method.name}( \"debug.layout\",  \"$showLayoutBounds\")")
            e.printStackTrace()

            // 実行しても書き換わらない
            execShell("setprop debug.layout $showLayoutBounds")
        }

        pokeSystemProp()
    }

    private fun developSettingsEnabled() : Boolean {
        return Settings.Secure.getInt(contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0
    }

    private fun pokeSystemProp() {
        execShell("service call activity 1599295570")
    }

    private fun execShell(command: String): Process? {
        return Runtime.getRuntime().exec(command)
    }

    private fun execShellWithResult(command: String): String? {
        val process = Runtime.getRuntime().exec(command)

        return process.let {
            val bufferedReader = BufferedReader(InputStreamReader(it?.inputStream))
            var result = ""
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                if (result.isNotEmpty()) {
                    result += "\n"
                }
                result += line
                line = bufferedReader.readLine()
            }
            Log.d(TAG, "execShellWithResult: $result")
            result
        }
    }
}