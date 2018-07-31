package com.blockstack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.blockstack.sdk.BlockstackNativeModule
import com.blockstack.sdk.ConfigProvider
import com.facebook.react.ReactActivity
import com.facebook.react.bridge.Arguments
import org.blockstack.android.sdk.BlockstackConfig
import org.blockstack.android.sdk.Scope
import org.blockstack.android.sdk.toBlockstackConfig

class MainActivity : ReactActivity(), ConfigProvider {

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    override fun getMainComponentName(): String? {
        return "Blockstack"
    }

    override fun getConfig(): BlockstackConfig = "https://flamboyant-darwin-d11c17.netlify.com"
            .toBlockstackConfig(arrayOf(Scope.StoreWrite))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == Intent.ACTION_VIEW) {
            handleAuthResponse(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.action == Intent.ACTION_VIEW) {
            handleAuthResponse(intent)
        }
    }

    private fun handleAuthResponse(intent: Intent) {
        val response = intent.dataString
        if (response != null) {
            val authResponseTokens = response.split(':')

            if (authResponseTokens.size > 1) {
                val authResponse = authResponseTokens[1]

                if (BlockstackNativeModule.currentSession != null) {
                    BlockstackNativeModule.currentSession!!.handlePendingSignIn(authResponse, { userData ->
                        if (userData.hasValue) {
                            // The user is now signed in!
                            Log.d("MainActivity", "user logged in")
                            if (BlockstackNativeModule.currentSignInPromise != null) {
                                val map = Arguments.createMap()
                                map.putString("decentralizedID", userData.value!!.decentralizedID)
                                BlockstackNativeModule.currentSignInPromise!!.resolve(map)
                            }
                        }
                    })
                }
            }
        }
    }
}
