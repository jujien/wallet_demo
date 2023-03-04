package com.tree.demoapp

import com.google.gson.annotations.Expose

data class Transactions (
    @Expose
    val newUser: Data
) {
    data class Data(
        @Expose
        val transactions: List<Transaction>
    )

    data class Transaction(
        @Expose
        val sender: String,
        @Expose
        val recipient: String,
        @Expose
        val amountTransferred: Int,
        @Expose
        val transactionFee: Int,
        @Expose
        val signature: String
    )
}
