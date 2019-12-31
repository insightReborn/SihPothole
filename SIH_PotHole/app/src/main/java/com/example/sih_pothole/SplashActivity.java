package com.example.sih_pothole;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.rbddevs.splashy.Splashy;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashy();
    }

    private void splashy() {
        new Splashy(this)
                .setLogo(R.drawable.splashy)
                .setTitle("Splashy")
                .setTitleColor("#FFFFFF")
                .setSubTitle("Splash screen made easy")
                .setProgressColor(R.color.white)
                .setBackgroundResource(R.color.colorPrimaryDark)
                .setFullScreen(true)
                .setTime(5000)
                .show();
    }

}
