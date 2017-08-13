package de.markusressel.typedpreferencesdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by Markus on 18.07.2017.
 */

public class MainActivity extends DaggerAppCompatActivity {

    @Inject
    PreferenceHandler preferenceHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        applyTheme();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        PreferencesFragment preferencesFragment = new PreferencesFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContentFrameLayout, preferencesFragment)
                .addToBackStack(preferencesFragment.getTag())
                .commit();
    }

    public void applyTheme() {
        int theme = preferenceHandler.getValue(PreferenceHandler.THEME);
        switch (theme) {
            case 1:
                getApplicationContext()
                        .setTheme(R.style.AppThemeDark);
                setTheme(R.style.AppThemeDark);
                break;
            default:
            case 0:
                getApplicationContext()
                        .setTheme(R.style.AppTheme);
                setTheme(R.style.AppTheme);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
