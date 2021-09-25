package com.smartshopping;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

public class MyReviewAdapter extends BaseAdapter {
    private Context context;
    private List<MyReview> reviewList;
    private AlertDialog dialog;

    public MyReviewAdapter(Context context, List<MyReview> reviewList) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context,R.layout.my_review,null);
        TextView mainText =(TextView)v.findViewById(R.id.rtext);
        TextView pName =(TextView)v.findViewById(R.id.pName);
        TextView dateText =(TextView)v.findViewById(R.id.rDate);
        TextView scoreText = (TextView)v.findViewById(R.id.score);
        Button update_btn = (Button)v.findViewById(R.id.updateBtn);
        Button delete_btn = (Button)v.findViewById(R.id.deleteBtn);

        mainText.setText(reviewList.get(i).getText());
        pName.setText(reviewList.get(i).getpName());
        dateText.setText(reviewList.get(i).getDate());
        String star = "";
        for(int j = 0; j < reviewList.get(i).getScore(); j++){
            star += "★";
        }
        for(int j = 0; j < 5-reviewList.get(i).getScore() ; j++){
            star += "☆";
        }
        scoreText.setText(star);
        if(reviewList.get(i).getGap()> 7) {
            update_btn.setVisibility(View.GONE);
        }
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ReviewActivity.class);
                intent.putExtra("rId",reviewList.get(i).getrID());
                intent.putExtra("pCode",reviewList.get(i).getpCode());
                intent.putExtra("pName",reviewList.get(i).getpName());
                intent.putExtra("rText",reviewList.get(i).getText());
                intent.putExtra("score",reviewList.get(i).getScore());
                context.startActivity(intent);
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                dialog = builder.setMessage("삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int index) {
                                deleteReview(i);
                            }
                        })
                        .setNegativeButton("취소",null)
                        .create();
                dialog.show();
            }
        });
        v.setTag(reviewList.get(i).getText());
        return v;
    }
    public void deleteReview(final int i){
        int rId = reviewList.get(i).getrID();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        Toast.makeText(context,"리뷰 삭제 완료",Toast.LENGTH_SHORT).show();
                        reviewList.remove(i);
                        notifyDataSetChanged();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        ReviewDeleteRequest request = new ReviewDeleteRequest(rId,responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}
