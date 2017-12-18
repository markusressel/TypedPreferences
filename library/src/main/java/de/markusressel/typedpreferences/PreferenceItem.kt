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

package de.markusressel.typedpreferences

import android.content.Context
import android.support.annotation.StringRes

/**
 * PreferenceItem used to specify the access key, default value and type of a preference.
 *
 *
 * Created by Markus on 16.07.2017.
 */
class PreferenceItem<T : Any>(@param:StringRes @field:StringRes
                              val keyRes: Int, val defaultValue: T) {
    val isBaseType: Boolean

    init {
        this.isBaseType = isBaseType(defaultValue)
    }

    /**
     * Used to check if the passed in value can be saved right away or needs to be serialized by gson
     *
     * @param value value to check for type
     * @return true if it is a base type and can be saved without the need of gson, false otherwise
     */
    private fun isBaseType(value: T): Boolean {
        return (value is Boolean
                || value is String
                || value is Int
                || value is Float
                || value is Long)
    }

    /**
     * Get the key of this preference as string
     *
     * @param context application context
     * @return Preference key
     */
    fun getKey(context: Context): String {
        return context.getString(keyRes)
    }

}
