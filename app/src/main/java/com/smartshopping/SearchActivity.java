package com.smartshopping;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SearchActivity extends AppCompatActivity {

    private ListView search_lv, tag_lv;
    private GridView search_gv;
    private List<SearchProduct> searchList = new ArrayList<>();
    private GridAdapter gridAdapter;
    private Search_ProductAdapter listAdapter;
    private Button eraze_btn;
    private TextView auto_save_on_off;
    public static EditText search_edit;
    private LinearLayout after_search, bottom_ll, s_img_ll;
    private FrameLayout st_fl;
    private List<String> searchedTexts = new ArrayList<>();
    private SearchedItemAdapter stAdapter;
    private boolean auto_save_mode = true;

    //이미지 분류용
    protected Interpreter tflite;
    //private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Bitmap bitmap;
    private List<String> labels;
    private ImageView s_img;
    private Uri imageuri;
    private boolean isImageSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        after_search = (LinearLayout) findViewById(R.id.search_layout);
        st_fl = (FrameLayout)findViewById(R.id.last_searching);
        bottom_ll =(LinearLayout)findViewById(R.id.bottom_ll);
        tag_lv = (ListView) findViewById(R.id.tag_list);
        search_lv = (ListView) findViewById(R.id.search_list);
        search_gv = (GridView) findViewById(R.id.search_grid);
        search_edit = (EditText) findViewById(R.id.search_edit);
        eraze_btn = (Button) findViewById(R.id. erase_btn);
        auto_save_on_off = (TextView) findViewById(R.id.auto_save_on_off);

        s_img_ll = (LinearLayout) findViewById(R.id.s_img_ll);
        s_img = (ImageView) findViewById(R.id.searching_img);

        SharedPreferences sp = getSharedPreferences("search_tag", Activity.MODE_PRIVATE);
        String auto_save_check = sp.getString("auto_save",null);
        if(auto_save_check == null||auto_save_check.equals("true")){
            auto_save_mode = true;
        }else{
            st_fl.setVisibility(View.GONE);
            auto_save_on_off.setText("자동저장 켜기");
            auto_save_mode = false;
        }
        searchedTexts = getList("st_List");
        if(searchedTexts.size() == 0){
            findViewById(R.id.no_tag_text).setVisibility(View.VISIBLE);
        }

        gridAdapter = new GridAdapter(SearchActivity.this,searchList);
        listAdapter = new Search_ProductAdapter(SearchActivity.this,searchList);
        stAdapter = new SearchedItemAdapter(SearchActivity.this,searchedTexts);

        search_gv.setAdapter(gridAdapter);
        search_lv.setAdapter(listAdapter);
        tag_lv.setAdapter(stAdapter);

        search_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this,ProductPop.class);
                String searchText = searchList.get(i).getpCode();
                if(searchText.equals("")){
                    return;
                }else{
                    intent.putExtra("searchText",searchText);
                    intent.putExtra("mode","check");
                    SearchActivity.this.startActivity(intent);
                }
            }
        });
        //검색 액션
        Button search_btn = findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search();
            }
        });
        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if(EditorInfo.IME_ACTION_SEARCH == id){
                    Search();
                }else{
                    return false;
                }
                return true;
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        eraze_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_edit.setText("");
                BeforeSearch();
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
        //검색기록 전체 삭제
        findViewById(R.id.all_clear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchedTexts.clear();
                setList("st_List",searchedTexts);
                stAdapter.notifyDataSetChanged();
                findViewById(R.id.no_tag_text).setVisibility(View.VISIBLE);
            }
        });
        //자동저장 끄기 켜기
        auto_save_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("search_tag", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if(auto_save_mode){
                    editor.putString("auto_save","false");
                    st_fl.setVisibility(View.GONE);
                    auto_save_on_off.setText("자동저장 켜기");
                    auto_save_mode = false;
                }else{
                    editor.putString("auto_save","true");
                    st_fl.setVisibility(View.VISIBLE);
                    auto_save_on_off.setText("자동저장 끄기");
                    auto_save_mode = true;
                }
                editor.commit();
            }
        });

        //글자 지우기 버튼 띄우기
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
                    BeforeSearch();
                    searchList.clear();
                }
            }
        });

        //이미지 검색
        findViewById(R.id.search_by_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),12);
            }
        });

        try{
            tflite=new Interpreter(loadmodelfile(SearchActivity.this));
        }catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.img_clear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BeforeSearch();
                searchList.clear();
                isImageSearch = false;
            }
        });

    }
    private void Search(){
        String searchText = search_edit.getText().toString();
        searchList.clear();
        gridAdapter.notifyDataSetChanged();
        listAdapter.notifyDataSetChanged();
        new ProductSearching(searchText).execute();
        AfterSearch();
        if(isImageSearch){
            s_img_ll.setVisibility(View.GONE);
        }
        if(auto_save_mode){
            if(searchedTexts.size() == 0){
                findViewById(R.id.no_tag_text).setVisibility(View.GONE);
            }else{
                int index = -1;
                for(int i = 0; i < searchedTexts.size();i++){
                    if(searchedTexts.get(i).equals(searchText)){
                        index = i;
                    }
                }
                if(index>=0){
                    searchedTexts.remove(index);
                }
            }
            searchedTexts.add(0,searchText);
            stAdapter.notifyDataSetChanged();
            setList("st_List",searchedTexts);
        }
    }

    private void AfterSearch(){
        bottom_ll.setVisibility(View.GONE);
        st_fl.setVisibility(View.GONE);
        after_search.setVisibility(View.VISIBLE);
    }

    private void BeforeSearch(){
        bottom_ll.setVisibility(View.VISIBLE);
        s_img_ll.setVisibility(View.GONE);
        after_search.setVisibility(View.GONE);
        if(auto_save_mode){
            st_fl.setVisibility(View.VISIBLE);
        }
    }

    public  List<String> getList(String key){
        key = MainActivity.user.getUserID() + key;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = preferences.getString(key,null);
        List<String> list = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        if(json != null){
            try {
                JSONArray jsonArray = new JSONArray(json);
                for(int i = 0; i<jsonArray.length();i++){
                    String p = gson.fromJson(jsonArray.get(i).toString(), String.class);
                    list.add(p);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return list;
    }
    public  void setList(String key, List<String> list){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

    //이미지 분류 액션
    public void getResult(){
        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

        int probabilityTensorIndex = 0;
        int[] probabilityShape =
                tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        inputImageBuffer = new TensorImage(imageDataType);
        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
        probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

        inputImageBuffer = loadImage(bitmap);

        tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
        showresult();
    }

    private TensorImage loadImage(final Bitmap bitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }


    private void showresult(){

        try{
            labels = FileUtil.loadLabels(SearchActivity.this,"labels.txt");
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        float maxValueInMap =(Collections.max(labeledProbability.values()));

        List<String> keys = new ArrayList<>();

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            //결과
            if (entry.getValue()==maxValueInMap) {
                //prediction.setText(entry.getKey());
                //System.out.println(entry.getKey()+" : "+entry.getValue());
                keys.add(0,entry.getKey());
            }else if(entry.getValue()>=0.1)
                keys.add(entry.getKey());
        }
        for(String key : keys){
            new ProductSearching(key).execute();
        }
        AfterSearch();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==12 && resultCode==RESULT_OK && data!=null) {
            imageuri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                s_img.setImageBitmap(bitmap);
                isImageSearch = true;
                s_img_ll.setVisibility(View.VISIBLE);
                getResult();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ProductSearching extends AsyncTask<Void,Void,String> {
        String target;
        String s_text;

        //public ProductSearching(){}

        public ProductSearching(String text){
            this.s_text = text;
        }

        @Override
        protected void onPreExecute(){
            try {
                target="https://ctg1770.cafe24.com/SC/S_C_ProductList.php?mode=tag&searchText="+ URLEncoder.encode(s_text,"UTF-8");
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
                if(count == 0){
                    findViewById(R.id.no_item_text).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.no_item_text).setVisibility(View.GONE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}