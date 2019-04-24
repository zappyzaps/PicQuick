package com.Zappyzaps.tflite;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Locale;


public class Main2Activity extends AppCompatActivity {

    ImageView iv;
    TextView textView, scanResults;
    Button button, btn3;
    private TextRecognizer detector;
    private static Bitmap  bmp1;
    TextToSpeech t1;
    private String path,blocks;
    public static byte[] byteArray;
    public static byte[] byteArray1;
    public static byte[] b1;
    public static byte[] b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        iv = findViewById(R.id.imageView);
        button = findViewById(R.id.button2);
        scanResults = findViewById(R.id.textView);
        detector = new TextRecognizer.Builder(getApplicationContext()).build();

        Intent i = getIntent();
        //s has b's content getting by id "title" eg clock
        final String s = i.getStringExtra("title");
        Intent intent = getIntent();
        intent.getStringExtra("title");
        //id is for differentiating camera and gallery activity
        String id = intent.getStringExtra("Uniqid");
        if (id.equals("Activity1")) {
            //camera activity
            //b1=byteArray;
//            bmp = BitmapFactory.decodeByteArray(b1, 0, b1.length);
//            Log.e("BMP", String.valueOf(byteArray1.length));

            //bmp1 has 1080 resolution
            b2 = byteArray1;
            //byteArray = getIntent().getByteArrayExtra("image");
            bmp1 = BitmapFactory.decodeByteArray(b2, 0, b2.length);
            iv.setImageBitmap(bmp1);
            textView = findViewById(R.id.tv2);
            //str1 has results of classifier eg clock 100%
            String str1 = intent.getStringExtra("results");
            textView.setText(str1);

            //getimageuri stores the image clicked from camera and generates the URI
            getImageUri(this, bmp1);

            // OCR function
            try {
//               String s5 = new String(byteArray);

                Bitmap bitmap = decodeBitmapUri(this, Uri.parse(path));
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = detector.detect(frame);
                    blocks = "";
                    String lines = "";
                    // words = "";
                    for (int index = 0; index < textBlocks.size(); index++) {
                        //extract scanned text blocks here
                        TextBlock tBlock = textBlocks.valueAt(index);
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
                        for (Text line : tBlock.getComponents()) {
                            //extract scanned text lines here
                            //lines = lines + line.getValue() + "\n";
                            for (Text element : line.getComponents()) {
                                //extract scanned text words here
                                //  words = words + element.getValue() + ", ";
                            }
                        }
                    }
                    if (textBlocks.size() == 0) {
                        scanResults.setText("\n" + "No text detected");
                    } else {
                        scanResults.setText("\n" + "Blocks: " + "\n");
                        scanResults.setText(blocks + "\n");
                       /* scanResults.setText(scanResults.getText() + "---------" + "\n");
                        scanResults.setText(scanResults.getText() + "Lines: " + "\n");
                        scanResults.setText(scanResults.getText() + lines + "\n");
                        scanResults.setText(scanResults.getText() + "---------" + "\n");*/
                        //  scanResults.setText(scanResults.getText() + "Words: " + "\n");
                        //scanResults.setText(scanResults.getText() + words + "\n");
                        // scanResults.setText(scanResults.getText() + "---------" + "\n");
                    }
                } else {
                    scanResults.setText("Could not set up the detector!");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

//          gallery activity
        } else if (id.equals("Activity2")) {
            //str contains URI of image selected from gallery
            String str = intent.getStringExtra("key1");
            iv = findViewById(R.id.imageView);
            iv.setImageURI(Uri.parse(str));
            textView = findViewById(R.id.tv2);
            String str2 = intent.getStringExtra("results");
            textView.setText(str2);

            // OCR function
            try {
                Bitmap bitmap = decodeBitmapUri(this, Uri.parse(str));
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = detector.detect(frame);
                    blocks = "";
                    String lines = "";
                    // words = "";
                    for (int index = 0; index < textBlocks.size(); index++) {
                        //extract scanned text blocks here
                        TextBlock tBlock = textBlocks.valueAt(index);
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
                        for (Text line : tBlock.getComponents()) {
                            //extract scanned text lines here
                            //lines = lines + line.getValue() + "\n";
                            for (Text element : line.getComponents()) {
                                //extract scanned text words here
                                //words = words + element.getValue() + ", ";
                            }
                        }
                    }
                    if (textBlocks.size() == 0) {
                        scanResults.setText("\n" + "No text detected");
                    } else {
                        scanResults.setText("Blocks: " + "\n");
                        scanResults.setText(scanResults.getText() + blocks + "\n");
                       /* scanResults.setText(scanResults.getText() + "---------" + "\n");
                        scanResults.setText(scanResults.getText() + "Lines: " + "\n");
                        scanResults.setText(scanResults.getText() + lines + "\n");
                        scanResults.setText(scanResults.getText() + "---------" + "\n");*/
//                        scanResults.setText(scanResults.getText() + "Words: " + "\n");
//                        scanResults.setText(scanResults.getText() + words + "\n");
                        // scanResults.setText(scanResults.getText() + "---------" + "\n");
                    }
                } else {
                    scanResults.setText("Could not set up the detector!");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e("hello", e.toString());
            }
        }
//          buy now implicit intent for redirecting to browser
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blk="";
                Log.e("blockscreen", String.valueOf(blocks.length()));
                if(blocks.length()>15)
                {
                     blk=blocks.substring(0,15);
                    Log.e("Condition",blk);
                }
                else {
                    Log.e("Normal BLK",blocks);
                    blk=blocks;
                }

//                  s is string that has title eg clock
                if (s.equals("Not Found"))
                {
                    Toast.makeText(Main2Activity.this, "Object Can't Be Purchased ", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                    String kl = "https://www.amazon.in/s/?url=search-alias%3Daps&field-keywords=" + s +" "+blk;
//             kl.concat(result);
                    Intent i = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(kl));
                    startActivity(i);

                }
            }
        });

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    t1.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }
        public void onPause(){
            //text to speech
            if(t1 !=null){
                t1.stop();
                t1.shutdown();
            }
            super.onPause();
        }


    // OCR backend calculation function
    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    private void getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        Uri.parse(path);

//        ContentResolver contentResolver = getContentResolver();
//        contentResolver.delete( Uri.parse(path),null,null);
    }
}
