package com.tree.demoapp

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("connect")
    suspend fun connect(
        @Body param: ConnectRequest
    ): User

    suspend fun transactions(
        @Body param: TransactionRequest
    ): Transactions
}