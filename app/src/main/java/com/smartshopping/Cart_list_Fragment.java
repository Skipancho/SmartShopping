package com.smartshopping;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Cart_list_Fragment extends Fragment {
    private Product_Item_Adapter adapter;
    private static TextView price_Text, no_item;
    private AlertDialog dialog;
    private final User user = MainActivity.user;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView product_lv = getView().findViewById(R.id.cartList);
        adapter = new Product_Item_Adapter(getContext(),MainActivity.productList);

        product_lv.setAdapter(adapter);

        no_item = getView().findViewById(R.id.no_cart_text);
        price_Text = getView().findViewById(R.id.totalPrice);
        price_Text.setSelected(true);
        totalCal_cart();

        Button buyBtn = getView().findViewById(R.id.buyBtn);
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                dialog = builder.setMessage("구매하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(MainActivity.productList.size()==0){
                                    Toast.makeText(getContext(),"장바구니에 상품이 없습니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                //AddPurchase();
                                Intent intent = new Intent(getActivity(),QR_pop.class);
                                startActivityForResult(intent,77);
                            }
                        })
                        .setNegativeButton("취소",null)
                        .create();
                dialog.show();
            }
        });

    }
    public static void totalCal_cart(){
        List<Product_Item> ps = MainActivity.productList;
        int sum = 0;
        for(Product_Item p : ps){
            sum += p.getPrice()*p.getAmount();
        }
        price_Text.setText("총액 : "+sum+"원");
        if(ps.size()==0){
            no_item.setVisibility(View.VISIBLE);
        }else{
            no_item.setVisibility(View.INVISIBLE);
        }
    }
    public void AddPurchase(){
        /*long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
        String bDate = mFormat.format(date);
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(!success){
                        Toast.makeText(getContext(),"네트워크 에러",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        for(Product p : MainActivity.productList){
            int price = p.getPrice()*p.getAmount();
            PurchaseRequest request = new PurchaseRequest(user.getUserID(),p.getpCode(),price,p.getAmount(),bDate,responseListener);
            queue.add(request);
        }*/
        MainActivity.productList.clear();
        MainActivity.checkList.clear();
        setList("cartList",MainActivity.productList);
        setList("checkList",MainActivity.checkList);
        adapter.notifyDataSetChanged();
        totalCal_cart();
    }
    public  void setList(String key, List<Product_Item> productList){
        key = MainActivity.user.getUserID() + key;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart_list_, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 77  && resultCode == Activity.RESULT_OK){
            AddPurchase();
        }
    }
}