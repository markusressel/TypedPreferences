/*
 * Copyright (c) 2017 Markus Ressel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    private val preferenceListeners: MutableMap<PreferenceItem<Any>, MutableCollection<(PreferenceItem<*>, Any, Any) -> Unit>?> = mutableMapOf()

    /**
     * @return the name of the preferences (file) to use
     */
    @get:CheckResult
    abstract val sharedPreferencesName: String

    /**
     * @return a list of all PreferenceItems used by this PreferenceHandler
     */
    @get:CheckResult
    abstract val allPreferenceItems: Set<PreferenceItem<*>>

    init {
        sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
        forceRefreshCache()
    }

    /**
     * Checks if this PreferencesHandler contains the specified PreferenceItem
     *
     * @param preferenceItem the item to check
     */
    fun <T : Any> hasPreference(preferenceItem: PreferenceItem<T>): Boolean {
        return allPreferenceItems.contains(preferenceItem)
    }

    /**
     * Get a PreferenceItem by it's key
     *
     * @param key the key of the PreferenceItem to get
     */
    @CheckResult
    fun getPreferenceItem(key: String): PreferenceItem<*>? {
        return allPreferenceItems.firstOrNull { it.getKey(context) == key }
    }

    /**
     * Add a listener to a specific PreferenceItem
     *
     * NOTE: This listener will not be able to detect changes
     * made outside of this PreferencesHandler (f.ex. a PreferenceFragment)
     * To keep track of those changes
     * you need to do this manually (f.ex. using the onSharedPreferenceChanged() method).
     *
     * @param preferenceItem the preferenceItem to listen for changes
     * @param listener the actions that should be taken when a change is registered
     *
     * @return the listener if it was added successfully, null otherwise
     */
    fun <T : Any> addOnPreferenceChangedListener(preferenceItem: PreferenceItem<T>, listener: (PreferenceItem<T>, T, T) -> Unit): ((PreferenceItem<T>, T, T) -> Unit)? {
        if (logIfMissingPreferenceItem(preferenceItem)) {
            return null
        }

        @Suppress("UNCHECKED_CAST")
        val listeners = preferenceListeners.getOrPut(preferenceItem as PreferenceItem<Any>) { HashSet() }

        if (listeners != null) {
            // this cast is safe, as the type a specific preference will never change during runtime
            @Suppress("UNCHECKED_CAST")
            val listenersWithType = listeners as MutableCollection<(PreferenceItem<T>, T, T) -> Unit>

            if (listenersWithType.contains(listener)) {
                Log.w(TAG, "Listener is already registered for this PreferenceItem")
                return null
            }

            if (listenersWithType.add(listener))
                return listener
            else {
                return null
            }
        }

        return null
    }

    /**
     * Remove a listener of a specific PreferenceItem
     *
     * @param listener the listener to remove
     * @return true if it was removed, false otherwise
     */
    fun <T : Any> removeOnPreferenceChangedListener(listener: (PreferenceItem<T>, T, T) -> Unit): Boolean {
        for ((_, listeners) in preferenceListeners) {
            if (listeners != null && listeners.contains(listener)) {
                listeners.remove(listener)
                return true
            }
        }

        return false
    }

    /**
     * Remove all OnPreferenceChanged listeners for the specified PreferenceItem from this PreferencesHandler
     *
     * @param preferenceItem the preferenceItem to clear listeners for
     */
    fun <T : Any> removeAllOnPreferenceChangedListeners(preferenceItem: PreferenceItem<T>) {
        if (logIfMissingPreferenceItem(preferenceItem)) {
            return
        }

        @Suppress("UNCHECKED_CAST")
        val listeners = preferenceListeners.getOrDefault(preferenceItem as PreferenceItem<Any>, HashSet())
        listeners?.clear()
    }

    /**
     * Remove all OnPreferenceChanged listeners from this PreferencesHandler
     */
    fun removeAllOnPreferenceChangedListeners() {
        for ((_, listeners) in preferenceListeners) {
            listeners?.clear()
        }
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
        throwIfMissingPreferenceItem(preferenceItem)

        val key = preferenceItem.getKey(context)
        val value = internalGetValue(preferenceItem, key)

        Log.v(TAG, "retrieving value \"$value\" for key \"$key\"")

        return value
    }

    /**
     * Internal method for retrieving a value for a PreferenceItem
     *
     * @param preferenceItem the PreferenceItem to retrieve the value of
     * @param key            the key of the PreferenceItem
     */
    private fun <T : Any> internalGetValue(preferenceItem: PreferenceItem<T>, key: String): T {
        // if no value was set, return preference default
        if (cachedValues[key] == null) {
            val defaultValue = preferenceItem.defaultValue
            // save default value in file
            internalSetValue(preferenceItem, key, defaultValue, defaultValue)
        }

        val value: T
        // check if gson serialization is needed
        if (preferenceItem.isBaseType) {
            @Suppress("UNCHECKED_CAST")
            value = cachedValues[key] as T
        } else {
            // This should work but the type of T is not detected correctly at runtime :/

            //          Type valueTypeToken = new TypeToken<T>() {
            //          }.getType();
            //          value = gson.fromJson((String) cachedValues.get(key), valueTypeToken);

            // this is a workaround for the above issue
            @Suppress("UNCHECKED_CAST")
            value = gson.fromJson<Any>(cachedValues[key] as String, preferenceItem.defaultValue::class.java) as T
        }

        return value
    }

    /**
     * Set a settings value by key
     *
     * @param preferenceItem the preference to set a new value for
     * @param newValue       the new value
     */
    fun <T : Any> setValue(preferenceItem: PreferenceItem<T>, newValue: T) {
        throwIfMissingPreferenceItem(preferenceItem)

        val key = preferenceItem.getKey(context)
        val oldValue = internalGetValue(preferenceItem, key)

        Log.d(TAG, "setting new value \"$newValue\" for key \"$key\"")

        internalSetValue(preferenceItem, key, oldValue, newValue)
    }

    /**
     * Internal method for setting a new value for a PreferenceItem
     *
     * @param preferenceItem the preference to set a new value for
     * @param key the key of the PreferenceItem
     * @param newValue       the new value
     */
    private fun <T : Any> internalSetValue(preferenceItem: PreferenceItem<T>, key: String, oldValue: T, newValue: T) {
        notifyListeners(preferenceItem, oldValue, newValue)


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
     * Calls the listeners if a value change is detected
     *
     * @param preferenceItem the PreferenceItem that has been assigned a new value
     * @param oldValue the old value of the PreferenceItem
     * @param newValue the new value of the PreferenceItem
     */
    private fun <T : Any> notifyListeners(preferenceItem: PreferenceItem<T>, oldValue: T, newValue: T) {
        if (oldValue != newValue) {
            @Suppress("UNCHECKED_CAST")
            preferenceListeners.getOrDefault(preferenceItem as PreferenceItem<Any>, HashSet())?.forEach {
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

    /**
     * Prints a log message if the specified PreferenceItem is missing from this PreferenceHandler
     *
     * @param preferenceItem the PreferenceItem that was not found
     */
    private fun <T : Any> logIfMissingPreferenceItem(preferenceItem: PreferenceItem<T>): Boolean {
        if (!hasPreference(preferenceItem)) {
            Log.w(TAG, "This PreferencesHandler doesn't contain the " +
                    "specified PreferenceItem (key: '" + preferenceItem.getKey(context) + "')! " +
                    "Did you add it to the 'allPreferences' list?")
            return true
        } else {
            return false
        }
    }

    /**
     * Raises an Exception if the specified PreferenceItem is missing from this PreferenceHandler
     *
     * @param preferenceItem the PreferenceItem that was not found
     */
    private fun <T : Any> throwIfMissingPreferenceItem(preferenceItem: PreferenceItem<T>) {
        if (!hasPreference(preferenceItem)) {
            throw IllegalArgumentException("This PreferencesHandler doesn't contain the " +
                    "specified PreferenceItem (key: '" + preferenceItem.getKey(context) + "')! " +
                    "Did you add it to the 'allPreferences' list?")
        }
    }

    companion object {
        private val TAG = "PreferencesHandlerBase"
    }
}