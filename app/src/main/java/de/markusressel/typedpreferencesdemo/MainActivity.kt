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

import android.os.Bundle
import com.github.ajalt.timberkt.Timber
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Created by Markus on 18.07.2017.
 */

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var preferenceHandler: PreferenceHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d { "Starting activity" }

        AndroidInjection.inject(this)

        applyTheme()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        val preferencesFragment = PreferencesFragment()

        supportFragmentManager.beginTransaction()
                .replace(R.id.mainContentFrameLayout, preferencesFragment)
                .addToBackStack(preferencesFragment.tag)
                .commit()
    }

    private fun applyTheme() {
        val theme = preferenceHandler.getValue(PreferenceHandler.THEME)

        val light = resources.getString(R.string.theme_light_value).toInt()
        val dark = resources.getString(R.string.theme_dark_value).toInt()

        when (theme) {
            dark -> {
                applicationContext
                        .setTheme(R.style.AppThemeDark)
                setTheme(R.style.AppThemeDark)
            }
            light -> {
                applicationContext
                        .setTheme(R.style.AppTheme)
                setTheme(R.style.AppTheme)
            }
            else -> {
                applicationContext.setTheme(R.style.AppTheme)
                setTheme(R.style.AppTheme)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
