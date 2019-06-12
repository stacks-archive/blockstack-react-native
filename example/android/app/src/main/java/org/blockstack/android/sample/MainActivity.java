package org.blockstack.android.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.blockstack.android.sdk.BlockstackSession;
import org.blockstack.reactnative.RNBlockstackSdkModule;

import javax.annotation.Nullable;

public class MainActivity extends ReactActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "example-sdk-module";
    }

    @Override
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new ReactActivityDelegate(this, getMainComponentName()) {
            @Nullable
            @Override
            protected Bundle getLaunchOptions() {
                if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
                    final String authResponse = getAuthResponse(getIntent());
                    if (authResponse != null) {
                        Bundle b = new Bundle();
                        b.putString("authResponse", authResponse);
                        Log.d(TAG, "launch with " + authResponse);
                        return b;
                    }
                }

                return null;
            }
        };
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            handleAuthResponse(intent);
        }
    }

    private void handleAuthResponse(Intent intent) {
        String authResponse = getAuthResponse(intent);
        final BlockstackSession session = RNBlockstackSdkModule.getCurrentSession();
        final ReactContext reactContext = getReactInstanceManager().getCurrentReactContext();
        if (authResponse != null && session != null && reactContext != null) {
            Log.d(TAG, "received " + authResponse);
            WritableMap params = Arguments.createMap();
            params.putString("authResponse", authResponse);
            sendEvent(reactContext, "onNewEvent", params);
            Log.d(TAG, "done");

        }
    }

    private String getAuthResponse(Intent intent) {
        String response = intent.getData().getQuery();
        if (response != null) {
            String[] authResponseTokens = response.split("=");

            if (authResponseTokens.length > 1) {
                final String authResponse = authResponseTokens[1];
                return authResponse;
            }
        }
        return null;
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
