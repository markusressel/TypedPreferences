package de.markusressel.typedpreferencesdemo;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.markusressel.typedpreferences.PreferenceItem;
import de.markusressel.typedpreferences.PreferencesHandlerBase;

/**
 * Created by Markus on 18.07.2017.
 */
@Singleton
public class PreferenceHandler extends PreferencesHandlerBase {

    public static final PreferenceItem<Integer> THEME = new PreferenceItem<>(R.string.key_theme, 0);
    public static final PreferenceItem<Boolean> BOOLEAN_SETTING = new PreferenceItem<>(R.string.key_boolean_setting, true);

    private List<PreferenceItem> allPreferences;

    @Inject
    public PreferenceHandler(Context context) {
        super(context);

        allPreferences = new LinkedList<>();
        allPreferences.add(THEME);
        allPreferences.add(BOOLEAN_SETTING);
    }

    @NonNull
    @Override
    public String getSharedPreferencesName() {
        return "preferences";
    }

    @NonNull
    @Override
    public List<PreferenceItem> getAllPreferenceItems() {
        return allPreferences;
    }

}
