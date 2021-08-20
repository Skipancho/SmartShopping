package com.smartshopping;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class WithdrawalActivity extends AppCompatActivity {
    private MainActivity mainA = (MainActivity)MainActivity.main;
    private MyPageActivity myPageA = (MyPageActivity)MyPageActivity.mypage;
    private User user = MainActivity.user;
    private EditText pw_edit;
    private CheckBox checkBox;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        checkBox = findViewById(R.id.check_box);
        pw_edit = findViewById(R.id.pw_edit);

        findViewById(R.id.withdrawal_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pw = pw_edit.getText().toString();
                if(!checkBox.isChecked()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawalActivity.this);
                    dialog = builder.setMessage("체크박스를 확인해주세요.")
                            .setNegativeButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }
                if(pw.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawalActivity.this);
                    dialog = builder.setMessage("비밀번호를 확인해주세요.")
                            .setNegativeButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }
                PwCheck(pw);
            }
        });
    }
    private void PwCheck(String pw){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawalActivity.this);
                        dialog = builder.setMessage("회원 탈퇴를 진행하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Withdrawal_user();
                                    }
                                })
                                .setNegativeButton("취소",null)
                                .create();
                        dialog.show();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawalActivity.this);
                        dialog = builder.setMessage("비밀번호를 확인해주세요.")
                                .setNegativeButton("다시 시도",null)
                                .create();
                        dialog.show();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        LoginRequest loginRequest = new LoginRequest(user.getUserID(),pw,responseListener);
        RequestQueue queue = Volley.newRequestQueue(WithdrawalActivity.this);
        queue.add(loginRequest);
    }

    private void Withdrawal_user(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        Toast.makeText(WithdrawalActivity.this,"회원 탈퇴가 완료되었습니다.",Toast.LENGTH_SHORT);

                        SharedPreferences loginInfo = getSharedPreferences("setting", Activity.MODE_PRIVATE);//기기에 저장된 회원 아이디/비번 삭제
                        SharedPreferences.Editor editor = loginInfo.edit();
                        editor.clear();
                        editor.commit();

                        WithdrawalActivity.this.startActivity(new Intent(WithdrawalActivity.this,LoginActivity.class));
                        mainA.finish();
                        myPageA.finish();
                        finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        WithdrawalRequest request = new WithdrawalRequest(user.getUserID(),responseListener);
        RequestQueue queue = Volley.newRequestQueue(WithdrawalActivity.this);
        queue.add(request);
    }

}