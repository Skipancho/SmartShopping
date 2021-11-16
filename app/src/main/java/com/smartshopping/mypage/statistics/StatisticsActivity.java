package com.smartshopping.mypage.statistics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.smartshopping.main.MainActivity;
import com.smartshopping.R;
import com.smartshopping.user.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class StatisticsActivity extends AppCompatActivity {
    private User user = MainActivity.user;
    private PieChart pieChart;
    //private int[] colorArray = new int[] {Color.LTGRAY,Color.BLUE,Color.RED,Color.GREEN,Color.YELLOW};
    private int[] colorArray = new int[] {0xffef9a9a,0xffffe082,0xffc5e1a5,0xff80cbc4,0xffb39ddb,0xff9fa8da,0xffffcc80,0xffce93d8};
    private ArrayList<Entry> dataVal = new ArrayList<>();
    private ArrayList<PurData> dataArray = new ArrayList<>();
    private TextView spend_text;
    private ListView typeList;
    private PurDataAdapter adapter;
    private ArrayAdapter year_adapt, month_adapt;
    private Spinner year_spin, month_spin;
    private String firstDate, lastDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        spend_text = findViewById(R.id.spend_text);
        pieChart = findViewById(R.id.pieChart);
        typeList = findViewById(R.id.typeList);
        year_spin = findViewById(R.id.year_spin);
        month_spin = findViewById(R.id.month_spin);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat yFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat mFormat = new SimpleDateFormat("MM");
        String year = yFormat.format(date);
        String month = mFormat.format(date);

        dateSet(year,month);

        adapter = new PurDataAdapter(dataArray);
        typeList.setAdapter(adapter);
        year_adapt = ArrayAdapter.createFromResource(this,R.array.year,android.R.layout.simple_spinner_dropdown_item);
        month_adapt = ArrayAdapter.createFromResource(this,R.array.month,android.R.layout.simple_spinner_dropdown_item);
        year_spin.setAdapter(year_adapt);
        month_spin.setAdapter(month_adapt);
        month_spin.setSelection(Integer.parseInt(month)-1);

        Button chart_call_btn = findViewById(R.id.chart_call_btn);
        chart_call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataVal.clear();
                dataArray.clear();
                pieChart.clear();
                String year = year_spin.getSelectedItem().toString();
                String month = month_spin.getSelectedItem().toString();
                dateSet(year,month);
                new BackgroundTask().execute();
            }
        });

        findViewById(R.id.month_st_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatisticsActivity.this.startActivity(new Intent(StatisticsActivity.this, MonthlyStatisticsActivity.class));
            }
        });

        new BackgroundTask().execute();
    }
    private void dateSet(String year, String month){
        firstDate = year +"-"+month+"-01";
        lastDate =  year +"-"+month+"-31";
        /*System.out.println(firstDate);
        System.out.println(lastDate);*/
    }


    class BackgroundTask extends AsyncTask<Void,Void,String> {

        String target;
        @Override
        protected void onPreExecute(){
            try {
                target="https://ctg1770.cafe24.com/SC/S_C_PurchaseCal.php?userID="+ URLEncoder.encode(user.getUserID(),"UTF-8")
                        +"&first="+URLEncoder.encode(firstDate,"UTF-8")
                        +"&last="+URLEncoder.encode(lastDate,"UTF-8");
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
                String type;
                int price;
                int count = 0;
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                while(count<jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    type = object.getString("type");
                    price = object.getInt("price");
                    PurData data = new PurData(type,price);
                    dataArray.add(data);
                    count++;
                }
                adapter.notifyDataSetChanged();
                if(count > 0)
                    DataCal();
                else{
                    spend_text.setText("0 원");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    void DataCal(){
        int sum = 0;
        ArrayList<String> labels = new ArrayList<>();
        for(PurData p : dataArray){
            sum += p.price;
        }
        spend_text.setText(sum+"원");
        //System.out.println("sum : "+sum);
        int i = 0;
        double etc = 0;
        for(PurData p : dataArray){
            double rate = ((int)((p.price/(double)sum)*10000))/100.0;
            if(rate >= 5.0){
                dataVal.add(new Entry((float) rate, i++));
                labels.add(p.type);
            }else{
                etc += rate;
            }
        }
        if(etc > 0){
            dataVal.add(new Entry((float)etc,i));
            labels.add("기타");
        }
        PieDataSet pieDataSet = new PieDataSet(dataVal,"");
        pieDataSet.setColors(colorArray);
        PieData pieData = new PieData(labels,pieDataSet);
        pieChart.setUsePercentValues(true);
        pieData.setValueTextSize(15);
        pieChart.setCenterText("지출 유형");
        pieChart.setCenterTextSize(25);
        pieChart.setHoleRadius(30);
        pieChart.setData(pieData);
        pieChart.animateXY(1000,1000);
        pieChart.setDescription("");
        pieChart.invalidate();
    }
    class PurData{
        String type;
        int price;
        public PurData(String type, int price){
            this.type = type;
            this.price = price;
        }
        public int getPrice(){
            return price;
        }
    }
    class PurDataAdapter extends BaseAdapter{
        private ArrayList<PurData> purDataList;

        public PurDataAdapter(ArrayList<PurData> purDataList){
            this.purDataList = purDataList;
        }

        @Override
        public int getCount() {
            return purDataList.size();
        }

        @Override
        public Object getItem(int i) {
            return purDataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(StatisticsActivity.this,R.layout.type_item,null);
            TextView typeText = v.findViewById(R.id.type);
            TextView spendText = v.findViewById(R.id.spend);

            typeText.setText(purDataList.get(i).type);
            spendText.setText(purDataList.get(i).price+" (원)");

            return v;
        }
    }
}