package com.smartshopping;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.List;

public class Product_Item_Adapter extends BaseAdapter {
    private Context context;
    private List<Product_Item> productList;

    public Product_Item_Adapter(Context context, List<Product_Item> productList) {
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
        View v = View.inflate(context,R.layout.product_item,null);

        TextView nameText =(TextView)v.findViewById(R.id.productName);
        TextView priceText =(TextView)v.findViewById(R.id.productPrice);
        final TextView amountText =(TextView)v.findViewById(R.id.amount);
        Button upBtn = (Button)v.findViewById(R.id.upBtn);
        Button downBtn = (Button)v.findViewById(R.id.downBtn);
        Button delBtn = (Button)v.findViewById(R.id.add_del_Btn);

        nameText.setText(productList.get(i).getpName());
        priceText.setText(productList.get(i).getPrice()+"원");
        amountText.setText(String.valueOf(productList.get(i).getAmount()));
        nameText.setSelected(true);

        final Product_Item p = productList.get(i);

        if(p.getWhere().equals("cart")){
            boolean checker = true;
            for(Product_Item product : MainActivity.checkList ) {
                if (product.getpCode().equals(p.getpCode())) {
                    checker = false;
                    break;
                }
            }
            if(checker){
                v.setBackground(context.getResources().getDrawable(R.drawable.red_shape_line));
            }
        }else if(p.getWhere().equals("check")){
            boolean checker = false;
            for(Product_Item product : MainActivity.productList ) {
                if (product.getpCode().equals(p.getpCode())) {
                    checker = true;
                    break;
                }
            }
            if(checker){
                v.setBackground(context.getResources().getDrawable(R.drawable.blue_shape_line));
            }
        }

        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productList.get(i).setAmount(productList.get(i).getAmount()+1);
                amountText.setText(""+productList.get(i).getAmount());
                if(p.getWhere().equals("check")){
                    setList("checkList", productList);
                    Check_list_Fragment.totalCal_check();
                }
                else if(p.getWhere().equals("cart")){
                    setList("cartList", productList);
                    Cart_list_Fragment.totalCal_cart();
                }

            }
        });
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount = productList.get(i).getAmount();
                if(amount>=2){
                    productList.get(i).setAmount(productList.get(i).getAmount()-1);
                    amountText.setText(""+productList.get(i).getAmount());
                }else {
                    productList.remove(i);
                    notifyDataSetChanged();
                }
                if(p.getWhere().equals("check")){
                    setList("checkList", productList);
                    Check_list_Fragment.totalCal_check();
                }
                else if(p.getWhere().equals("cart")){
                    setList("cartList", productList);
                    Cart_list_Fragment.totalCal_cart();
                }
            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productList.remove(i);
                if(p.getWhere().equals("check")){
                    setList("checkList", productList);
                    Check_list_Fragment.totalCal_check();
                }
                else if(p.getWhere().equals("cart")){
                    setList("cartList", productList);
                    Cart_list_Fragment.totalCal_cart();
                }
                notifyDataSetChanged();
            }
        });

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
        key = MainActivity.user.getUserID() + key;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        /*ArrayList<Product> products = new ArrayList<>();
        for(Product p : productList){
            products.add(p);
        }*/
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
