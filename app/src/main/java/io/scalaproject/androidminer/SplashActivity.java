// Copyright (c) 2021 Scala
//
// Please see the included LICENSE file for more information.

package io.scalaproject.androidminer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import io.scalaproject.androidminer.api.ChangelogItem;

public class SplashActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        String configversion = Config.read(Config.CONFIG_KEY_CONFIG_VERSION);
        if(!configversion.equals(Config.version)) {
            Config.clear();
            Config.write(Config.CONFIG_KEY_CONFIG_VERSION, Config.version);
        }

        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }

        setContentView(R.layout.splashscreen);

        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        loadChangelogAsync();

        int millisecondsDelay = 2000;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                String hide_setup_wizard = Config.read(Config.CONFIG_HIDE_SETUP_WIZARD);

                if (hide_setup_wizard.isEmpty()) {
                    startActivity(new Intent(SplashActivity.this, WizardHomeActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }

                finish();
            }
        }, millisecondsDelay);
    }

    public void loadChangelogAsync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        MainActivity.changeLogRetries = 0;
        MainActivity.isChangelogLoaded = false;

        executor.execute(() -> {
            MainActivity.allChangelogItems.clear();

            for (int i = 1; i < 100; i++) {
                ChangelogItem changelogItem = new ChangelogItem();
                String strChangelogFile = Config.URL_CHANGELOG_DIRECTORY + i + ".txt";

                if (Tools.isURLReachable(strChangelogFile)) {
                    try {
                        changelogItem.mVersion = i;

                        URL url = new URL(strChangelogFile);
                        HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
                        InputStream in = uc.getInputStream();

                        BufferedReader br = new BufferedReader(new InputStreamReader(in));

                        String line;
                        while ((line = br.readLine()) != null) {
                            changelogItem.mChanges.add(line);
                        }

                        MainActivity.allChangelogItems.add(changelogItem);

                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                } else {
                    break;
                }
            }

            MainActivity.isChangelogLoaded = true;
        });
    }
}