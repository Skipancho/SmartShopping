package com.smartshopping;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class FindPwActivity extends AppCompatActivity {
    private Spinner spinner;
    private ArrayAdapter adapter;
    private EditText name_edit, phone_edit,id_edit,answer_edit;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);
        spinner = findViewById(R.id.mySpinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.PWask,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        name_edit = findViewById(R.id.name_edit);
        phone_edit = findViewById(R.id.phoneNo_edit);
        id_edit = findViewById(R.id.id_edit);
        answer_edit = findViewById(R.id.answer_edit);

        findViewById(R.id.find_pw_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_edit.getText().toString();
                String phone = phone_edit.getText().toString();
                String id = id_edit.getText().toString();
                String pAsk = spinner.getSelectedItem().toString();
                String answer = answer_edit.getText().toString();

                if(name.equals("")||phone.equals("")||id.equals("")||answer.equals("")){
                    Toast.makeText(FindPwActivity.this,"빈칸을 확인해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                FindPw(name,phone,id,pAsk,answer);
            }
        });

        findViewById(R.id.finishBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void FindPw(String name, String phone, final String id, String pAsk, String answer){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success){
                        Intent intent = new Intent(FindPwActivity.this, ChangePwActivity.class);
                        intent.putExtra("userID",id);
                        FindPwActivity.this.startActivity(intent);
                        finish();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FindPwActivity.this);
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
        FindPwRequest request = new FindPwRequest(name,phone,id,pAsk,answer,responseListener);
        RequestQueue queue = Volley.newRequestQueue(FindPwActivity.this);
        queue.add(request);
    }
}