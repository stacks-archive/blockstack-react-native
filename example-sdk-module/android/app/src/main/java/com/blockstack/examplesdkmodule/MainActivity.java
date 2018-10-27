package com.blockstack.examplesdkmodule;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import org.blockstack.android.sdk.BlockstackSession;
import org.blockstack.android.sdk.Result;
import org.blockstack.android.sdk.UserData;
import org.blockstack.reactnative.RNBlockstackSdkModule;

import kotlin.Unit;

public class MainActivity extends ReactActivity {

    Handler handler = new Handler();
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

                final BlockstackSession session = RNBlockstackSdkModule.getCurrentSession();
                if (session != null) {

                    Log.d("MainActivity", Thread.currentThread().getName());
                    session.aquireThreadLock();
                    Log.d("MainActivity", "aquired ");
                    session.handlePendingSignIn(authResponse, new kotlin.jvm.functions.Function1<Result<UserData>, Unit>() {
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
                            handler.postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                                                  Log.d("MainActivity", Thread.currentThread().getName());
                                                  try {
                                                      session.releaseThreadLock();
                                                  } catch (Exception e) {
                                                      Log.e("MainActivity", e.toString(), e);
                                                  }
                                                  Log.d("MainActivity", "released");
                                              }
                                          }, 500);
                            return null;
                        }
                    });
                }
            }
        }
    }
}
