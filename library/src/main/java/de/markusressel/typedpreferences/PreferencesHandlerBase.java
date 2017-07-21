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

package de.markusressel.typedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * Preference handler base class used to store general app settings
 * <p>
 * Created by Markus on 16.07.2017.
 */
public abstract class PreferencesHandlerBase {

    private static final String TAG = "PreferencesHandlerBase";

    protected Context context;
    protected Gson gson;

    private SharedPreferences sharedPreferences;
    private Map<String, ?> cachedValues;

    public PreferencesHandlerBase(Context context) {
        this.context = context;
        gson = new Gson();

        sharedPreferences = context.getSharedPreferences(getSharedPreferencesName(), Context.MODE_PRIVATE);
        forceRefreshCache();
    }

    /**
     * @return the name of the preferences (file) to use
     */
    @NonNull
    @CheckResult
    public abstract String getSharedPreferencesName();

    /**
     * @return a list of all PreferenceItems used by this PreferenceHandler
     */
    @NonNull
    @CheckResult
    public abstract List<PreferenceItem> getAllPreferenceItems();

    @Nullable
    @CheckResult
    @SuppressWarnings("unused")
    public PreferenceItem getPreferenceItem(@NonNull String key) {
        for (PreferenceItem preferenceItem : getAllPreferenceItems()) {
            if (preferenceItem.getKey(context)
                    .equals(key)) {
                return preferenceItem;
            }
        }
        return null;
    }

    /**
     * Forces an update of the cached values
     */
    public void forceRefreshCache() {
        cachedValues = sharedPreferences.getAll();
    }

    /**
     * Get a settings value by key
     * <p>
     * Note: Be sure to assign the return value of this method to variable with your expected return type.
     *
     * @param preferenceItem Key of setting
     * @param <T>            expected type of return value (optional)
     * @return settings value
     */
    @SuppressWarnings("unchecked,unused")
    @CheckResult
    @CallSuper
    public <T> T getValue(@NonNull PreferenceItem<T> preferenceItem) throws ClassCastException {
        String key = preferenceItem.getKey(context);

        // if no value was set, return preference default
        if (cachedValues.get(key) == null) {
            T defaultValue = preferenceItem.getDefaultValue();
            // save default value in file
            setValue(preferenceItem, defaultValue);
        }

        T value;
        // check if gson serialization is needed
        if (preferenceItem.isBaseType()) {
            value = (T) cachedValues.get(key);
        } else {
            // This should work but the type of T is not detected correctly at runtime :/

//          Type valueTypeToken = new TypeToken<T>() {
//          }.getType();
//          value = gson.fromJson((String) cachedValues.get(key), valueTypeToken);

            // this is a workaround for the above issue
            value = (T) gson.fromJson((String) cachedValues.get(key), preferenceItem.getDefaultValue().getClass());
        }

        Log.v(TAG, "retrieving value \"" + value + "\" for key \"" + key + "\"");

        return value;
    }

    /**
     * Set a settings value by key
     *
     * @param preferenceItem the preference to set a new value for
     * @param newValue       new value
     */
    public <T> void setValue(@NonNull PreferenceItem<T> preferenceItem, @NonNull T newValue) {
        String key = preferenceItem.getKey(context);

        Log.d(TAG, "setting new value \"" + newValue + "\" for key \"" + key + "\"");

        // store the new value
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (newValue instanceof Boolean) {
            editor.putBoolean(key, (Boolean) newValue);
        } else if (newValue instanceof String) {
            editor.putString(key, (String) newValue);
        } else if (newValue instanceof Integer) {
            editor.putInt(key, (Integer) newValue);
        } else if (newValue instanceof Float) {
            editor.putFloat(key, (Float) newValue);
        } else if (newValue instanceof Long) {
            editor.putLong(key, (Long) newValue);
        } else {
            // generate json representation of object

            // just for safety the same workaround as in getValue()
//            Type valueTypeToken = new TypeToken<T>() {
//            }.getType();
//            String json = gson.toJson(newValue, valueTypeToken);

            // workaround
            String json = gson.toJson(newValue, preferenceItem.getDefaultValue().getClass());

            // save json as a string to preferences
            editor.putString(key, json);
        }

        editor.apply();

        forceRefreshCache();
    }

    /**
     * Removes the saved value for a preference item.
     * On the next getValue() call the passed in PreferenceItem will return it's default value.
     *
     * @param preferenceItem the preference to remove the saved value for
     */
    public <T> void clear(PreferenceItem<T> preferenceItem) {
        clear(preferenceItem.getKeyRes());
    }

    /**
     * Removes the saved value for a preference item.
     * On the next getValue() call the passed in PreferenceItem will return it's default value.
     *
     * @param keyRes the key of the preference to remove the saved value for
     */
    private void clear(@StringRes int keyRes) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(context.getString(keyRes));
        editor.apply();

        forceRefreshCache();
    }

    /**
     * Remove all saved preferences
     * <p>
     * WARNING: this will permanently delete saved values!
     */
    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        forceRefreshCache();
    }
}
