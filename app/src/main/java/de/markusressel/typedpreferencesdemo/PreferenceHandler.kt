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
import de.markusressel.typedpreferences.PreferenceItem
import de.markusressel.typedpreferences.PreferencesHandlerBase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Markus on 18.07.2017.
 */
@Singleton
class PreferenceHandler @Inject
constructor(context: Context) : PreferencesHandlerBase(context) {

    // be sure to override the get() method
    override val sharedPreferencesName: String
        get() = "preferences"

    override val allPreferenceItems: Set<PreferenceItem<*>> = hashSetOf(
            THEME,
            BOOLEAN_SETTING,
            COMPLEX_SETTING
    )

    companion object {
        val THEME = PreferenceItem(R.string.key_theme, 0)
        val BOOLEAN_SETTING = PreferenceItem(R.string.key_boolean_setting, true)
        val COMPLEX_SETTING = PreferenceItem(R.string.key_complex_setting, ComplexClass("Complex ^", 10, listOf(1, 2, 3)))
    }

}
