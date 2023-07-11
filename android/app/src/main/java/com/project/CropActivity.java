package com.project;

import static com.project.CustomModule.sendEvent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CropActivity extends AppCompatActivity {
    CropUtils.CropImageView imageview1;
    private Uri croppedImageUri;

    MainApplication application;
    ReactNativeHost reactNativeHost;
    ReactInstanceManager reactInstanceManager;
    ReactApplicationContext reactApplicationContext;



    private void sendCroppedImageUriToCustomActivity(Uri croppedImageUri) {
        Intent resultIntent = new Intent();
        resultIntent.setData(croppedImageUri);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

//    private void sendCroppedImageUriToReactNative(Uri croppedImageUri) {
//        // Send the croppedImageUri to React Native
//        WritableMap data = Arguments.createMap();
//        data.putString("croppedImageUri", croppedImageUri.toString());
//        sendEvent(reactApplicationContext, "dataCallback", data);
//    }

    private void sendCroppedImageUriToReactNative(Uri croppedImageUri) {
        String base64Image = convertImageToBase64(croppedImageUri);
        if (base64Image != null) {
            WritableMap data = Arguments.createMap();
            data.putString("croppedImageBase64", base64Image);
            sendEvent(reactApplicationContext, "dataCallback", data);
        }
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop);

        application = (MainApplication) this.getApplication();
        reactNativeHost = application.getReactNativeHost();
        reactInstanceManager = reactNativeHost.getReactInstanceManager();
        reactApplicationContext = (ReactApplicationContext) reactInstanceManager.getCurrentReactContext();

        Button button1 = findViewById(R.id.done_button);
        imageview1 = findViewById(R.id.cropimageview);

        imageview1.setImageBitmap(getBitmapFromCache());
//        button1.setOnClickListener(v -> {
//            Bitmap bitmap = imageview1.getCroppedImage();
//            try {
//                Uri croppedImageUri = saveBitmapToCache(bitmap);
//                sendCroppedImageUriToCustomActivity(croppedImageUri);
//                sendCroppedImageUriToReactNative(croppedImageUri);
//            } catch (IOException e) {
//                Log.e("tag", e.toString());
//            }
//        });

        button1.setOnClickListener(v -> {
            Bitmap bitmap = imageview1.getCroppedImage();
            try {
                Uri croppedImageUri = saveBitmapToCache(bitmap);
                sendCroppedImageUriToCustomActivity(croppedImageUri);
                sendCroppedImageUriToReactNative(croppedImageUri);
            } catch (IOException e) {
                Log.e("tag", e.toString());
            }
            finish();
        });

    }


    public Uri saveBitmapToCache(@NonNull Bitmap bitmap) throws IOException {
        String filename = "final_image.jpg";
        File cacheFile = new File(getApplicationContext().getCacheDir(), filename);
        OutputStream out = new FileOutputStream(cacheFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, (int)100, out);
        out.flush();
        out.close();
        return Uri.fromFile(cacheFile);
    }

    public Bitmap getBitmapFromCache(){
        File cacheFile = new File(getApplicationContext().getCacheDir(), "final_image.jpg");
        Bitmap myBitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        return myBitmap;
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
