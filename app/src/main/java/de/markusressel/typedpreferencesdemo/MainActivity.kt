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
