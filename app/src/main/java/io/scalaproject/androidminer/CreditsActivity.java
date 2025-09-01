// Copyright (c) 2021 Scala
//
// Please see the included LICENSE file for more information.

package io.scalaproject.androidminer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsActivity;

public class CreditsActivity extends LibsActivity {
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        LibsBuilder builder = new LibsBuilder();
        builder.setAboutAppName(getResources().getString(R.string.app_name));
        builder.setAboutShowIcon(true);
        builder.setAboutShowVersion(true);

        setIntent(builder.intent(this));

        super.onCreate(savedInstanceState);

        // Handle Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.backgound_toolbar));
        toolbar.setTitle(R.string.credits);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.Toolbar_Title);

        // Set action bar height from theme
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
            layoutParams.height = actionBarHeight;
            toolbar.setLayoutParams(layoutParams);
        }

        setSupportActionBar(toolbar);
    }
}