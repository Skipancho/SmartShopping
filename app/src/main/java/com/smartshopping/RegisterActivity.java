package com.smartshopping;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private AlertDialog dialog;
    private Spinner spinner;
    private boolean idValidate = false, nickValidate = false, pwCheck = false ;
    private EditText idText, passwordText, phoneNoText,nameText,nickNameText,pwAnswerText, pwChecker;
    private Button idValidateBtn,nickValidateBtn;
    private TextView pw_msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        spinner = (Spinner) findViewById(R.id.mySpinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.PWask,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        idText = (EditText) findViewById(R.id.idText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        nameText = (EditText) findViewById(R.id.nameText);
        nickNameText = (EditText) findViewById(R.id.NicknameText);
        phoneNoText = (EditText) findViewById(R.id.phoneNoText);
        pwAnswerText = (EditText) findViewById(R.id.answerText);
        pwChecker = (EditText) findViewById(R.id.pw_checkText);
        pw_msg = (TextView) findViewById(R.id.pw_msg);
        nickValidateBtn =(Button) findViewById(R.id.validateNicknameBtn);
        idValidateBtn = (Button) findViewById(R.id.validateBtn);
        Button registerBtn = (Button)findViewById(R.id.registerBtn);


        idValidateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateID(idText.getText().toString());
            }
        });

        nickValidateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateNick(nickNameText.getText().toString());
            }
        });

        pwChecker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                pw_msg.setVisibility(View.VISIBLE);
                String pw = passwordText.getText().toString();
                String pwC = pwChecker.getText().toString();
                if(pw.equals(pwC)){
                    pw_msg.setText("※ 패스워드가 일치합니다.");
                    pw_msg.setTextColor(getResources().getColor(R.color.green));
                    pwCheck = true;
                }else if(pwC.equals("")){
                    pw_msg.setVisibility(View.INVISIBLE);
                    pwCheck = false;
                }else{
                    pw_msg.setText("※ 패스워드가 일치하지 않습니다.");
                    pw_msg.setTextColor(getResources().getColor(R.color.colorWarning));
                    pwCheck = false;
                }
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                String userPW = passwordText.getText().toString();
                String userPhoneNo = phoneNoText.getText().toString();
                String userName = nameText.getText().toString();
                String userNickname = nickNameText.getText().toString();
                String pwAsk = spinner.getSelectedItem().toString();
                String pwAnswer = pwAnswerText.getText().toString();
                if(!idValidate || !nickValidate){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("중복 체크를 해주세요.")
                            .setNegativeButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }
                if(userID.equals("")||userPW.equals("")||userName.equals("")||userPhoneNo.equals("")||userNickname.equals("")||pwAnswer.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("빈칸을 확인해주세요.")
                            .setNegativeButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }
                if(!pwCheck){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("패스워드를 확인해주세요.")
                            .setNegativeButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }
                User user = new User(userID, userPW,userPhoneNo,userName,userNickname,pwAsk,pwAnswer);
                UserRegistration(user);
            }
        });
    }

    public void ValidateID(String id){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        dialog = builder.setMessage("사용 가능한 아이디 입니다.")
                                .setPositiveButton("확인",null)
                                .create();
                        dialog.show();
                        idText.setEnabled(false);
                        idValidate = true;
                        idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                        idValidateBtn.setBackgroundColor(getResources().getColor(R.color.colorGray));
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        dialog = builder.setMessage("사용할 수 없는 아이디 입니다.")
                                .setNegativeButton("확인",null)
                                .create();
                        dialog.show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        ValidateRequest validateRequest = new ValidateRequest(id,"userID",responseListener);
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(validateRequest);
    }
    public void ValidateNick(String nickName){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        dialog = builder.setMessage("사용 가능한 닉네임 입니다.")
                                .setPositiveButton("확인",null)
                                .create();
                        dialog.show();
                        nickNameText.setEnabled(false);
                        nickValidate = true;
                        nickNameText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                        nickValidateBtn.setBackgroundColor(getResources().getColor(R.color.colorGray));
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(validateRequest);
    }


    public void UserRegistration(User user){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        Toast.makeText(RegisterActivity.this,"회원 가입에 성공하셨습니다.",Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        dialog = builder.setMessage("회원 가입에 실패하였습니다.")
                                .setNegativeButton("확인",null)
                                .create();
                        dialog.show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        RegisterRequest registerRequest = new RegisterRequest(user,responseListener);
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(registerRequest);
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