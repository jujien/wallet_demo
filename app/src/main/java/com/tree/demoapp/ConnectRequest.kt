package com.tree.demoapp

import com.google.gson.annotations.Expose

data class ConnectRequest(
    @Expose
    val wallet: String,
    @Expose
    val deviceId: String,
)
