package jp.tarako.hmd.devlauncher

import android.content.Intent
import android.provider.Settings
import android.service.quicksettings.TileService

class DevLauncherTileService : TileService() {
    override fun onClick() {
        super.onClick()
        startSettings()
    }

    private fun startSettings() {
        val developSettingsEnabled = Settings.Secure.getInt(contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0
        val action = if (developSettingsEnabled) Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS else Settings.ACTION_DEVICE_INFO_SETTINGS
        val intent = Intent().apply {
            setAction(action)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivityAndCollapse(intent)
    }
}