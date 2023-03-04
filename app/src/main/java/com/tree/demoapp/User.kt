package com.tree.demoapp

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(
    @Expose
    @SerializedName("newUser")
    val newUser: Data
) {
    data class Data(
        @Expose
        @SerializedName("wallet")
        val wallet: String,
        @Expose
        @SerializedName("deviceId")
        val deviceId: String,
        @Expose
        @SerializedName("_id")
        val id: String,
    )
}
