package com.smartshopping.main;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.smartshopping.R;
import com.smartshopping.main.MainActivity;
import com.smartshopping.product.Product;
import com.smartshopping.product.Product_Item;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

public class QR_pop extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_pop);
        ImageView qr_iv = findViewById(R.id.qr_iv);
        qr_iv.setImageBitmap(createQR(toJson(MainActivity.productList)));

        findViewById(R.id.finish_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               setResult(RESULT_OK);
               finish();
            }
        });
    }
    public String toJson(List<Product_Item> list){
        JSONArray jsonArray = new JSONArray();
        Gson gson = new GsonBuilder().create();
        for(int i = 0; i < list.size();i++){
            String string = gson.toJson(list.get(i), Product.class);
            jsonArray.put(string);
        }
        String json = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userID",MainActivity.user.getUserID());
            jsonObject.put("list",jsonArray);
            json = jsonObject.toString();
            json = URLEncoder.encode(json,"UTF-8");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
    public Bitmap createQR(String json){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(json, BarcodeFormat.QR_CODE,300,300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;
        }catch (WriterException e){
            e.printStackTrace();
        }
        return null;
    }

}