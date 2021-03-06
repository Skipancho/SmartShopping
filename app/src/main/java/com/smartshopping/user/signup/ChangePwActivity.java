package com.smartshopping.user.signup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.smartshopping.R;
import com.smartshopping.request.ChangePwRequest;

import org.json.JSONObject;

public class ChangePwActivity extends AppCompatActivity {
    private String userID;
    private EditText pw_edit, pwChecker;
    private TextView pw_msg;
    private boolean pwCheck = false;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);
        userID = getIntent().getStringExtra("userID");
        pw_edit = findViewById(R.id.new_pw_edit);
        pwChecker = findViewById(R.id.pw_check_edit);
        pw_msg = findViewById(R.id.pw_msg);

        pwChecker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pw = pw_edit.getText().toString();
                String pwC = pwChecker.getText().toString();
                if(pw.equals(pwC)){
                    pw_msg.setVisibility(View.INVISIBLE);
                    pwCheck = true;
                }else{
                    pw_msg.setVisibility(View.VISIBLE);
                    pwCheck = false;
                }
            }
        });

        final Button change_pw_btn = findViewById(R.id.pw_change_btn);
        change_pw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pw = pw_edit.getText().toString();
                if(!pwCheck){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePwActivity.this);
                    dialog = builder.setMessage("??????????????? ??????????????????.")
                            .setNegativeButton("??????",null)
                            .create();
                    dialog.show();
                    return;
                }
                if(pw.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePwActivity.this);
                    dialog = builder.setMessage("??? ??????????????? ??????????????????.")
                            .setNegativeButton("??????",null)
                            .create();
                    dialog.show();
                    return;
                }
                ChangePW(pw);
            }
        });
    }
    private void ChangePW(String pw){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        Toast.makeText(ChangePwActivity.this,"???????????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePwActivity.this);
                        dialog = builder.setMessage("?????? ??????????????????.")
                                .setNegativeButton("?????? ??????",null)
                                .create();
                        dialog.show();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        ChangePwRequest request = new ChangePwRequest(userID,pw,responseListener);
        RequestQueue queue = Volley.newRequestQueue(ChangePwActivity.this);
        queue.add(request);
    }
}