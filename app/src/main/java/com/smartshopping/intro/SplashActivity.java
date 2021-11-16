package com.smartshopping.intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.smartshopping.user.signin.LoginActivity;
import com.smartshopping.R;

public class SplashActivity extends AppCompatActivity {
    private ImageView logo_img;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo_img = findViewById(R.id.logo_img);
        animationDrawable = (AnimationDrawable) logo_img.getBackground();
        animationDrawable.start();
        animationDrawable.setOneShot(true);
        Thread mThread = new Thread(){
            @Override
            public void run() {
                int i = 0;
                while (i<2500){
                      try {
                          Thread.sleep(100);
                          i+=100;
                      }catch (Exception e){
                          e.printStackTrace();
                      }
                }
                animationDrawable.stop();
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        mThread.start();
    }
}