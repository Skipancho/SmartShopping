package com.smartshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class PurchaseListInqury extends AppCompatActivity {
    private User user = MainActivity.user;
    private ListView purList;
    private List<PurchaseProduct> purchaseProducts = new ArrayList<>();
    private PurchaseAdapter adapter;
    @Override
    protected void onRestart() {
        super.onRestart();
        purchaseProducts.clear();
        new BackgroundTask().execute();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list_inqury);
        purList = findViewById(R.id.pur_list);
        adapter = new PurchaseAdapter(this,purchaseProducts);
        purList.setAdapter(adapter);

        new BackgroundTask().execute();

        purList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //리뷰 쓰기
                String pCode = purchaseProducts.get(i).getpCode();
                String pName = purchaseProducts.get(i).getpName();
                int pkey = purchaseProducts.get(i).getpKey();
                int review = purchaseProducts.get(i).getIsReviewed();
                if(review == 0) {
                    Intent intent = new Intent(PurchaseListInqury.this, ReviewActivity.class);

                    intent.putExtra("pCode", pCode);
                    intent.putExtra("pName", pName);
                    intent.putExtra("pkey",pkey);
                    PurchaseListInqury.this.startActivity(intent);
                }
            }
        });
    }

    class BackgroundTask extends AsyncTask<Void,Void,String> {

        String target;
        @Override
        protected void onPreExecute(){
            try {
                target="https://ctg1770.cafe24.com/SC/S_C_PurchaseList.php?userID="+ URLEncoder.encode(user.getUserID(),"UTF-8");
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
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            try{
                String pName, pCode, bDate;
                int price, amount, pkey,review;
                int count = 0;
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                while(count<jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    pName = object.getString("pName");
                    pCode = object.getString("pCode");
                    bDate = object.getString("bDate");
                    price = object.getInt("price");
                    amount = object.getInt("amount");
                    review = object.getInt("review");
                    pkey = object.getInt("pKey");
                    Date bd = mFormat.parse(bDate);
                    long gap = date.getTime() - bd.getTime();
                    gap = gap/(24*60*60*1000);
                    PurchaseProduct p;
                    if(gap > 15){
                        p = new PurchaseProduct(pCode,pName,price,amount,bDate,1,pkey);
                    }else{
                        p = new PurchaseProduct(pCode,pName,price,amount,bDate,review,pkey);
                    }
                    purchaseProducts.add(p);
                    adapter.notifyDataSetChanged();
                    count++;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}