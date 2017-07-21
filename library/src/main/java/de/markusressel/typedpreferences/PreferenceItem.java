package de.markusressel.typedpreferences;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import lombok.Getter;

/**
 * PreferenceItem used to specify the access key, default value and type of a preference.
 * <p>
 * Created by Markus on 16.07.2017.
 */
@Getter
public class PreferenceItem<T> {

    @StringRes
    private final int keyRes;
    private final T defaultValue;
    private final boolean isBaseType;

    public PreferenceItem(@StringRes int keyRes, @NonNull T defaultValue) {
        if (defaultValue == null) {
            throw new IllegalArgumentException("default value must not be null!");
        }

        this.keyRes = keyRes;
        this.defaultValue = defaultValue;
        this.isBaseType = isBaseType(defaultValue);
    }

    /**
     * Used to check if the passed in object can be saved right away or needs to be serialized by gson
     *
     * @param object object to check for type
     * @return true if it is a base type and can be saved without the need of gson, false otherwise
     */
    private boolean isBaseType(Object object) {
        if (object instanceof Boolean
                || object instanceof String
                || object instanceof Integer
                || object instanceof Float
                || object instanceof Long) {
            return true;
        }

        return false;
    }

    /**
     * Get the key of this preference as string
     *
     * @param context application context
     * @return Preference key
     */
    public String getKey(Context context) {
        return context.getString(keyRes);
    }

}
