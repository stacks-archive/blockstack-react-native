package com.blockstack.examplesdkmodule;

import android.content.Intent;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import org.blockstack.android.sdk.Result;
import org.blockstack.android.sdk.UserData;
import org.blockstack.reactnative.RNBlockstackSdkModule;

import kotlin.Unit;

public class MainActivity extends ReactActivity {

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "example-sdk-module";
    }


    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            handleAuthResponse(intent);
        }
    }

    private void handleAuthResponse(Intent intent) {
        String response = intent.getDataString();
        if (response != null) {
            String[] authResponseTokens = response.split(":");

            if (authResponseTokens.length > 1) {
                String authResponse = authResponseTokens[1];

                if (RNBlockstackSdkModule.getCurrentSession() != null) {
                    RNBlockstackSdkModule.getCurrentSession().handlePendingSignIn(authResponse, new kotlin.jvm.functions.Function1<Result<UserData>, Unit>() {
                        @Override
                        public Unit invoke(Result<UserData> result) {

                            if (result.getHasValue()) {
                                // The user is now signed in!
                                Log.d("MainActivity", "user logged in");
                                if (RNBlockstackSdkModule.getCurrentSignInPromise() != null) {
                                    WritableMap map = Arguments.createMap();
                                    map.putString("decentralizedID", result.getValue().getDecentralizedID());
                                    RNBlockstackSdkModule.getCurrentSignInPromise().resolve(map);
                                }
                            }
                            return null;
                        }
                    });
                }
            }
        }
    }
}
