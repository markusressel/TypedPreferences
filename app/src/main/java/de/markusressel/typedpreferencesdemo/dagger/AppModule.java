package de.markusressel.typedpreferencesdemo.dagger;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import de.markusressel.typedpreferencesdemo.MainActivity;
import de.markusressel.typedpreferencesdemo.application.App;

/**
 * Created by Markus on 11.07.2017.
 */
@Module
public abstract class AppModule {

    @Binds
    abstract Application application(App application);

    @Provides
    @Singleton
    static Context provideContext(Application application) {
        return application;
    }

    @ContributesAndroidInjector
    abstract MainActivity mainActivity();

}
