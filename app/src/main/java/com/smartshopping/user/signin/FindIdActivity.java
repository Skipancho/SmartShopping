package com.smartshopping.user.signin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.smartshopping.R;
import com.smartshopping.request.FindIdRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class FindIdActivity extends AppCompatActivity {
    private EditText name_edit, phone_edit;
    private String result="";
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);
        name_edit = findViewById(R.id.name_edit);
        phone_edit = findViewById(R.id.phoneNo_edit);

        findViewById(R.id.find_id_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_edit.getText().toString();
                String phone = phone_edit.getText().toString();
                FindID(name,phone);
            }
        });
        findViewById(R.id.finishBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    public void FindID(String name, String phoneNum){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("response");
                    int count = 0;
                    String userID;
                    result = "";
                    while(count<jsonArray.length()){
                        JSONObject object = jsonArray.getJSONObject(count);
                        userID = object.getString("userID");
                        if(userID.length()>2){
                            userID = userID.substring(0,userID.length()-2) + "**";
                        }
                        result += userID+"\n";
                        count++;
                    }
                    if(count == 0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(FindIdActivity.this);
                        dialog = builder.setMessage("해당하는 아이디가 없습니다.")
                                .setNegativeButton("확인",null)
                                .create();
                        dialog.show();
                    }else{
                        String msg = "회원님의 아이디는 총 "+ count+"개 입니다.\n\n"+result;
                        AlertDialog.Builder builder = new AlertDialog.Builder(FindIdActivity.this);
                        dialog = builder.setMessage(msg)
                                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        FindIdRequest request = new FindIdRequest(name,phoneNum,responseListener);
        RequestQueue queue = Volley.newRequestQueue(FindIdActivity.this);
        queue.add(request);
    }

}