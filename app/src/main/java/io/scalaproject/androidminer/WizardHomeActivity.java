// Copyright (c) 2021 Scala
//
// Please see the included LICENSE file for more information.

package io.scalaproject.androidminer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class WizardHomeActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }

        setContentView(R.layout.fragment_wizard_home);

        View view = findViewById(android.R.id.content).getRootView();

        String sDisclaimerText = getResources().getString(R.string.disclaimer_agreement);
        String sDisclaimer = getResources().getString(R.string.disclaimer);

        SpannableString ss = getSpannableString(sDisclaimerText, sDisclaimer);

        TextView tvDisclaimer = view.findViewById(R.id.disclaimer);
        tvDisclaimer.setText(ss);
        tvDisclaimer.setMovementMethod(LinkMovementMethod.getInstance());
        tvDisclaimer.setLinkTextColor(getResources().getColor(R.color.c_blue));
        tvDisclaimer.setHighlightColor(Color.TRANSPARENT);
    }

    @NonNull
    private SpannableString getSpannableString(String sDisclaimerText, String sDisclaimer) {
        SpannableString ss = new SpannableString(sDisclaimerText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                showDisclaimer();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        int iStart = sDisclaimerText.indexOf(sDisclaimer);
        int iEnd = iStart + sDisclaimer.length();
        ss.setSpan(clickableSpan, iStart, iEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public void onEnterAddress(View view) {
        startActivity(new Intent(WizardHomeActivity.this, WizardAddressActivity.class));
    }

    public void onCreateWallet(View view) {
        startActivity(new Intent(WizardHomeActivity.this, VaultActivity.class));
    }

    private void showDisclaimer() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.disclaimer);
        dialog.setTitle("Disclaimer");
        dialog.setCancelable(false);

        Button btnOK = dialog.findViewById(R.id.btnAgree);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.write(Config.CONFIG_DISCLAIMER_AGREED, "1");
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}