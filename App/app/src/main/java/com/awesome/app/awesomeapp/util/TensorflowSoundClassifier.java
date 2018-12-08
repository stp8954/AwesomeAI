package com.awesome.app.awesomeapp.util;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public class TensorflowSoundClassifier {

    private Interpreter interpreter;
    private int inputSize;
    private List<String> labelList;
    private HashMap<Integer, Double> preds;
    private List<Integer> labels;
    private List<float[]> embds;

    private double avgTimespam = 0;
    private int count = 0;

    private TensorflowSoundClassifier(){

    }

    static TensorflowSoundClassifier create(AssetManager assetManager,
                                            String modelPath,
                                            String labelPath,
                                            int inputSize) throws IOException{
        TensorflowSoundClassifier classifier = new TensorflowSoundClassifier();
        classifier.interpreter = new Interpreter(classifier.loadModelFile(assetManager, modelPath));
        //classifier.labelList = classifier.loadLabelList(assetManager, labelPath);
        classifier.inputSize = inputSize;
        return classifier;
    }

    public float[] Classify(float[] spectrogram)
    {
        float[][] result = new float[1][8];
        float[][][][] input = new float[1][80][64][1];

        for (int i = 0 ;i < 80 ; i++)
        {
            for (int j = 0; j <64 ; j++)
            {
                input[0][i][j][0] = spectrogram[64 * i + j];
            }
        }


        Calendar start = Calendar.getInstance();
        count++;
        interpreter.run(input, result);
        Calendar end = Calendar.getInstance();

        avgTimespam = (avgTimespam * (count -1) + (end.getTimeInMillis() - start.getTimeInMillis()))/count;
        Log.i("AwesomeLog","Avg evaluation time: [" + avgTimespam + "]");

       // monitor.updateProbs(String.format("Fire Alarm:%f,",result[0][0] * 100.0 ),
       //         String.format("Carhorn:%f,",result[0][1] * 100.0 ),
       //         String.format("Crying:%f,",result[0][2] * 100.0 ),
       //         String.format("Dog:%f,",result[0][3] * 100.0 ),
       //         String.format("Door:%f,",result[0][4] * 100.0 ),
       //         String.format("Doorbell:%f,",result[0][5] * 100.0 ),
       //         String.format("Gun:%f,",result[0][6] * 100.0 ),
       //         String.format("Siren:%f,",result[0][7] * 100.0 ));

        return result[0];
        //Log.i("AwesomeLog","Got 128 dim result vector: [" + sb.toString() + "]");
        //Log.i("AwesomeLog","Probability : [" + sb2.toString() + "]");

    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws  IOException{
        AssetFileDescriptor fileDescriptio = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptio.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptio.getStartOffset();
        long declaredLength = fileDescriptio.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


}
