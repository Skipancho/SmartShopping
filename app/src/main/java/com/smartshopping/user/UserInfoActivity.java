package com.smartshopping.user;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.smartshopping.user.signup.ChangePwActivity;
import com.smartshopping.main.MainActivity;
import com.smartshopping.R;
import com.smartshopping.request.UpdateRequest;
import com.smartshopping.request.ValidateRequest;

import org.json.JSONObject;

public class UserInfoActivity extends AppCompatActivity {
    private User user = MainActivity.user;
    private TextView idText;
    private EditText nameText,nickText,phoneNoText;
    private AlertDialog dialog;
    private boolean nickValidate = false;
    private Button val_nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        idText = findViewById(R.id.idText);
        nameText = findViewById(R.id.nameText);
        nickText = findViewById(R.id.nickNameText);
        phoneNoText = findViewById(R.id.phoneNoText);
        val_nick = findViewById(R.id.val_nick_Btn);

        idText.setText(user.getUserID());
        nameText.setText(user.getName());
        nickText.setText(user.getNickName());
        phoneNoText.setText(user.getPhoneNum());

        findViewById(R.id.finishBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        val_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newNickName = nickText.getText().toString();
                if(newNickName.equals("")){
                    Toast.makeText(UserInfoActivity.this,"빈칸을 확인하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                ValidateNick(newNickName);
            }
        });
        //회원정보수정
        findViewById(R.id.updateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameText.getText().toString();
                String phoneNum = phoneNoText.getText().toString();
                String nickName = nickText.getText().toString();
                if(name.equals("")||phoneNum.equals("")){
                    Toast.makeText(UserInfoActivity.this,"빈칸을 확인하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }else if(!nickValidate){
                    Toast.makeText(UserInfoActivity.this,"닉네임 중복 체크를 해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    UpdateUserInfo(name,phoneNum,nickName);
                }
            }
        });
        //회원 탈퇴
        findViewById(R.id.withdrawal_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfoActivity.this.startActivity(new Intent(UserInfoActivity.this, WithdrawalActivity.class));
                finish();
            }
        });
        //비밀번호 변경
        findViewById(R.id.pw_change_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, ChangePwActivity.class);
                intent.putExtra("userID",user.getUserID());
                startActivity(intent);
            }
        });
    }

    public void ValidateNick(String nickName){
        if(nickName.equals(user.getNickName())){
            AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
            dialog = builder.setMessage("사용 가능한 닉네임 입니다.")
                    .setPositiveButton("사용", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            nickText.setEnabled(false);
                            nickValidate = true;
                            nickText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                            val_nick.setBackgroundColor(getResources().getColor(R.color.colorGray));
                        }
                    })
                    .setNegativeButton("취소",null)
                    .create();
            dialog.show();
            return;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
                        dialog = builder.setMessage("사용 가능한 닉네임 입니다.")
                                .setPositiveButton("사용", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        nickText.setEnabled(false);
                                        nickValidate = true;
                                        nickText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                        val_nick.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                    }
                                })
                                .setNegativeButton("취소",null)
                                .create();
                        dialog.show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
                        dialog = builder.setMessage("사용할 수 없는 닉네임 입니다.")
                                .setNegativeButton("확인",null)
                                .create();
                        dialog.show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        ValidateRequest validateRequest = new ValidateRequest(nickName,"nickName",responseListener);
        RequestQueue queue = Volley.newRequestQueue(UserInfoActivity.this);
        queue.add(validateRequest);
    }

    public void UpdateUserInfo(final String name, final String phoneNum, final String nickName){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
                        dialog = builder.setMessage("회원 정보 변경이 완료되었습니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .create();
                        dialog.show();
                        MainActivity.user.setName(name);
                        MainActivity.user.setPhoneNum(phoneNum);
                        MainActivity.user.setNickName(nickName);
                        finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        UpdateRequest request = new UpdateRequest(user.getUserID(),name,phoneNum,nickName,responseListener);
        RequestQueue queue = Volley.newRequestQueue(UserInfoActivity.this);
        queue.add(request);

    }
}