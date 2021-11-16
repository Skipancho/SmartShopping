package com.smartshopping.mypage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smartshopping.main.MainActivity;
import com.smartshopping.user.signin.PasswordPop;
import com.smartshopping.R;
import com.smartshopping.mypage.statistics.StatisticsActivity;
import com.smartshopping.intro.TutorialActivity;
import com.smartshopping.user.User;
import com.smartshopping.user.signin.LoginActivity;

public class MyPageActivity extends AppCompatActivity {
    public static Activity mypage;
    private User user = MainActivity.user;
    private MainActivity mainA = (MainActivity) MainActivity.main;
    private AlertDialog dialog;
    private TextView idText,nickText;

    @Override
    protected void onRestart() {
        super.onRestart();
        nickText.setText(user.getNickName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        mypage = MyPageActivity.this;
        idText = findViewById(R.id.myPageID);
        nickText = findViewById(R.id.myPageNickname);
        idText.setText(user.getUserID());
        nickText.setText(user.getNickName());
        Button logout_btn = findViewById(R.id.logoutBtn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        Button userInfoBtn = findViewById(R.id.userInfoBtn);
        userInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MyPageActivity.this.startActivity(new Intent(MyPageActivity.this, PasswordPop.class));
            }
        });
        Button purInqBtn = findViewById(R.id.pur_inq_btn);
        purInqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPageActivity.this.startActivity(new Intent(MyPageActivity.this, PurchaseListInqury.class));
            }
        });
        Button chart_btn = findViewById(R.id.chart_btn);
        chart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPageActivity.this.startActivity(new Intent(MyPageActivity.this, StatisticsActivity.class));
            }
        });

        findViewById(R.id.tutorial_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPageActivity.this.startActivity(new Intent(MyPageActivity.this, TutorialActivity.class));
            }
        });

    }

    void LogOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
        dialog = builder.setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences loginInfo = getSharedPreferences("setting", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = loginInfo.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                        MyPageActivity.this.startActivity(intent);
                        mainA.finish();
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_none,R.anim.anim_right_out);
    }
}