package com.smartshopping.main.review;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.smartshopping.R;
import com.smartshopping.main.MainActivity;
import com.smartshopping.request.ReviewRequest;
import com.smartshopping.request.ReviewUpdateRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewActivity extends AppCompatActivity {
    private String userID, pCode, pName;
    private ArrayAdapter adapter;
    private Spinner spinner;
    private EditText review_edit;
    private TextView txt_length;
    private int pkey;
    private boolean isCreated = true;
    private CheckBox agree_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        userID = MainActivity.user.getUserID();
        pCode = getIntent().getStringExtra("pCode");
        pName = getIntent().getStringExtra("pName");
        pkey = getIntent().getIntExtra("pkey",-1);
        spinner = (Spinner) findViewById(R.id.mySpinner);
        agree_check = findViewById(R.id.agree_check);
        review_edit = findViewById(R.id.reviewText);
        TextView pNameText = findViewById(R.id.pName);

        adapter = ArrayAdapter.createFromResource(this,R.array.score,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        pNameText.setText(pName);
        findViewById(R.id.finishBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button reviewBtn = findViewById(R.id.reviewBtn);
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String score = spinner.getSelectedItem().toString();
                String rText = review_edit.getText().toString();
                if(rText.equals("")){
                    Toast.makeText(ReviewActivity.this,"????????? ????????? ?????????.",Toast.LENGTH_SHORT).show();
                    return;
                }else if(!agree_check.isChecked()){
                    Toast.makeText(ReviewActivity.this,"?????? ????????? ????????? ?????????.",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(isCreated){
                        reviewRegist(pCode,userID,score,rText);
                    }else{
                        ReviewUpdate(score,rText);
                    }
                }
            }
        });

        if(pkey == -1){
            String rText = getIntent().getStringExtra("rText");
            int score = getIntent().getIntExtra("score",5);
            int pos = 5 - score;
            review_edit.setText(rText);
            spinner.setSelection(pos);
            isCreated = false;
            reviewBtn.setText("??????");
        }

        txt_length = findViewById(R.id.txt_len);
        review_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int t_len = review_edit.getText().toString().length();
                txt_length.setText(t_len+"/200");
            }
        });
    }
    private void reviewRegist(String pCode, String userID, String scoreStr, String rText){
        int score = scoreStr.length();
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
        String rDate = mFormat.format(date);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        Toast.makeText(ReviewActivity.this,"?????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        ReviewRequest request = new ReviewRequest(pCode,userID,score,rText,rDate,pkey,responseListener);
        RequestQueue queue = Volley.newRequestQueue(ReviewActivity.this);
        queue.add(request);
    }
    private void ReviewUpdate(String scoreStr, String rText){
        int rId = getIntent().getIntExtra("rId",-1);
        if(rId == -1){
            Toast.makeText(ReviewActivity.this,"??????",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int score = scoreStr.length();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        Toast.makeText(ReviewActivity.this,"?????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        ReviewUpdateRequest request = new ReviewUpdateRequest(rId,score,rText,responseListener);
        RequestQueue queue = Volley.newRequestQueue(ReviewActivity.this);
        queue.add(request);
    }
}