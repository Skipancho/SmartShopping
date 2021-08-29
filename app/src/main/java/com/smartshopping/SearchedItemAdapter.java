package com.smartshopping;

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

import org.json.JSONArray;

import java.util.List;

public class SearchedItemAdapter extends BaseAdapter {
    private Context context;
    private List<String> tagList;

    public SearchedItemAdapter(Context context, List<String> tagList) {
        this.context = context;
        this.tagList = tagList;
    }

    @Override
    public int getCount() {
        return tagList.size();
    }

    @Override
    public Object getItem(int i) {
        return tagList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context,R.layout.searching_tag_item,null);
        TextView tag_text = v.findViewById(R.id.tag_text);
        Button delete_btn = v.findViewById(R.id.delete_btn);

        tag_text.setText(tagList.get(i));
        tag_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.search_edit.setText(tagList.get(i));
            }
        });


        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagList.remove(i);
                setList("st_List",tagList);
                notifyDataSetChanged();
            }
        });

        return v;
    }
    public  void setList(String key, List<String> list){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        key = MainActivity.user.getUserID() + key;
        JSONArray jsonArray = new JSONArray();
        Gson gson = new Gson();
        for(int i = 0; i < list.size();i++){
            String string = gson.toJson(list.get(i), String.class);
            jsonArray.put(string);
        }
        if(!list.isEmpty()){
            editor.putString(key,jsonArray.toString()).commit();
        }else {
            editor.putString(key,null).commit();
        }
    }
}
