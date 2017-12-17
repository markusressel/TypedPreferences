package de.markusressel.typedpreferences

/**
 * Created by Markus on 16.12.2017.
 */
interface OnPreferenceChangedListener<in T : Any> {

    /**
     * Called when the value of the preference has changed and only if it has changed.
     * If you set the same value twice this will not be called.
     */
    fun onPreferenceChanged(preferenceItem: PreferenceItem<T>, oldValue: T, newValue: T)

}