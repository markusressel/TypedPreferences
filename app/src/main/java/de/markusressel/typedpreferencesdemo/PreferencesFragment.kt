package de.markusressel.typedpreferencesdemo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // set preferences file name
        preferenceManager.sharedPreferencesName = preferenceHandler.sharedPreferencesName

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)

        initializePreferenceItems()
        addListeners()
    }

    private fun addListeners() {
        // set initial value
        preferenceHandler.setValue(PreferenceHandler.BOOLEAN_SETTING, false)

        // define lambda
        val log: (PreferenceItem<Any>, Any, Any) -> Unit = { preference, old, new -> Timber.d { "Preference '$preference' changed from '$old' to '$new'" } }
        preferenceHandler.addOnPreferenceChangedListener(PreferenceHandler.BOOLEAN_SETTING, log)

        // trigger value change
        preferenceHandler.setValue(PreferenceHandler.BOOLEAN_SETTING, true)

        preferenceHandler.removeOnPreferenceChangedListener(log)

        preferenceHandler.setValue(PreferenceHandler.BOOLEAN_SETTING, false)
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        preferenceHandler.forceRefreshCache()

        val preferenceItem = preferenceHandler.getPreferenceItem(key) ?: return

        if (preferenceItem == PreferenceHandler.THEME) {
            theme.summary = themeMap.get(preferenceHandler.getValue(preferenceItem))

            // restart activity
            activity?.finish()
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
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

}
