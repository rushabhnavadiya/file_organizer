package com.manthanvanani.fileorganizer.Screens.Splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.manthanvanani.fileorganizer.BuildConfig;
import com.manthanvanani.fileorganizer.Constants.Utills;
import com.manthanvanani.fileorganizer.R;
import com.manthanvanani.fileorganizer.Screens.Home.HomeScreen;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

import tyrantgit.explosionfield.ExplosionField;
import tyrantgit.explosionfield.Utils;

public class SplashScreen extends AppCompatActivity {
    private ExplosionField mExplosionField;
    ImageView mImgSplash;
    TextView mTxtSplash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AudienceNetworkAds.initialize(this);
        if (BuildConfig.DEBUG){
            AdSettings.setTestMode(true);
        }
        setContentView(R.layout.activity_splash_screen);
        mExplosionField = ExplosionField.attach2Window(this);
        mImgSplash = findViewById(R.id.mImgSplash);
        mTxtSplash = findViewById(R.id.mTxtSplash);
        mImgSplash.getLayoutParams().width = (Utills.getDeviceWidth(this)/3);
        mImgSplash.getLayoutParams().height = (Utills.getDeviceWidth(this)/3);
        YoYo.with(Techniques.FadeIn)
                .duration(300)
                .playOn(mTxtSplash);
        YoYo.with(Techniques.FadeIn)
                .duration(700)
                .playOn(mImgSplash);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
//                mExplosionField.explode(mTxtSplash);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        startActivity(new Intent(SplashScreen.this, HomeScreen.class));
                        finish();
                    }
                }, 150);
            }
        }, 1150);
    }
}