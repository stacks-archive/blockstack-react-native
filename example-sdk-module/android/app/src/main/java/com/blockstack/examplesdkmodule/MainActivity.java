package com.blockstack.examplesdkmodule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import org.blockstack.android.sdk.BlockstackSession;
import org.blockstack.android.sdk.Result;
import org.blockstack.android.sdk.UserData;
import org.blockstack.reactnative.RNBlockstackSdkModule;

import javax.annotation.Nullable;

import kotlin.Unit;

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
                if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
                    String response = getIntent().getDataString();
                    if (response != null) {
                        String[] authResponseTokens = response.split(":");

                        if (authResponseTokens.length > 1) {

                            final String authResponse = authResponseTokens[1];
                            Bundle b = new Bundle();
                            b.putString("authResponse", authResponse);
                            Log.d(TAG, "launch with " + authResponse);
                            return b;
                        }
                    }
                }
                return null;
            }
        };
    }
}
