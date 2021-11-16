package com.smartshopping.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smartshopping.main.review.ReviewManagement;
import com.smartshopping.product.ProductPop;
import com.smartshopping.R;
import com.smartshopping.product.SearchActivity;
import com.smartshopping.adapter.GridAdapter;
import com.smartshopping.adapter.Search_ProductAdapter;
import com.smartshopping.intro.TutorialActivity;
import com.smartshopping.mypage.MyPageActivity;
import com.smartshopping.product.Product_Item;
import com.smartshopping.product.SearchProduct;
import com.smartshopping.request.TokenRequest;
import com.smartshopping.user.User;
import com.smartshopping.user.signin.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    public static Activity main;
    public static User user;
    public static String myToken = null;
    private BottomNavigationView b_navi;
    private List<SearchProduct> searchList = new ArrayList<>();
    private ListView search_lv;
    private GridView search_gv;
    public static List<Product_Item> productList;
    public static List<Product_Item> checkList;

    private GridAdapter gridAdapter;
    private Search_ProductAdapter listAdapter;
    private LinearLayout main_layout;
    private RelativeLayout fragment;
    private TextView header_text;

    // 검색 모드 변경용 (임시)
    //private boolean isView_1 = false;
    //private EditText search_edit;
    //private Button eraze_btn;

    private TokenThread tokenThread = new TokenThread();
    @Override
    protected void onStop(){
        super.onStop();
        tokenThread.setRunning(false);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        tokenThread = new TokenThread();
        tokenThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = new User(getIntent().getStringExtra("userID"),getIntent().getStringExtra("phoneNum"),getIntent().getStringExtra("name"),getIntent().getStringExtra("nickName"));
        main = MainActivity.this;
        productList = new ArrayList<>();
        checkList = new ArrayList<>();
        checkList = getList("checkList");
        productList = getList("cartList");
        myToken = getIntent().getStringExtra("token");
        SharedPreferences sp = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("first_login",true);
        if(isFirst){
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("first_login",false);
            editor.commit();
            startActivity(new Intent(MainActivity.this, TutorialActivity.class));
        }
        tokenThread.start();

        header_text =findViewById(R.id.header_text);
        main_layout = findViewById(R.id.main_layout);
        fragment = findViewById(R.id.fragment);
        b_navi = findViewById(R.id.bottomNavi);

        search_gv = findViewById(R.id.search_grid);
        search_lv = findViewById(R.id.search_list);

        gridAdapter = new GridAdapter(MainActivity.this,searchList);
        listAdapter = new Search_ProductAdapter(MainActivity.this,searchList);

        search_gv.setAdapter(gridAdapter);
        search_lv.setAdapter(listAdapter);

        search_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, ProductPop.class);
                String searchText = searchList.get(i).getpCode();
                if(searchText.equals("")){
                    return;
                }else{
                    intent.putExtra("searchText",searchText);
                    intent.putExtra("mode","check");
                    MainActivity.this.startActivity(intent);
                }
            }
        });

        new ProductSearching().execute();

        findViewById(R.id.mypage_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, MyPageActivity.class));
                overridePendingTransition(R.anim.anim_right_in,R.anim.anim_none);
            }
        });

        findViewById(R.id.to_grid_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_lv.setVisibility(View.GONE);
                search_gv.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.to_list_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_gv.setVisibility(View.GONE);
                search_lv.setVisibility(View.VISIBLE);
            }
        });

        b_navi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.tab1:{
                        header_text.setText("스마트 장보기");
                        //searchList.clear();
                        //new ProductSearching().execute();
                        fragment.setVisibility(View.GONE);
                        main_layout.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case R.id.tab2:{
                        header_text.setText("장바구니");
                        main_layout.setVisibility(View.GONE);
                        fragment.setVisibility(View.VISIBLE);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment,new Cart_list_Fragment());
                        fragmentTransaction.commit();
                        return true;
                    }
                    case R.id.tab3:{
                        header_text.setText("상품 인식");
                        main_layout.setVisibility(View.GONE);
                        fragment.setVisibility(View.VISIBLE);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment,new CameraFragment());
                        fragmentTransaction.commit();
                        return true;
                    }
                    case R.id.tab4:{
                        header_text.setText("체크리스트");
                        main_layout.setVisibility(View.GONE);
                        fragment.setVisibility(View.VISIBLE);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment,new Check_list_Fragment());
                        fragmentTransaction.commit();
                        return true;
                    }
                    case R.id.tab5:{
                        header_text.setText("리뷰 관리");
                        main_layout.setVisibility(View.GONE);
                        fragment.setVisibility(View.VISIBLE);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment,new ReviewManagement());
                        fragmentTransaction.commit();
                        return true;
                    }
                    default:return false;
                }
            }
        });

        //검색 액티비티 이동 액션
        findViewById(R.id.search_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        //홈 로고를 이용해 검색 모드 변경
        /*findViewById(R.id.home_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout view1 = findViewById(R.id.view_1);
                LinearLayout view2 = findViewById(R.id.view_2);
                if(isView_1){
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    isView_1 = false;
                }else{
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.GONE);
                    isView_1 = true;
                }

            }
        });*/

        //홈 화면에서 검색
         /*
        eraze_btn = findViewById(R.id.erase_btn);
        search_edit = findViewById(R.id.search_edit);
        eraze_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_edit.setText("");
            }
        });
        Button search_btn = findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText = search_edit.getText().toString();
                searchList.clear();
                new ProductSearching().execute();
                search_edit.setText("");
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
                }
            }
        });*/

    }

    private void Token_Check(String token){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(!success){
                        AlertDialog dialog;
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        dialog = builder.setMessage("다른 기기에서의 로그인이 감지되었습니다.")
                                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        intent.putExtra("kill",true);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .create();
                        dialog.show();
                        tokenThread.setRunning(false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        TokenRequest request = new TokenRequest(user.getUserID(),token,responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(request);
    }

    public  void setList(String key, List<Product_Item> productList){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        key = MainActivity.user.getUserID() + key;
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
    public  List<Product_Item> getList(String key){
        key = MainActivity.user.getUserID() + key;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = preferences.getString(key,null);
        List<Product_Item> list = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        if(json != null){
            try {
                JSONArray jsonArray = new JSONArray(json);
                for(int i = 0; i<jsonArray.length();i++){
                    Product_Item p = gson.fromJson(jsonArray.get(i).toString(), Product_Item.class);
                    list.add(p);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return list;
    }
    class TokenThread extends Thread{
        private boolean running = true;
        public TokenThread(){}

        public void setRunning(boolean running){
            this.running = running;
        }

        @Override
        public void run() {
            //int i = 0;
            while (running){
                Token_Check(myToken);
                try{
                    this.sleep(3000);
                    //System.out.println("running"+(i++));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    class ProductSearching extends AsyncTask<Void,Void,String> {

        String target;
        @Override
        protected void onPreExecute(){
            try {
                target="https://ctg1770.cafe24.com/SC/S_C_ProductList.php?mode=tag&searchText=";
                //target="https://ctg1770.cafe24.com/SC/S_C_ProductList.php?mode=tag&searchText="+ URLEncoder.encode(searchText,"UTF-8");
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
                //searchText = "";
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private long lastTimeBackPressed;
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-lastTimeBackPressed<1500){
            finish();
            return;
        }
        Toast.makeText(this,"뒤로가기를 한 번 더 눌러 종료합니다.",Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }
}