package de.markusressel.typedpreferencesdemo.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.markusressel.typedpreferencesdemo.PreferencesFragment

/**
 * Created by Markus on 18.07.2017.
 */
@Module
abstract class PreferencesModule {

    @ContributesAndroidInjector
    internal abstract fun preferencesFragment(): PreferencesFragment

}
