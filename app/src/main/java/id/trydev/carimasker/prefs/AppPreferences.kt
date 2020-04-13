package id.trydev.carimasker.prefs

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val PREFS_FILENAME = "id.trydev.carimasker.prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME,Context.MODE_PRIVATE)

    private val LAST_LATITUDE = "LastLatitude"
    private val LAST_LONGITUDE = "LastLongitude"
    private val UID = "UID"
    private val MASKER = "MASKER"
    private val HARGAMASKER = "MASKER"
    private val APD = "APD"
    private val HARGAAPD = "APD"
    private val HANDSANITIZER = "HANDSANITIZER"
    private val HARGAHANDSANITIZER = "HANDSANITIZER"

    var lastLatitude: String?
        get() = prefs.getString(LAST_LATITUDE, null)
        set(value) = prefs.edit().putString(LAST_LATITUDE, value).apply()

    var lastLongitude: String?
        get() = prefs.getString(LAST_LONGITUDE, null)
        set(value) = prefs.edit().putString(LAST_LONGITUDE, value).apply()

    var uid: String?
        get() = prefs.getString(UID, null)
        set(value) = prefs.edit().putString(UID, value).apply()

    var masker:String?
        get() = prefs.getString(MASKER, null)
        set(value) = prefs.edit().putString(MASKER, value).apply()
    var hargaMasker:String?
        get() = prefs.getString(HARGAMASKER, null)
        set(value) = prefs.edit().putString(HARGAMASKER, value).apply()

    var handsanitizer:String?
        get() = prefs.getString(HANDSANITIZER, null)
        set(value) = prefs.edit().putString(HANDSANITIZER, value).apply()
    var hargaHandsanitizer:String?
        get() = prefs.getString(HARGAHANDSANITIZER, null)
        set(value) = prefs.edit().putString(HARGAHANDSANITIZER, value).apply()

    var apd:String?
        get() = prefs.getString(APD, null)
        set(value) = prefs.edit().putString(APD, value).apply()
    var hargaApd:String?
        get() = prefs.getString(HARGAAPD, null)
        set(value) = prefs.edit().putString(HARGAAPD, value).apply()

    fun resetPreference() {
        val editor = prefs.edit()
        editor.remove(LAST_LATITUDE)
        editor.remove(LAST_LONGITUDE)
        editor.remove(UID)
        editor.apply()
    }

}