package com.blockstack.sdk

import org.blockstack.android.sdk.BlockstackConfig

interface ConfigProvider {
    fun getConfig(): BlockstackConfig
}