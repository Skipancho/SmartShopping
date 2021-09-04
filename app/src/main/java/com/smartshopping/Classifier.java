package com.smartshopping;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Classifier {
    private Interpreter.Options tfOptions;
    protected Interpreter tflite;
    private MappedByteBuffer tfModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private List<String> labels;
    private int MAX_RESULT = 20;

    public Classifier(Activity activity, String modelPath, String labelPath) throws IOException {
        tfOptions = new Interpreter.Options();
        //Neural Networks API
        //tfOptions.setUseNNAPI(true);


        // load TF Lite model
        tfModel = FileUtil.loadMappedFile(activity, modelPath);
        tflite = new Interpreter(tfModel, tfOptions);

        // load labels from label file
        labels = FileUtil.loadLabels(activity, labelPath);

        // reads type and shape of input tensors of tf model
        int[] imageShape = tflite.getInputTensor(0).shape(); // (1,height, width, 3)
        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        DataType imageDataType = tflite.getInputTensor(0).dataType();
        // create input tensor
        inputImageBuffer = new TensorImage(imageDataType);

        // reads type and shape of output tensors of tf model
        int[] probShape = tflite.getOutputTensor(0).shape(); // (1, NUM_CLASSES)
        DataType probDataType = tflite.getOutputTensor(0).dataType();
        //create output tensor
        outputProbabilityBuffer = TensorBuffer.createFixedSize(probShape, probDataType);
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

    public List<Recognition> recognizeImage(Bitmap bitmap) {

        inputImageBuffer = loadImage(bitmap);
        probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

        // runs model
        tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());

        // map labels and their corresponding probability
        TensorLabel tLabel = new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer));

        // create map to access the result based on label
        Map<String, Float> labeledProb = tLabel.getMapWithFloatValue();

        return getSortedResult(labeledProb);

    }


    private List<Recognition> getSortedResult(Map<String, Float> labeledProb) {
        PriorityQueue<Recognition> pq =
                new PriorityQueue<>(
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(Recognition o1, Recognition o2) {
                                return Float.compare(o2.getConfidence(), o1.getConfidence());
                            }
                        }
                );

        // 큐에 데이터 넣음
        for (Map.Entry<String, Float> entry: labeledProb.entrySet()) {
            pq.add(new Recognition(entry.getKey(), entry.getValue()));
        }

        // 몇개를 반환할 것인지 결정
        List<Recognition> recognitions = new ArrayList<>();
        int recognitionsSize = Math.min(pq.size(), MAX_RESULT);

        // 큐에서 정해진 갯수만큼 데이터 빼냄
        for (int i=0; i<recognitionsSize; i++) {
            recognitions.add(pq.poll());
        }

        return recognitions;
    }


    //이름과 확률 쌍
    public class Recognition {
        private String title;
        private Float confidence;

        public Recognition(String title, Float confidence) {
            this.title = title;
            this.confidence = confidence;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }
    }

}
