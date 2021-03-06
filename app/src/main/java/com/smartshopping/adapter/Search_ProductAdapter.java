package com.smartshopping.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.smartshopping.main.MainActivity;
import com.smartshopping.product.Product;
import com.smartshopping.product.ProductPop;
import com.smartshopping.product.Product_Item;
import com.smartshopping.R;
import com.smartshopping.product.SearchProduct;

import org.json.JSONArray;

import java.util.List;

public class Search_ProductAdapter extends BaseAdapter {
    private Context context;
    private List<SearchProduct> productList;

    public Search_ProductAdapter(Context context, List<SearchProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int i) {
        return productList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.search_item_list,null);
        TextView nameText = v.findViewById(R.id.name);
        TextView priceText = v.findViewById(R.id.price);
        TextView amountText = v.findViewById(R.id.amount);


        nameText.setText(productList.get(i).getpName());
        priceText.setText(productList.get(i).getPrice()+"원");
        amountText.setText("재고량 : "+productList.get(i).getAmount()+"개");

        nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductPop.class);
                String searchText = productList.get(i).getpCode();
                if(searchText.equals("")){
                    return;
                }else{
                    intent.putExtra("searchText",searchText);
                    intent.putExtra("mode","check");
                    context.startActivity(intent);
                }
            }
        });

        Button addBtn = v.findViewById(R.id.add_Btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checker = true;
                for(Product p : MainActivity.checkList){
                    if(p.getpCode().equals(productList.get(i).getpCode())){
                        p.setAmount(p.getAmount()+1);
                        checker = false;
                    }
                }
                if(checker){
                    Product p = (Product) productList.get(i);
                    Product_Item p2 = new Product_Item(p.getpCode(),p.getpName(),p.getPrice(),1,"check");
                    MainActivity.checkList.add(p2);
                }
                Toast.makeText(context,"상품이 체크리스트에 추가되었습니다.",Toast.LENGTH_SHORT).show();
                setList("checkList", MainActivity.checkList);
            }
        });


        v.setTag(productList.get(i).getpCode());

        TranslateAnimation translateAnimation = new TranslateAnimation(300, 0, 0, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(500);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        v.setAnimation(animation);

        return v;
    }
    public  void setList(String key, List<Product_Item> productList){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        /*ArrayList<Product> products = new ArrayList<>();
        for(Product p : productList){
            products.add(p);
        }*/
        key = MainActivity.user.getUserID() + key;
        JSONArray jsonArray = new JSONArray();
        Gson gson = new Gson();
        for(int i = 0; i < productList.size();i++){
            String string = gson.toJson(productList.get(i), Product_Item.class);
            jsonArray.put(string);
        }
        if(!productList.isEmpty()){
            editor.putString(key,jsonArray.toString()).commit();
        }else {
            editor.putString(key,null).commit();
        }
    }
}
