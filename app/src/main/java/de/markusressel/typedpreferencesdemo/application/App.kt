package de.markusressel.typedpreferencesdemo.application

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.HasActivityInjector
import de.markusressel.typedpreferencesdemo.BuildConfig
import de.markusressel.typedpreferencesdemo.dagger.DaggerAppComponent
import timber.log.Timber

/**
 * Created by Markus on 18.07.2017.
 */

class App : DaggerApplication(), HasActivityInjector {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
                .create(this)
    }

    override fun onCreate() {
        super.onCreate()

        plantTimberTrees()
    }

    private fun plantTimberTrees() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}
