package de.markusressel.typedpreferencesdemo.application;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.HasActivityInjector;
import de.markusressel.typedpreferencesdemo.dagger.DaggerAppComponent;

/**
 * Created by Markus on 18.07.2017.
 */

public class App extends DaggerApplication implements HasActivityInjector {

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder()
                .create(this);
    }

}
