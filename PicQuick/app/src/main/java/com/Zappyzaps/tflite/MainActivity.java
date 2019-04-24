package com.Zappyzaps.tflite;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/* Developed By- Neel Shah and Mitalee Khanna */

public class MainActivity extends AppCompatActivity {

    private static final String MODEL_PATH = "mobilenet_quant_v1_224.tflite";
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "labels.txt";
    private static final int INPUT_SIZE = 224;
    private static final int INPUT_SIZE1 = 1080;

    private Classifier classifier;

    Executor executor = Executors.newSingleThreadExecutor();

    Button btnDetectObject, btnToggleCamera;

    CameraView cameraView;
    int PICK_IMAGE_REQUEST = 100;

  /*  @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = findViewById(R.id.cameraView);


        btnToggleCamera = findViewById(R.id.btnToggleCamera);
        btnDetectObject = findViewById(R.id.btnDetectObject);

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                try {
//                      Creating bitmap obj bitmap of 224 & bmp1 of 1080
                    Bitmap bitmap = cameraKitImage.getBitmap();
                    Bitmap bmp1 = cameraKitImage.getBitmap();
                    bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                    bmp1 = Bitmap.createScaledBitmap(bmp1, INPUT_SIZE1, INPUT_SIZE1, false);

                    // passing bitmap obj of 224 into classifier
                    final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                    // b is the string which has label title of dataset eg clock
                    String b = results.get(0).getTitle();

                    //      textViewResult.setText(results.toString());
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    byte[] byteArray = stream.toByteArray();

//                    Log.e("NewBMP", String.valueOf(byteArray.length));
                    //generating bytearray of bmp1 so that it can be passed through mainactivity2
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    bmp1.compress(Bitmap.CompressFormat.PNG, 100, stream1);
                    byte[] byteArray1 = stream1.toByteArray();

                    //tv is obj which has classifier result eg clock 100%
                    String tv;
                    tv = results.toString();

                    //expliit intent in1
                    Intent in1 = new Intent(MainActivity.this, Main2Activity.class);
//                    Main2Activity.byteArray = byteArray;
                    Main2Activity.byteArray1=byteArray1;
                    in1.putExtra("title", b);
                    in1.putExtra("Uniqid", "Activity1");
                    //in1.putExtra("image", byteArray);
                    //in1.putExtra("img", byteArray1);
                    in1.putExtra("results", tv);
                    startActivity(in1);
                } catch (Exception e) {
                    Log.e("Error", String.valueOf(e) + "Error" + CameraKitError.TYPE_ERROR);
                    {

                    }
                }

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
        //impicit intent of opening gallery
        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cameraView.toggleFacing();
                Intent intent = new Intent();

                intent.setType("image/jpeg");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        initTensorFlowAndLoadModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDetectObject.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Log.e("Data", String.valueOf(uri));

            try {
                //generating uri of bitmap obj and passing through mainactivity2
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);



                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                String tv;
                tv = results.toString();
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                // b is the string which has label title of dataset eg clock
                String b = results.get(0).getTitle();
                intent.putExtra("title", b);
                intent.putExtra("Uniqid", "Activity2");
                intent.putExtra("key1", String.valueOf(uri));
                intent.putExtra("results", tv);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
