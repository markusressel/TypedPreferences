package de.markusressel.typedpreferencesdemo

import android.content.Context
import de.markusressel.typedpreferences.PreferenceItem
import de.markusressel.typedpreferences.PreferencesHandlerBase
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Markus on 18.07.2017.
 */
@Singleton
class PreferenceHandler @Inject
constructor(context: Context) : PreferencesHandlerBase(context) {

    private val allPreferences: MutableList<PreferenceItem<*>>

    override val sharedPreferencesName: String
        get() = "preferences"

    override val allPreferenceItems: MutableList<PreferenceItem<*>>
        get() = allPreferences

    init {
        allPreferences = LinkedList()
        allPreferences.add(THEME)
        allPreferences.add(BOOLEAN_SETTING)
        allPreferences.add(COMPLEX_SETTING)
    }

    companion object {
        val THEME = PreferenceItem(R.string.key_theme, 0)
        val BOOLEAN_SETTING = PreferenceItem(R.string.key_boolean_setting, true)
        val COMPLEX_SETTING = PreferenceItem(R.string.key_complex_setting, ComplexClass("Complex ^", 10, listOf(1, 2, 3)))
    }

}
