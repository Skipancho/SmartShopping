package com.smartshopping;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private CaptureManager captureManager;
    private DecoratedBarcodeView barcodeView;
    private EditText barcodeText;
    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
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
    Uri imageuri;
    ImageView imageView;

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
        try{
            tflite=new Interpreter(loadmodelfile(getActivity()));
        }catch (Exception e) {
            e.printStackTrace();
        }
        Button p_inq_Btn = getView().findViewById(R.id.p_Inq);
        p_inq_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ProductPop.class);
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
            labels = FileUtil.loadLabels(getContext(),"labels.txt");
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        float maxValueInMap =(Collections.max(labeledProbability.values()));

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            if (entry.getValue()==maxValueInMap) {
                barcodeText.setText(entry.getKey().substring(2));
            }
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