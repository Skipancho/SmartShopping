package com.smartshopping;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class FindIdActivity extends AppCompatActivity {
    private TextView find_result;
    private EditText name_edit, phone_edit;
    private String result="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);
        find_result = findViewById(R.id.IDview);
        name_edit = findViewById(R.id.name_edit);
        phone_edit = findViewById(R.id.phoneNo_edit);

        findViewById(R.id.find_id_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                find_result.setText("");
                String name = name_edit.getText().toString();
                String phone = phone_edit.getText().toString();
                FindID(name,phone);
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
                    while(count<jsonArray.length()){
                        JSONObject object = jsonArray.getJSONObject(count);
                        userID = object.getString("userID");
                        result += userID+"\n";
                        count++;
                    }
                    if(count == 0){
                        find_result.setText("해당하는 아이디가 없습니다.");
                    }else{
                        find_result.setText("회원님의 아이디는 총 "+ count+"개 입니다.\n");
                        find_result.append(result);
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