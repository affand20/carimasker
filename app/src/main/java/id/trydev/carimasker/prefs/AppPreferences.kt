package id.trydev.carimasker.prefs

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val PREFS_FILENAME = "id.trydev.carimasker.prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME,Context.MODE_PRIVATE)

    private val LAST_LATITUDE = "LastLatitude"
    private val LAST_LONGITUDE = "LastLongitude"

    var lastLatitude: String?
        get() = prefs.getString(LAST_LATITUDE, null)
        set(value) = prefs.edit().putString(LAST_LATITUDE, value).apply()

    var lastLongitude: String?
        get() = prefs.getString(LAST_LONGITUDE, null)
        set(value) = prefs.edit().putString(LAST_LONGITUDE, value).apply()

    fun resetPreference() {
        val editor = prefs.edit()
        editor.remove(LAST_LATITUDE)
        editor.remove(LAST_LONGITUDE)
        editor.apply()
    }

}