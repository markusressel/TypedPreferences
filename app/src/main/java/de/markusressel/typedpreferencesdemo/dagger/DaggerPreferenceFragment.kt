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

package de.markusressel.typedpreferencesdemo.dagger

import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceFragmentCompat
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Created by Markus on 15.07.2017.
 */
abstract class DaggerPreferenceFragment : PreferenceFragmentCompat(), HasFragmentInjector, SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun fragmentInjector(): AndroidInjector<Fragment>? {
        return childFragmentInjector
    }

    override fun onResume() {
        super.onResume()

        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)

        super.onPause()
    }

}
