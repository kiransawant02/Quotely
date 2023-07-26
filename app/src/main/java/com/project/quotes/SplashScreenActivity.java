package com.project.quotes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIMER=3000;
    private ImageView appImage;
    private TextView poweredName;
    Animation sideAnim,bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        appImage=findViewById(R.id.splash_image);
        poweredName=findViewById(R.id.splash_powered);

        sideAnim= AnimationUtils.loadAnimation(this,R.anim.side_anim);
        bottomAnim=AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        appImage.setAnimation(sideAnim);
        poweredName.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreenActivity.this,MainActivity.class);
/*
                Pair[] pairs=new Pair[1];
                pairs[0]=new Pair<View,String>(appImage,"logo_image");

                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(SplashScreenActivity.this,pairs);
                startActivity(intent,activityOptions.toBundle());
                finish();
                */
                startActivity(intent);
                finish();
            }
        },SPLASH_TIMER);
    }
}