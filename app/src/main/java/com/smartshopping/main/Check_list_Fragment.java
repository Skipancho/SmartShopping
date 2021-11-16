package com.smartshopping.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.smartshopping.R;
import com.smartshopping.adapter.Product_Item_Adapter;
import com.smartshopping.main.MainActivity;
import com.smartshopping.product.Product_Item;

import java.util.List;

public class Check_list_Fragment extends Fragment {


    private ListView check_lv;
    private Product_Item_Adapter adapter;
    private static TextView total_price,no_item;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        check_lv = getView().findViewById(R.id.checkList);
        adapter = new Product_Item_Adapter(getContext(), MainActivity.checkList);
        check_lv.setAdapter(adapter);
        no_item = getView().findViewById(R.id.no_check_text);
        total_price = getView().findViewById(R.id.totalPrice);
        totalCal_check();
    }
    public static void totalCal_check(){
        List<Product_Item> ps = MainActivity.checkList;
        int sum = 0;
        for(Product_Item p : ps){
            sum += p.getPrice()*p.getAmount();
        }
        total_price.setText("총액 : "+sum+"원");
        if(ps.size()==0){
            no_item.setVisibility(View.VISIBLE);
        }else{
            no_item.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_list_, container, false);
    }
}