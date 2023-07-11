package com.project;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class CustomModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext reactContext;

    public CustomModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "CustomModule";
    }

    @ReactMethod
    public void startActivity(String message) {
        Intent intent = new Intent(reactContext, CustomActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        reactContext.startActivity(intent);
    }

    @ReactMethod
    public void addListener(String eventName) {
        // Implement the logic to add a listener for the given event
        // You can use the DeviceEventManagerModule to add the listener
    }

    @ReactMethod
    public void removeListeners(int count) {
        // Implement the logic to remove the specified number of listeners
        // You can use the DeviceEventManagerModule to remove listeners
    }

    public static void sendEvent(
            ReactApplicationContext reactContext,
            String eventName,
            @Nullable WritableMap params
    ) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
