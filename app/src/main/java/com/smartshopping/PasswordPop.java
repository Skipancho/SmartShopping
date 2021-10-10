package com.smartshopping;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class PasswordPop extends AppCompatActivity {

    private final String userId = MainActivity.user.getUserID();
    private EditText pwText;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_pop);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*0.9),(int)(height*0.3));

        Token_Check(MainActivity.myToken);

        pwText = findViewById(R.id.pw_edit_text);
        Button pw_checkBtn = findViewById(R.id.pw_checkBtn);
        pw_checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pw = pwText.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                startActivity(new Intent(PasswordPop.this,UserInfoActivity.class));
                                finish();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PasswordPop.this);
                                dialog = builder.setMessage("다시 확인해주세요.")
                                        .setNegativeButton("다시 시도",null)
                                        .create();
                                dialog.show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userId,pw,responseListener);
                RequestQueue queue = Volley.newRequestQueue(PasswordPop.this);
                queue.add(loginRequest);
            }
        });
    }
    private void Token_Check(String token){

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(!success){
                        AlertDialog dialog;
                        AlertDialog.Builder builder = new AlertDialog.Builder(PasswordPop.this);
                        dialog = builder.setMessage("다른 기기에서의 로그인이 감지되었습니다.")
                                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(PasswordPop.this, LoginActivity.class);
                                        MainActivity.main.finish();
                                        MyPageActivity.mypage.finish();
                                        intent.putExtra("kill",true);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .create();
                        dialog.show();

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        TokenRequest request = new TokenRequest(MainActivity.user.getUserID(),token,responseListener);
        RequestQueue queue = Volley.newRequestQueue(PasswordPop.this);
        queue.add(request);
    }
}