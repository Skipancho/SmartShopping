package com.smartshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
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
    ImageView icon_image;
    ImageView logo_text;
    ImageView random_image;
    Bitmap bitmap;
    ProgressDialog mDialog;
    Thread splashTread;
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Animation bounds = AnimationUtils.loadAnimation(this,R.anim.splash_anim_bounds);
        Animation fade_in = AnimationUtils.loadAnimation(this,R.anim.splash_anim_fade_in);
        mDialog = new ProgressDialog(this);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        random_image = findViewById(R.id.random_image);
        Thread mThread = new Thread(){
            @Override
            public void run(){
                try {
                    URL url = new URL("https://source.unsplash.com/random");
                    HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.connect();
                    InputStream inputStream = con.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mDialog.show();
        mThread.start();
        try {
            mThread.join();
            random_image.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 2000) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashActivity.this,
                            LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashActivity.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashActivity.this.finish();
                }

            }
        };
        splashTread.start();




        /*icon_image = findViewById(R.id.logo_image);
        logo_text = findViewById(R.id.logo_text);
        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        icon_image.startAnimation(bounds);
        logo_text.startAnimation(fade_in);*/
    }



}