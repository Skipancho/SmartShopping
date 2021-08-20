package com.smartshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ProductPop extends AppCompatActivity {
    private List<Product_Item> products;
    private Product_Item product;
    private String searchText;
    private TextView pNameText,pPriceText,pAmoutText,pInfoText , no_reivew_text;
    private List<Review_Product> reviewList = new ArrayList<>();
    private ReviewAdapter adapter;
    private ListView reviewListView;
    private String mode = "";
    private boolean isNull = true;
    private Bitmap bitmap;
    private ImageView p_image, review_bar, info_bar;
    private Button info_btn, review_btn;
    private FrameLayout review_frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_pop);
        searchText = getIntent().getStringExtra("searchText");
        mode = getIntent().getStringExtra("mode");
        p_image = findViewById(R.id.p_Image);
        pNameText = findViewById(R.id.p_name);
        pPriceText = findViewById(R.id.p_price);
        pAmoutText = findViewById(R.id.p_amount);
        pInfoText = findViewById(R.id.p_info);
        pInfoText.setMovementMethod(new ScrollingMovementMethod());
        reviewListView = findViewById(R.id.reviewList);
        no_reivew_text = findViewById(R.id.no_review_text);
        info_btn = findViewById(R.id.info_btn);
        review_btn = findViewById(R.id.review_btn);
        info_bar = findViewById(R.id.info_select_bar);
        review_bar = findViewById(R.id.review_select_bar);
        review_frame = findViewById(R.id.review_frame);

        review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //하단 바 변경
                info_btn.setTextColor(getResources().getColor(R.color.black));
                info_bar.setVisibility(View.INVISIBLE);
                review_btn.setTextColor(getResources().getColor(R.color.colorBlue));
                review_bar.setVisibility(View.VISIBLE);

                //상세설명 -> 리뷰로 전환
                pInfoText.setVisibility(View.GONE);
                review_frame.setVisibility(View.VISIBLE);
            }
        });
        info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                review_btn.setTextColor(getResources().getColor(R.color.black));
                review_bar.setVisibility(View.INVISIBLE);
                info_btn.setTextColor(getResources().getColor(R.color.colorBlue));
                info_bar.setVisibility(View.VISIBLE);

                //상세설명 -> 리뷰로 전환
                review_frame.setVisibility(View.GONE);
                pInfoText.setVisibility(View.VISIBLE);
            }
        });



        pNameText.setSelected(true);

        adapter = new ReviewAdapter(this, reviewList);
        reviewListView.setAdapter(adapter);

        new SearchReview().execute();
        new SearchProduct().execute();
        Thread mThread = new Thread(){
            @Override
            public void run(){
                try {
                    URL url = new URL("https://ctg1770.cafe24.com/SC/image/"+searchText+".jpg");
                    HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.connect();

                    InputStream inputStream = con.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        try {
            mThread.join();
            p_image.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        products = new ArrayList<>();

        Button add_Cart_Btn = findViewById(R.id.add_Btn);
        if(mode.equals("cart")){
            add_Cart_Btn.setText("장바구니 담기");
        }else if(mode.equals("check")){
            add_Cart_Btn.setText("체크리스트 추가");
        }else{
            add_Cart_Btn.setVisibility(View.GONE);
        }
        add_Cart_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(product.getAmount() == 0){
                    Toast.makeText(ProductPop.this,"품절된 상품 입니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isNull){
                    return;
                }else{
                    if(mode.equals("cart"))
                        products = MainActivity.productList;
                    else if(mode.equals("check"))
                        products = MainActivity.checkList;

                    boolean checker = true;
                    for(int i = 0; i<products.size();i++){
                        if(products.get(i).getpCode().equals(product.getpCode())){
                            products.get(i).setAmount(products.get(i).getAmount()+1);
                            checker = false;
                        }
                    }
                    if(checker){
                        product.setAmount(1);
                        product.setWhere(mode);
                        products.add(product);
                    }
                    String key = mode + "List";
                    setList(key,products);
                    Toast.makeText(ProductPop.this,"상품 추가 완료",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        Button exitBtn = findViewById(R.id.close_Btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public  void setList(String key, List<Product_Item> productList){
        key = MainActivity.user.getUserID() + key;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        JSONArray jsonArray = new JSONArray();
        Gson gson = new GsonBuilder().create();
        for(int i = 0; i < productList.size();i++){
            String string = gson.toJson(productList.get(i),Product_Item.class);
            jsonArray.put(string);
        }
        if(!productList.isEmpty()){
            editor.putString(key,jsonArray.toString()).commit();
            //System.out.println(jsonArray.toString());
        }else {
            editor.putString(key,null).commit();
            //System.out.println(jsonArray.toString());
        }
    }

    class SearchReview extends AsyncTask<Void,Void,String> {

        String target;
        @Override
        protected void onPreExecute(){
            try {
                target="https://ctg1770.cafe24.com/SC/S_C_ReviewList.php?mode=pCode&find="+ URLEncoder.encode(searchText,"UTF-8");
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
                String pCode, nick, rText, rDate;
                int score,rId;
                int count = 0;
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                while(count<jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    rId = object.getInt("rID");
                    pCode = object.getString("pCode");
                    score = object.getInt("score");
                    nick = object.getString("nickName");
                    rText = object.getString("rText");
                    rDate = object.getString("rDate");

                    Review_Product review = new Review_Product(rId,pCode,score,nick,rText,rDate);
                    reviewList.add(review);

                    adapter.notifyDataSetChanged();
                    count++;
                }
                if(count == 0){
                    no_reivew_text.setVisibility(View.VISIBLE);
                }else{
                    no_reivew_text.setVisibility(View.INVISIBLE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    class SearchProduct extends AsyncTask<Void,Void,String> {

        String target;
        @Override
        protected void onPreExecute(){
            try {
                target="https://ctg1770.cafe24.com/SC/S_C_ProductList.php?mode=pCode&searchText="+ URLEncoder.encode(searchText,"UTF-8");
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
                    product = new Product_Item(pCode,pName,price,amount,mode);
                    pNameText.setText(pName);
                    pPriceText.setText(price+" (원)");
                    pAmoutText.setText(amount+" (개)");
                    pInfoText.setText(info);
                    count++;
                    isNull = false;
                }
                if(count == 0){
                    pInfoText.setText("해당하는 상품이 없습니다.");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}