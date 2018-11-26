package org.blockstack.reactnative

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.Base64
import android.util.Log
import com.facebook.react.bridge.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import org.blockstack.android.sdk.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URI

class RNBlockstackSdkModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName() = "RNBlockstackSdk"

    override fun getConstants(): MutableMap<String, Any> {
        val constants = HashMap<String, Any>()
        return constants;
    }

    private lateinit var session: BlockstackSession
    private lateinit var handler: Handler
    private val handlerThread: HandlerThread = HandlerThread("blockstack-rn")

    @ReactMethod
    fun hasSession(promise: Promise) {
        val map = Arguments.createMap()
        try {
            @Suppress("SENSELESS_COMPARISON")
            map.putBoolean("hasSession", session != null)
        } catch (e:Exception) {
            map.putBoolean("hasSession", false)
        }
        promise.resolve(map)
    }

    @ReactMethod
    fun createSession(configArg: ReadableMap, promise: Promise) {
        val activity = getReactApplicationContext().currentActivity
        if (activity != null) {
            val scopes = configArg.getArray("scopes")
                    .toArrayList().map {
                        Scope.valueOf((it as String)
                                .split("_").joinToString("") { it.capitalize() })
                    }
                    .toTypedArray()

            if (!configArg.hasKey("appDomain")) {
                throw IllegalArgumentException("'appDomain' needed in config object")
            }
            val appDomain = configArg.getString("appDomain")
            val manifestPath = if (configArg.hasKey("manifestUrl")) {
                configArg.getString("manifestUrl")
            } else {
                "/manifest.json"
            }

            val redirectPath = if (configArg.hasKey("redirectUrl")) {
                configArg.getString("redirectUrl")
            } else {
                "/redirect"
            }
            val config = BlockstackConfig(URI(appDomain), redirectPath, manifestPath, scopes)

            handlerThread.start()
            handler = Handler(handlerThread.looper)

            runOnV8Thread {
                Log.d("BlockstackNativeModule", "create session" + Thread.currentThread().name)

                session = BlockstackSession(activity, config, executor = object : Executor {
                    override fun onMainThread(function: (Context) -> Unit) {
                        activity.runOnUiThread {
                            function(activity)
                        }
                    }

                    override fun onV8Thread(function: () -> Unit) {
                        runOnV8Thread(function)
                    }

                    override fun onNetworkThread(function: suspend () -> Unit) {
                        async(CommonPool) {
                            function()
                        }
                    }

                })

                Log.d("BlockstackNativeModule", "created session")
                val map = Arguments.createMap()
                map.putBoolean("loaded", true)
                promise.resolve(map)
                currentSession = session
                currentHandler = handler
            }
        } else {
            Log.d(name, "reject create session")
            promise.reject(IllegalStateException("must be called from an Activity that implements ConfigProvider"))
        }
    }

    private fun runOnV8Thread(function: () -> Unit) {
        handler.post(function)
    }

    @ReactMethod
    fun isUserSignedIn(promise: Promise) {
        if (session.loaded) {
            runOnV8Thread {
                val map = Arguments.createMap()
                map.putBoolean("signedIn", session.isUserSignedIn())
                Log.d("RNBlockstack", "signed in:" + map.getBoolean("signedIn").toString())
                promise.resolve(map)
            }
        } else {
            promise.reject("NOT_LOADED", "Session not loaded")
        }
    }

    @ReactMethod
    fun signIn(promise: Promise) {
        if (session.loaded) {
            runOnV8Thread {

                RNBlockstackSdkModule.currentSignInPromise = promise
                session.redirectUserToSignIn {
                    // never called
                }
            }
        } else {
            promise.reject("NOT_LOADED", "Session not loaded")
        }
    }

    @ReactMethod
    fun handlePendingSignIn(authResponse: String, promise: Promise) {
        if (session.loaded) {
            runOnV8Thread {
                session.handlePendingSignIn(authResponse) { result ->
                    val userData = result.value
                    if (userData != null) {
                        // The user is now signed in!
                        val map = convertJsonToMap(userData.json)
                        map.putBoolean("loaded", true)
                        promise.resolve(map)
                    } else {
                        promise.reject("ERROR", result.error)
                    }
                }
            }
        }
    }

    @ReactMethod
    fun signUserOut(promise: Promise) {
        if (session.loaded) {
            runOnV8Thread {
                session.signUserOut()
                val map = Arguments.createMap()
                map.putBoolean("signedOut", true)
                promise.resolve(map)
            }
        } else {
            promise.reject("NOT_LOADED", "Session not loaded")
        }
    }

    @ReactMethod
    fun loadUserData(promise: Promise) {
        if (session.loaded) {
            runOnV8Thread {
                val userData = session.loadUserData()
                if ( userData != null) {
                    promise.resolve(convertJsonToMap(userData.json))
                } else {
                    promise.reject("NOT_SIGNED_IN", "Not signed in")
                }
            }
        } else {
            promise.reject("NOT_LOADED", "Session not loaded")
        }
    }

    @ReactMethod
    fun putFile(path: String, content: String, optionsArg: ReadableMap, promise: Promise) {
        if (canUseBlockstack()) {
            runOnV8Thread {
                val options = PutFileOptions(optionsArg.getBoolean("encrypt"))
                session.putFile(path, content, options) {
                    Log.d("RNBlockstackSdkModuel", "putFile result")
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
            runOnV8Thread {
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


    @Throws(JSONException::class)
    private fun convertJsonToMap(jsonObject: JSONObject): WritableMap {
        val map = Arguments.createMap()

        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = jsonObject.get(key)
            if (value is JSONObject) {
                map.putMap(key, convertJsonToMap(value))
            } else if (value is JSONArray) {
                map.putArray(key, convertJsonToArray(value))
            } else if (value is Boolean) {
                map.putBoolean(key, value)
            } else if (value is Int) {
                map.putInt(key, value)
            } else if (value is Double) {
                map.putDouble(key, value)
            } else if (value is String) {
                map.putString(key, value)
            } else {
                map.putString(key, value.toString())
            }
        }
        return map
    }

    @Throws(JSONException::class)
    private fun convertJsonToArray(jsonArray: JSONArray): WritableArray {
        val array = Arguments.createArray()

        for (i in 0 until jsonArray.length()) {
            val value = jsonArray.get(i)
            if (value is JSONObject) {
                array.pushMap(convertJsonToMap(value))
            } else if (value is JSONArray) {
                array.pushArray(convertJsonToArray(value))
            } else if (value is Boolean) {
                array.pushBoolean(value)
            } else if (value is Int) {
                array.pushInt(value)
            } else if (value is Double) {
                array.pushDouble(value)
            } else if (value is String) {
                array.pushString(value)
            } else {
                array.pushString(value.toString())
            }
        }
        return array
    }

    companion object {
        // TODO only store transitKey and the likes in this static variable
        @JvmStatic
        var currentSession: BlockstackSession? = null
        @JvmStatic
        var currentSignInPromise: Promise? = null
        @JvmStatic
        var currentHandler: Handler? = null
    }
}