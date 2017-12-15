package de.markusressel.typedpreferencesdemo.dagger

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import de.markusressel.typedpreferencesdemo.MainActivity
import de.markusressel.typedpreferencesdemo.application.App
import javax.inject.Singleton

/**
 * Created by Markus on 11.07.2017.
 */
@Module
abstract class AppModule {

    @Binds
    internal abstract fun application(application: App): Application

    @ContributesAndroidInjector
    internal abstract fun mainActivity(): MainActivity

    @Module
    companion object {

        @Provides
        @Singleton
        @JvmStatic
        internal fun provideContext(application: Application): Context {
            return application
        }
    }

}