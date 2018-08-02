package com.blockstack.sdk

import android.util.Base64
import android.util.Log
import com.facebook.react.bridge.*
import org.blockstack.android.sdk.*
import java.net.URI


class BlockstackNativeModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName() = "BlockstackNativeModule"

    override fun getConstants(): MutableMap<String, Any> {
        val constants = HashMap<String, Any>()
        return constants;
    }

    private lateinit var session: BlockstackSession

    @ReactMethod
    fun createSession(configArg: ReadableMap, promise: Promise) {
        val activity = getReactApplicationContext().currentActivity
        if (activity != null) {
            val scopes = configArg.getArray("scopes")
                    .toArrayList().map { Scope.valueOf((it as String)
                            .split("_").joinToString("") { it.capitalize() }) }
                    .toTypedArray()

            if (!configArg.hasKey("appDomain")) {
                throw IllegalArgumentException("'appDomain' needed in config object")
            }
            val appDomain = configArg.getString("appDomain")
            val manifestUrl = if (configArg.hasKey("manifestUrl")) {
                configArg.getString("manifestUrl")
            } else {
                "$appDomain/manifest.json"
            }

            val redirectUrl = if(configArg.hasKey("redirectUrl")) {
                configArg.getString("redirectUrl")
            } else {
                "$appDomain/redirect"
            }
            val config = BlockstackConfig(URI(appDomain), URI(redirectUrl), URI(manifestUrl), scopes)

            activity.runOnUiThread {
                Log.d("BlockstackNativeModule", "create session")
                session = BlockstackSession(activity, config) {
                    Log.d("BlockstackNativeModule", "created session")
                    val map = Arguments.createMap()
                    map.putBoolean("loaded", true)
                    promise.resolve(map)
                    currentSession = session
                }
            }
        } else {
            promise.reject(IllegalStateException("must be called from an Activity that implements ConfigProvider"))
        }
    }

    @ReactMethod
    fun signIn(promise: Promise) {
        if (session.loaded) {
            BlockstackNativeModule.currentSignInPromise = promise
            getReactApplicationContext().currentActivity!!.runOnUiThread {
                session.redirectUserToSignIn {
                    // never called
                }
            }
        }
    }

    @ReactMethod
    fun signUserOut(promise: Promise) {
        if (session.loaded) {
           getReactApplicationContext().currentActivity!!.runOnUiThread {
               session.signUserOut {
                   val map = Arguments.createMap()
                   map.putBoolean("signedOut", true)
                   promise.resolve(map)
               }
           }
        }
    }

    @ReactMethod
    fun putFile(path: String, content:String, optionsArg:ReadableMap, promise: Promise) {
        if (canUseBlockstack()) {
            reactApplicationContext.currentActivity!!.runOnUiThread {
                val options = PutFileOptions(optionsArg.getBoolean("encrypt"))
                session.putFile(path, content, options) {
                    if (it.hasValue) {
                        val map = Arguments.createMap()
                        map.putString("fileUrl", it.value)
                        promise.resolve(map)
                    } else {
                        promise.reject("0", it.error)
                    }
                }
            }
        }
    }

    @ReactMethod
    fun getFile(path: String, optionsArg: ReadableMap, promise: Promise) {
        if (canUseBlockstack()) {
            reactApplicationContext.currentActivity!!.runOnUiThread {
                val options = GetFileOptions(optionsArg.getBoolean("decrypt"))
                session.getFile(path, options) {
                    if (it.hasValue) {
                        val map = Arguments.createMap()
                        if (it.value is String) {
                            map.putString("fileContents", it.value as String)
                        } else {
                            map.putString("fileContentsEncoded", Base64.encodeToString(it.value as ByteArray, Base64.NO_WRAP))
                        }
                        promise.resolve(map)
                    } else {
                        promise.reject("0", it.error)
                    }
                }
            }
        }
    }

    private fun canUseBlockstack() = session.loaded && reactApplicationContext.currentActivity != null

    companion object {
        // TODO only store transitKey and the likes in this static variable
        var currentSession: BlockstackSession? = null
        var currentSignInPromise: Promise? = null
    }
}

