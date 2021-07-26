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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Random;

public class WithdrawalActivity extends AppCompatActivity {
    private MainActivity mainA = (MainActivity)MainActivity.main;
    private MyPageActivity myPageA = (MyPageActivity)MyPageActivity.mypage;
    private User user = MainActivity.user;
    private String rndmStr;
    private EditText check_edit;
    private CheckBox checkBox;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);
        rndmStr = MakeRandomStr(6);
        checkBox = findViewById(R.id.check_box);
        check_edit = findViewById(R.id.check_text);
        TextView random_text = findViewById(R.id.random_text);
        random_text.setText(rndmStr);
        findViewById(R.id.withdrawal_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkBox.isChecked()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawalActivity.this);
                    dialog = builder.setMessage("체크박스를 확인해주세요.")
                            .setNegativeButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }
                if(!check_edit.getText().toString().equals(rndmStr)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawalActivity.this);
                    dialog = builder.setMessage("문자열이 일치하지 않습니다.")
                            .setNegativeButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }
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
        });


    }

    private String MakeRandomStr(int n){
        String text = "";
        String[] strData = new String[n];
        Random random = new Random();

        for(int i = 0; i < n;i++){
            strData[i] = String.valueOf((char) ((int) (random.nextInt(11171))+44032));
            text += strData[i];
        }

        return text;
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