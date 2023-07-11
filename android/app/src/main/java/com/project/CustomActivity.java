package com.project;

import static com.project.CustomModule.sendEvent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CustomActivity extends AppCompatActivity {

    MainApplication application;
    ReactNativeHost reactNativeHost;
    ReactInstanceManager reactInstanceManager;
    ReactApplicationContext reactApplicationContext;
    private Uri croppedImageUri;

    Button button1,button3,button4;
    ImageView imageview1;
    String image_uri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        application = (MainApplication) this.getApplication();
        reactNativeHost = application.getReactNativeHost();
        reactInstanceManager = reactNativeHost.getReactInstanceManager();
        reactApplicationContext = (ReactApplicationContext) reactInstanceManager.getCurrentReactContext();

        button1 = findViewById(R.id.button1);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        imageview1 = findViewById(R.id.imageView1);

        button1.setOnClickListener(v -> mGetContent.launch("image/*"));

        button3.setOnClickListener(v -> {
            Intent intent = new Intent(CustomActivity.this, CropActivity.class);
            startActivityForResult(intent, 1);
            button4.setVisibility(View.VISIBLE); // Show the "Submit" button
        });

//        button3.setOnClickListener(v -> {
//            Intent intent = new Intent(CustomActivity.this, CropActivity.class);
//            startActivity(intent);
//        });


        button4.setOnClickListener(v -> {
            if (croppedImageUri != null) {
                WritableMap data = Arguments.createMap();
                data.putString("croppedImageUri", croppedImageUri.toString());
                sendEvent(reactApplicationContext, "dataCallback", data);
                button3.setVisibility(View.GONE); // Hide the "Crop" button
                button4.setVisibility(View.GONE); // Hide the "Submit" button
                finish();
            } else {
                // Handle case when no image is selected or cropped
                Log.e("CustomActivity", "No image selected or cropped");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!image_uri.equals("")){
            imageview1.setImageBitmap(getBitmapFromCache());
        }
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    image_uri = uri.toString();
                    try {
                        Bitmap showBitmap = getBitmapFromUri(uri);
                        saveBitmapToCache(showBitmap);
                        imageview1.setImageBitmap(showBitmap);
                        button3.setVisibility(View.VISIBLE); // Show the "Crop" button
                        button4.setVisibility(View.VISIBLE); // Show the "Submit" button
                    } catch (IOException e){
                        Log.e("tag", e.toString());
                    }
                }
            });

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void saveBitmapToCache(Bitmap bitmap) throws IOException {
        String filename = "final_image.jpg";
        File cacheFile = new File(getApplicationContext().getCacheDir(), filename);
        OutputStream out = new FileOutputStream(cacheFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, (int)100, out);
        out.flush();
        out.close();
    }

    public Bitmap getBitmapFromCache(){
        File cacheFile = new File(getApplicationContext().getCacheDir(), "final_image.jpg");
        Bitmap myBitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        return myBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            croppedImageUri = data.getData();
            imageview1.setImageURI(croppedImageUri);
        }
    }
}