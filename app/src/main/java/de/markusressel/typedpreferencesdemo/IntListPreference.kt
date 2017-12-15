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

package de.markusressel.typedpreferencesdemo

import android.content.Context
import android.support.v7.preference.ListPreference
import android.util.AttributeSet

/**
 * Created by Markus on 31.07.2016.
 */
class IntListPreference : ListPreference {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun persistString(value: String?): Boolean {
        return if (value == null) {
            false
        } else {
            persistInt(Integer.valueOf(value)!!)
        }
    }

    override fun getPersistedString(defaultReturnValue: String?): String {
        if (sharedPreferences.contains(key)) {
            val intValue = getPersistedInt(0)
            return intValue.toString()
        } else {
            return defaultReturnValue as String
        }
    }

}
