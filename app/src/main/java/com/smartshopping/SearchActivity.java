package com.smartshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class SearchActivity extends AppCompatActivity {

    private ListView search_lv;
    private GridView search_gv;
    private String searchText="";
    private List<SearchProduct> searchList = new ArrayList<>();
    private GridAdapter gridAdapter;
    private Search_ProductAdapter listAdapter;
    private Button eraze_btn;
    private EditText search_edit;
    private LinearLayout after_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        after_search = findViewById(R.id.search_layout);
        search_lv = findViewById(R.id.search_list);
        search_gv = findViewById(R.id.search_grid);
        search_edit = findViewById(R.id.search_edit);
        eraze_btn = findViewById(R.id. erase_btn);

        gridAdapter = new GridAdapter(SearchActivity.this,searchList);
        listAdapter = new Search_ProductAdapter(SearchActivity.this,searchList);

        search_gv.setAdapter(gridAdapter);
        search_lv.setAdapter(listAdapter);

        search_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this,ProductPop.class);
                String searchText = searchList.get(i).getpCode();
                if(searchText.equals("")){
                    return;
                }else{
                    intent.putExtra("searchText",searchText);
                    intent.putExtra("mode","check");
                    SearchActivity.this.startActivity(intent);
                }
            }
        });

        Button search_btn = findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText = search_edit.getText().toString();
                searchList.clear();
                new ProductSearching().execute();
                after_search.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        eraze_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_edit.setText("");
                after_search.setVisibility(View.GONE);
            }
        });


        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String txt = search_edit.getText().toString();
                if(txt.length() >= 1){
                    eraze_btn.setVisibility(View.VISIBLE);
                }else{
                    eraze_btn.setVisibility(View.GONE);
                    after_search.setVisibility(View.GONE);
                }
            }
        });


    }

    class ProductSearching extends AsyncTask<Void,Void,String> {

        String target;
        @Override
        protected void onPreExecute(){
            try {
                target="https://ctg1770.cafe24.com/SC/S_C_ProductList.php?mode=tag&searchText="+ URLEncoder.encode(searchText,"UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = httpsURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp=bufferedReader.readLine()) != null){
                    stringBuilder.append(temp+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpsURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate();
        }
        @Override
        public void onPostExecute(String result){
            try{
                String pCode, pName, info;
                int price,amount;
                int count = 0;
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                while(count<jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    pCode = object.getString("pCode");
                    pName = object.getString("pName");
                    info = object.getString("info");
                    price = object.getInt("price");
                    amount = object.getInt("amount");
                    SearchProduct p = new SearchProduct(pCode,pName,price,amount,info);
                    searchList.add(p);
                    listAdapter.notifyDataSetChanged();
                    gridAdapter.notifyDataSetChanged();
                    count++;
                }
                searchText = "";
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}