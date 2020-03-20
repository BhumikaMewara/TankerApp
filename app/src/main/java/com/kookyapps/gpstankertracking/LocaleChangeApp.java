package com.kookyapps.gpstankertracking;

import android.app.Application;
import android.content.res.Configuration;

import com.franmontiel.localechanger.LocaleChanger;

import java.util.ArrayList;
import java.util.Locale;

public class LocaleChangeApp extends Application {

    ArrayList<Locale> SUPPORTED_LOCALES;

    @Override
    public void onCreate() {
        super.onCreate();
        SUPPORTED_LOCALES = new ArrayList<>();
        SUPPORTED_LOCALES.add(new Locale("en"));
        SUPPORTED_LOCALES.add(new Locale("hi"));
        LocaleChanger.initialize(getApplicationContext(), SUPPORTED_LOCALES);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleChanger.onConfigurationChanged();
    }
}
