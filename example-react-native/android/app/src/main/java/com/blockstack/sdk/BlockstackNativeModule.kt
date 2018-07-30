package com.blockstack.sdk

import android.widget.Toast
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

class BlockstackNativeModule(reactContext:ReactApplicationContext): ReactContextBaseJavaModule(reactContext) {

    override fun getName() = "BlockstackNativeModule"

    override fun getConstants(): MutableMap<String, Any> {
        val constants = HashMap<String, Any>()
        return constants;
    }

    @ReactMethod
    fun signIn() {
        Toast.makeText(getReactApplicationContext(), "sign in", Toast.LENGTH_SHORT).show()
    }
}