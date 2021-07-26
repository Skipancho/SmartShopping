package com.smartshopping;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;
    private String userID;
    private String userPassword;
    private EditText idText, passwordText;
    private CheckBox autoLogin;
    private SharedPreferences loginInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idText = findViewById(R.id.idText);
        passwordText = findViewById(R.id.passwordText);
        autoLogin = findViewById(R.id.autoLogin);

        loginInfo = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        userID = loginInfo.getString("inputID",null);
        userPassword = loginInfo.getString("inputPassword",null);

        if(userID != null && userPassword != null){
            idText.setText(userID);
            passwordText.setText(userPassword);
            autoLogin.setChecked(true);
            UserLogin(userID,userPassword);
        }

        Button loginBtn = findViewById(R.id.loginBtn);
        TextView registerBtn = findViewById(R.id.registerBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userID = idText.getText().toString();
                userPassword = passwordText.getText().toString();
                UserLogin(userID,userPassword);
            }
        });
       registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
        TextView findIDBtn = findViewById(R.id.findIDBtn);
        findIDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindIdActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
        TextView findPWBtn = findViewById(R.id.findPWBtn);
        findPWBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindPwActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
    }

    public void UserLogin(String id, String pw){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        Toast.makeText(LoginActivity.this,"로그인 성공",Toast.LENGTH_SHORT).show();
                        String userName = jsonResponse.getString("name");
                        String phoneNo = jsonResponse.getString("phoneNum");
                        String nickName = jsonResponse.getString("nickName");
                        if(autoLogin.isChecked()){
                            SharedPreferences.Editor editor= loginInfo.edit();
                            editor.putString("inputID",userID);
                            editor.putString("inputPassword",userPassword);
                            editor.commit();
                        }else{
                            SharedPreferences.Editor editor= loginInfo.edit();
                            editor.clear();
                            editor.commit();
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userID",userID);
                        intent.putExtra("phoneNum",phoneNo);
                        intent.putExtra("name",userName);
                        intent.putExtra("nickName",nickName);
                        LoginActivity.this.startActivity(intent);
                        finish();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
        LoginRequest loginRequest = new LoginRequest(userID,userPassword,responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
    }
    @Override
    protected void onStop(){
        super.onStop();
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
}