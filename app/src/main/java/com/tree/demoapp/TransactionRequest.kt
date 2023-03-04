package com.tree.demoapp

data class TransactionRequest(
    val page: Int,
    val size: Int,
    val walletId: String
)
