package com.smartshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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