package com.smartshopping.user.signin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.smartshopping.main.MainActivity;
import com.smartshopping.R;
import com.smartshopping.request.LoginRequest;
import com.smartshopping.user.signup.RegisterActivity;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;
    private String userID;
    private String userPassword;
    private EditText idText, passwordText;
    private CheckBox autoLogin;
    private SharedPreferences loginInfo;
    private String token_key;

    protected InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(charSequence).matches()) {
                return "";
            }
            return null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        token_key = CreateTokenKey(10);
        idText = findViewById(R.id.idText);
        passwordText = findViewById(R.id.passwordText);
        autoLogin = findViewById(R.id.autoLogin);
        Button loginBtn = findViewById(R.id.loginBtn);
        TextView registerBtn = findViewById(R.id.registerBtn);
        TextView findIDBtn = findViewById(R.id.findIDBtn);
        TextView findPWBtn = findViewById(R.id.findPWBtn);

        //idText.setFilters(new InputFilter[]{filter});

        loginInfo = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        userID = loginInfo.getString("inputID",null);
        userPassword = loginInfo.getString("inputPassword",null);

        boolean isKilled = getIntent().getBooleanExtra("kill",false);

        if(userID != null && userPassword != null){
            idText.setText(userID);
            passwordText.setText(userPassword);
            autoLogin.setChecked(true);
            if(!isKilled)
                UserLogin(userID,userPassword);
        }

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

        findIDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindIdActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        findPWBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindPwActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
    }

    public void UserLogin(final String id, final String pw){
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
                        String token = jsonResponse.getString("token");
                        if(autoLogin.isChecked()){
                            SharedPreferences.Editor editor= loginInfo.edit();
                            editor.putString("inputID",id);
                            editor.putString("inputPassword",pw);
                            editor.commit();
                        }else{
                            SharedPreferences.Editor editor= loginInfo.edit();
                            editor.clear();
                            editor.commit();
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userID",id);
                        intent.putExtra("phoneNum",phoneNo);
                        intent.putExtra("name",userName);
                        intent.putExtra("nickName",nickName);
                        intent.putExtra("token",token+token_key);
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
        LoginRequest loginRequest = new LoginRequest(id,pw,token_key,responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
    }

    private String CreateTokenKey(int size){
        if(size > 0) {
            char[] tmp = new char[size];
            for(int i=0; i<tmp.length; i++) {
                int div = (int) Math.floor( Math.random() * 3 );

                if(div == 0) { // 0이면 숫자로
                    tmp[i] = (char) (Math.random() * 10 + '0') ;
                }else if(div == 1) { //1이면 알파벳 대문자
                    tmp[i] = (char) (Math.random() * 26 + 'A') ;
                }else{ //2이면 알파벳 소문자
                    tmp[i] = (char) (Math.random() * 26 + 'a') ;
                }
            }
            return new String(tmp);
        }
        return "err";
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