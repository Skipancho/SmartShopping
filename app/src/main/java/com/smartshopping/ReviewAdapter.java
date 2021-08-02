package com.smartshopping;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ReviewAdapter extends BaseAdapter {
    private Context context;
    private List<Review_Product> reviewList;

    public ReviewAdapter(Context context, List<Review_Product> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }
    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public Object getItem(int i) {
        return reviewList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context,R.layout.review,null);
        TextView mainText =(TextView)v.findViewById(R.id.rtext);
        TextView nickname =(TextView)v.findViewById(R.id.rUser);
        TextView dateText =(TextView)v.findViewById(R.id.rDate);
        TextView scoreText = (TextView)v.findViewById(R.id.score);

        mainText.setText(reviewList.get(i).getText());
        nickname.setText(reviewList.get(i).getNickName());
        dateText.setText(reviewList.get(i).getDate());
        String star = "";
        for(int j = 0; j < reviewList.get(i).getScore(); j++){
            star += "★";
        }
        for(int j = 0; j < 5-reviewList.get(i).getScore() ; j++){
            star += "☆";
        }
        scoreText.setText(star);
        v.setTag(reviewList.get(i).getText());
        return v;
    }
}
