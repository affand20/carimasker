package id.trydev.carimasker.prefs

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val PREFS_FILENAME = "id.trydev.carimasker.prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME,Context.MODE_PRIVATE)

    private val LAST_LATITUDE = "LastLatitude"
    private val LAST_LONGITUDE = "LastLongitude"
    private val UID = "UID"

    var lastLatitude: String?
        get() = prefs.getString(LAST_LATITUDE, null)
        set(value) = prefs.edit().putString(LAST_LATITUDE, value).apply()

    var lastLongitude: String?
        get() = prefs.getString(LAST_LONGITUDE, null)
        set(value) = prefs.edit().putString(LAST_LONGITUDE, value).apply()

    var uid: String?
        get() = prefs.getString(UID, null)
        set(value) = prefs.edit().putString(UID, value).apply()

    fun resetPreference() {
        val editor = prefs.edit()
        editor.remove(LAST_LATITUDE)
        editor.remove(LAST_LONGITUDE)
        editor.remove(UID)
        editor.apply()
    }

}