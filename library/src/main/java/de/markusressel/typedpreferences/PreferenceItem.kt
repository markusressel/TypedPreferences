package de.markusressel.typedpreferences

import android.content.Context
import android.support.annotation.StringRes

/**
 * PreferenceItem used to specify the access key, default value and type of a preference.
 *
 *
 * Created by Markus on 16.07.2017.
 */
class PreferenceItem<out T : Any>(@param:StringRes @field:StringRes
                                  val keyRes: Int, val defaultValue: T) {
    val isBaseType: Boolean

    init {
        this.isBaseType = isBaseType(defaultValue)
    }

    /**
     * Used to check if the passed in object can be saved right away or needs to be serialized by gson
     *
     * @param object object to check for type
     * @return true if it is a base type and can be saved without the need of gson, false otherwise
     */
    private fun isBaseType(`object`: T): Boolean {
        return if (`object` is Boolean
                || `object` is String
                || `object` is Int
                || `object` is Float
                || `object` is Long) {
            true
        } else false
    }

    /**
     * Get the key of this preference as string
     *
     * @param context application context
     * @return Preference key
     */
    fun getKey(context: Context): String {
        return context.getString(keyRes)
    }

}
