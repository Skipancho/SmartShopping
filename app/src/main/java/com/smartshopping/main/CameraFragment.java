package com.smartshopping.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.smartshopping.product.ProductPop;
import com.smartshopping.R;

import java.util.List;

public class CameraFragment extends Fragment {

    private CaptureManager captureManager;
    private DecoratedBarcodeView barcodeView;
    private EditText barcodeText;
    private Bitmap bitmap;
    private ImageView imageView;
    private Button p_inq_Btn;
    private Classifier classifier;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        barcodeView = getView().findViewById(R.id.barcodeScn);
        barcodeText = getView().findViewById(R.id.barcodeNum);
        imageView =getView().findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);

        barcodeView.setStatusText("QR / 바코드를 사각형 안에 비춰주세요.");

        captureManager = new CaptureManager(getActivity(),barcodeView);
        captureManager.initializeFromIntent(getActivity().getIntent(),savedInstanceState);
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                readBarcode(result.toString());
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });

        initClassifier();
        //상품 조회 버튼
        p_inq_Btn = getView().findViewById(R.id.p_Inq);
        p_inq_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProductPop.class);
                String searchText = barcodeText.getText().toString();
                if(searchText.equals("")){
                    return;
                }else{
                    intent.putExtra("searchText",searchText);
                    intent.putExtra("mode","cart");
                   getActivity().startActivity(intent);
                }

            }
        });
        Button photoScnBtn = getView().findViewById(R.id.photoScanBtn);
        photoScnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //카메라 온
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 101);
            }
        });
        barcodeText.setEnabled(false);
        barcodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int bar_len = barcodeText.getText().toString().length();
                if(bar_len >= 5){
                    p_inq_Btn.setBackground(getResources().getDrawable(R.drawable.btn_shape_no_line));
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    public void readBarcode(String barcode){
        //Toast.makeText(getApplicationContext(),barcode,Toast.LENGTH_SHORT).show();
        barcodeText.setText(barcode);
    }
    private void getResult(){
        List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
        Classifier.Recognition recognition = results.get(0);
        barcodeText.setText(recognition.getTitle());
    }

    void initClassifier(){
        try{
            classifier = new Classifier(getActivity(),"model.tflite","labels.txt");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101  && resultCode == Activity.RESULT_OK && data!=null){
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            imageView.setImageBitmap(bitmap);
            getResult();
        }
    }
}