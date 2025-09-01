// Copyright (c) 2021 Scala
//
// Please see the included LICENSE file for more information.

package io.scalaproject.androidminer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;

import static io.scalaproject.androidminer.MainActivity.contextOfApplication;

public class MobileMinerApplication extends Application implements LifecycleObserver {
    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);

        SharedPreferences preferences = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
        Config.initialize(preferences);

        // Initialize ACRA
        ACRA.init(this, new CoreConfigurationBuilder()
                .withBuildConfigClass(BuildConfig.class)
                .withPluginConfigurations(
                        new MailSenderConfigurationBuilder()
                                .withMailTo("support@scala.network")
                                .withSubject("Scala Miner App Crash Report")
                                .build()
                )
        );

        ACRA.getErrorReporter().setEnabled(Config.read(Config.CONFIG_SEND_DEBUG_INFO, "0").equals("1"));
    }
}