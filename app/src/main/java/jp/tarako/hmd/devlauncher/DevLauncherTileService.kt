package jp.tarako.hmd.devlauncher

import android.content.Intent
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class DevLauncherTileService : TileService() {
    override fun onClick() {
        super.onClick()
        startSettings()
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile.apply {
            state = if (developSettingsEnabled()) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            updateTile()
        }
    }

    private fun startSettings() {
        val action = if (developSettingsEnabled()) Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS else Settings.ACTION_DEVICE_INFO_SETTINGS
        val intent = Intent().apply {
            setAction(action)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivityAndCollapse(intent)
    }

    private fun developSettingsEnabled() : Boolean {
        return Settings.Secure.getInt(contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0
    }
}