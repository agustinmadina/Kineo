package com.ownhealth.kineo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by Agustin Madina on 21/09/18.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            goToHomeScreen();
            finish();
        }, 1000);
    }

    private void goToHomeScreen() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
