package de.markusressel.typedpreferencesdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.preference.Preference;

import javax.inject.Inject;

import de.markusressel.typedpreferences.PreferenceItem;
import de.markusressel.typedpreferencesdemo.dagger.DaggerPreferenceFragment;

/**
 * Created by Markus on 18.07.2017.
 */

public class PreferencesFragment extends DaggerPreferenceFragment {

    @Inject
    PreferenceHandler preferenceHandler;

    private SparseArrayCompat<String> themeMap;
    private IntListPreference theme;
    private Preference complex;
    private Preference clearAll;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // set preferences file name
        getPreferenceManager().setSharedPreferencesName(preferenceHandler.getSharedPreferencesName());

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        initializePreferenceItems();
    }

    private void initializePreferenceItems() {
        theme = (IntListPreference) findPreference(PreferenceHandler.THEME.getKey(getContext()));
        theme.setDefaultValue(PreferenceHandler.THEME.getDefaultValue());
        themeMap = getListPreferenceEntryValueMap(R.array.theme_values, R.array.theme_names);
        theme.setSummary(themeMap.get(preferenceHandler.getValue(PreferenceHandler.THEME)));

        complex = findPreference(PreferenceHandler.COMPLEX_SETTING.getKey(getContext()));
        ComplexClass value = preferenceHandler.getValue(PreferenceHandler.COMPLEX_SETTING);
        complex.setSummary(value.toString());

        clearAll = findPreference(getString(R.string.key_clear_all));
        clearAll.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                preferenceHandler.clearAll();
                return false;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        preferenceHandler.forceRefreshCache();

        PreferenceItem preferenceItem = preferenceHandler.getPreferenceItem(key);
        if (preferenceItem == null) {
            return;
        }

        if (preferenceItem == PreferenceHandler.THEME) {
            theme.setSummary(themeMap.get(preferenceHandler.<Integer>getValue(preferenceItem)));

            // restart activity
            getActivity().finish();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * Gets a Map from two array resources
     *
     * @param valueRes values stored in preferences
     * @param nameRes  name/description of this option used in view
     * @return Map from stored value -> display name
     */
    private SparseArrayCompat<String> getListPreferenceEntryValueMap(@ArrayRes int valueRes, @ArrayRes int nameRes) {
        SparseArrayCompat<String> map = new SparseArrayCompat<>();

        String[] values = getResources().getStringArray(valueRes);
        String[] names = getResources().getStringArray(nameRes);

        for (int i = 0; i < values.length; i++) {
            map.put(Integer.valueOf(values[i]), names[i]);
        }

        return map;
    }

}
