package com.tree.demoapp

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhantomResponse(
    @Expose
    @SerializedName("public_key")
    val publicKey: String,
    @Expose
    @SerializedName("session")
    val session: String,
)
