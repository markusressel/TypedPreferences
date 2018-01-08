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

package de.markusressel.typedpreferencesdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.ArrayRes
import android.support.v4.util.SparseArrayCompat
import android.support.v7.preference.Preference
import com.github.ajalt.timberkt.Timber
import de.markusressel.typedpreferences.PreferenceItem
import de.markusressel.typedpreferencesdemo.dagger.DaggerPreferenceFragment
import javax.inject.Inject

/**
 * Created by Markus on 18.07.2017.
 */
class PreferencesFragment : DaggerPreferenceFragment() {

    @Inject
    lateinit var preferenceHandler: PreferenceHandler

    @Inject
    lateinit var appContext: Context

    private lateinit var themeMap: SparseArrayCompat<String>
    private lateinit var theme: IntListPreference
    private lateinit var booleanPreference: Preference
    private lateinit var complex: Preference
    private lateinit var clearAll: Preference

    private var booleanSettingListener: ((PreferenceItem<Boolean>, Boolean, Boolean) -> Unit)? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // set preferences file name
        preferenceManager.sharedPreferencesName = preferenceHandler.sharedPreferencesName

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)

        initializePreferenceItems()
        addListeners()
    }

    private fun addListeners() {
        val hasPreference = preferenceHandler.hasPreference(PreferenceHandler.BOOLEAN_SETTING)
        Timber.d { "PreferenceHandler has boolean preference: " + hasPreference }

        booleanSettingListener = preferenceHandler.addOnPreferenceChangedListener(PreferenceHandler.BOOLEAN_SETTING) { preference, old, new ->
            Timber.d { "Preference '${preference.getKey(appContext)}' changed from '$old' to '$new'" }
        }

        preferenceHandler.addOnPreferenceChangedListener(PreferenceHandler.THEME) { preference, old, new ->
            theme.summary = themeMap.get(new)

            // restart activity
            activity?.finish()
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun initializePreferenceItems() {
        theme = findPreference(PreferenceHandler.THEME.getKey(appContext)) as IntListPreference
        theme.setDefaultValue(PreferenceHandler.THEME.defaultValue)
        themeMap = getListPreferenceEntryValueMap(R.array.theme_values, R.array.theme_names)
        theme.summary = themeMap.get(preferenceHandler.getValue(PreferenceHandler.THEME))

        booleanPreference = findPreference(PreferenceHandler.BOOLEAN_SETTING.getKey(appContext))

        complex = findPreference(PreferenceHandler.COMPLEX_SETTING.getKey(appContext))
        val value = preferenceHandler.getValue(PreferenceHandler.COMPLEX_SETTING)
        complex.summary = value.toString()

        clearAll = findPreference(getString(R.string.key_clear_all))
        clearAll.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            preferenceHandler.clearAll()
            false
        }
    }

    /**
     * Gets a Map from two array resources
     *
     * @param valueRes values stored in preferences
     * @param nameRes  name/description of this option used in view
     * @return Map from stored value -> display name
     */
    private fun getListPreferenceEntryValueMap(@ArrayRes valueRes: Int, @ArrayRes nameRes: Int): SparseArrayCompat<String> {
        val map = SparseArrayCompat<String>()

        val values = resources.getStringArray(valueRes)
        val names = resources.getStringArray(nameRes)

        for (i in values.indices) {
            map.put(Integer.valueOf(values[i]), names[i])
        }

        return map
    }

    override fun onPause() {
        super.onPause()

        // remove a single listener
        booleanSettingListener?.let {
            preferenceHandler.removeOnPreferenceChangedListener(it)
        }

        // remove all listeners of a specific preference
        preferenceHandler.removeAllOnPreferenceChangedListeners(PreferenceHandler.BOOLEAN_SETTING)

        // remove all listeners of the handler
        preferenceHandler.removeAllOnPreferenceChangedListeners()
    }

}
