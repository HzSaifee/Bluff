package com.akatsuki.bluff;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 1700;
    ImageView iv1;
    //AnimationDrawable Anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iv1 = (ImageView) findViewById(R.id.splash_image);
        iv1.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable ivAnimation = (AnimationDrawable) iv1.getBackground();
        ivAnimation.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}