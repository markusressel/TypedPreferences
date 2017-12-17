/*
 *     PowerSwitch by Max Rosin & Markus Ressel
 *     Copyright (C) 2015  Markus Ressel
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.markusressel.typedpreferences

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import android.support.annotation.StringRes
import android.util.Log
import com.google.gson.Gson

/**
 * Preference handler base class used to store general app settings
 *
 *
 * Created by Markus on 16.07.2017.
 */
abstract class PreferencesHandlerBase(protected var context: Context) {

    private var gson: Gson = Gson()

    private val sharedPreferences: SharedPreferences
    private lateinit var cachedValues: Map<String, *>

    private val preferenceListeners: MutableMap<PreferenceItem<Any>, MutableCollection<(PreferenceItem<Any>, Any, Any) -> Unit>?> = mutableMapOf()

    /**
     * @return the name of the preferences (file) to use
     */
    @get:CheckResult
    abstract val sharedPreferencesName: String

    /**
     * @return a list of all PreferenceItems used by this PreferenceHandler
     */
    @get:CheckResult
    abstract val allPreferenceItems: MutableList<PreferenceItem<*>>

    init {
        sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
        forceRefreshCache()
    }

    @CheckResult
    fun getPreferenceItem(key: String): PreferenceItem<*>? {
        return allPreferenceItems.firstOrNull { it.getKey(context) == key }
    }

    /**
     * Add a listener to a specific PreferenceItem
     */
    fun <T : Any> addOnPreferenceChangedListener(preferenceItem: PreferenceItem<T>, listener: (PreferenceItem<Any>, Any, Any) -> Unit): Boolean {
        val listeners = preferenceListeners.getOrPut(preferenceItem) { HashSet() }

        if (listeners != null) {
            if (listeners.contains(listener)) {
                Log.w(TAG, "Listener is already registered for this PreferenceItem")
                return false
            }

            return listeners.add(listener)
        }

        return false
    }

    /**
     * Remove a listener of a specific PreferenceItem
     */
    fun <T : Any> removeOnPreferenceChangedListener(preferenceItem: PreferenceItem<T>, listener: (PreferenceItem<T>, T, T) -> Unit): Boolean {
        val listeners = preferenceListeners.getOrElse(preferenceItem) { HashSet() }

        if (listeners != null) {
            return listeners.remove(listener)
        } else {
            Log.w(TAG, "listener list was null, but should never be")
        }

        return false
    }

    /**
     * Remove a listener of a specific PreferenceItem
     */
    fun <T : Any> removeOnPreferenceChangedListener(listener: (PreferenceItem<T>, T, T) -> Unit): Boolean {
        for ((key, listeners) in preferenceListeners) {
            if (listeners != null && listeners.contains(listener)) {
                listeners.remove(listener)
                return true
            }
        }

        return false
    }

    /**
     * Forces an update of the cached values
     */
    fun forceRefreshCache() {
        cachedValues = sharedPreferences.all
    }

    /**
     * Get a settings value by key
     *
     *
     * Note: Be sure to assign the return value of this method to variable with your expected return type.
     *
     * @param preferenceItem Key of setting
     * @param <T>            expected type of return value (optional)
     * @return settings value
    </T> */
    @CheckResult
    @CallSuper
    fun <T : Any> getValue(preferenceItem: PreferenceItem<T>): T {
        val key = preferenceItem.getKey(context)
        val value = internalGetValue(preferenceItem, key)

        Log.v(TAG, "retrieving value \"$value\" for key \"$key\"")

        return value
    }

    private fun <T : Any> internalGetValue(preferenceItem: PreferenceItem<T>, key: String): T {
        // if no value was set, return preference default
        if (cachedValues[key] == null) {
            val defaultValue = preferenceItem.defaultValue
            // save default value in file
            internalSetValue(preferenceItem, defaultValue)
        }

        val value: T
        // check if gson serialization is needed
        if (preferenceItem.isBaseType) {
            value = cachedValues[key] as T
        } else {
            // This should work but the type of T is not detected correctly at runtime :/

            //          Type valueTypeToken = new TypeToken<T>() {
            //          }.getType();
            //          value = gson.fromJson((String) cachedValues.get(key), valueTypeToken);

            // this is a workaround for the above issue
            value = gson.fromJson<Any>(cachedValues[key] as String, preferenceItem.defaultValue::class.java) as T
        }

        return value
    }

    /**
     * Set a settings value by key
     *
     * @param preferenceItem the preference to set a new value for
     * @param newValue       new value
     */
    fun <T : Any> setValue(preferenceItem: PreferenceItem<T>, newValue: T) {
        internalSetValue(preferenceItem, newValue)
    }

    private fun <T : Any> internalSetValue(preferenceItem: PreferenceItem<T>, newValue: T) {
        val key = preferenceItem.getKey(context)

        val oldValue = internalGetValue(preferenceItem, key)
        notifyListeners(preferenceItem, oldValue, newValue)

        Log.d(TAG, "setting new value \"$newValue\" for key \"$key\"")

        // store the new value
        val editor = sharedPreferences.edit()

        when (newValue) {
            is Boolean -> editor.putBoolean(key, newValue as Boolean)
            is String -> editor.putString(key, newValue as String)
            is Int -> editor.putInt(key, newValue as Int)
            is Float -> editor.putFloat(key, newValue as Float)
            is Long -> editor.putLong(key, newValue as Long)
            else -> {
                // generate json representation of object

                // just for safety the same workaround as in getValue()
                //            Type valueTypeToken = new TypeToken<T>() {
                //            }.getType();
                //            String json = gson.toJson(newValue, valueTypeToken);

                // workaround
                val json = gson.toJson(newValue, preferenceItem.defaultValue::class.java)

                // save json as a string to preferences
                editor.putString(key, json)
            }
        }

        editor.apply()

        forceRefreshCache()
    }

    /**
     *
     */
    private fun <T : Any> notifyListeners(preferenceItem: PreferenceItem<T>, oldValue: T, newValue: T) {
        if (oldValue != newValue) {
            preferenceListeners.getOrDefault(preferenceItem, HashSet())?.forEach {
                it.invoke(preferenceItem, oldValue, newValue)
            }
        }
    }

    /**
     * Removes the saved value for a preference item.
     * On the next getValue() call the passed in PreferenceItem will return it's default value
     * which will then also be written to the preference file.
     *
     * @param preferenceItem the preference to remove the saved value for
     */
    fun <T : Any> clear(preferenceItem: PreferenceItem<T>) {
        clear(preferenceItem.keyRes)
    }

    /**
     * Removes the saved value for a preference item.
     * On the next getValue() call the passed in PreferenceItem will return it's default value
     * which will then also be written to the preference file.
     *
     * @param keyRes the key of the preference to remove the saved value for
     */
    private fun clear(@StringRes keyRes: Int) {
        val editor = sharedPreferences.edit()
        editor.remove(context.getString(keyRes))
        editor.apply()

        forceRefreshCache()
    }

    /**
     * Remove all saved preferences
     *
     *
     * WARNING: this will permanently delete saved values!
     */
    fun clearAll() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        forceRefreshCache()
    }

    companion object {
        private val TAG = "PreferencesHandlerBase"
    }
}