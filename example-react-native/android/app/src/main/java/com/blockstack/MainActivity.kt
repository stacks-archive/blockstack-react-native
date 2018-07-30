package com.blockstack

import com.blockstack.sdk.ConfigProvider
import com.facebook.react.ReactActivity
import org.blockstack.android.sdk.BlockstackConfig
import org.blockstack.android.sdk.BlockstackSession
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

}
