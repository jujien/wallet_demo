package com.tree.demoapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tree.demoapp.ui.theme.DemoAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.bitcoinj.core.Base58
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity: ComponentActivity() {

    lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRetrofit()
        setContent {
            DemoAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginView {
                        val publicKey = getSharedPreferences("Key_Encrypt", Context.MODE_PRIVATE)
                            .getString("my_public_key", "") ?: ""
                        val uri = Uri.Builder()
                            .scheme("https")
                            .authority("phantom.app")
                            .appendPath("ul")
                            .appendPath("v1")
                            .appendPath("connect")
                            .appendQueryParameter("app_url", "https://beavercrush.com/")
                            .appendQueryParameter("dapp_encryption_public_key", publicKey)
                            .appendQueryParameter("cluster", "devnet")
                            .appendQueryParameter("redirect_link", "treeapp://connected")
                            .build()

                        Log.d("Uri", uri.toString())

                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                uri
                            )
                        )
                    }
                }
            }
        }

        getSharedPreferences("Key_Encrypt", Context.MODE_PRIVATE).apply {
            getString("my_public_key", "").let {
                if (it != "") {
                } else {
                    val keyPair = TweetNaclFast.Box.keyPair()
                    val pubKey = Base58.encode(keyPair.publicKey)
                    val privKey = Base58.encode(keyPair.secretKey)
                    edit()
                        .putString("my_public_key", pubKey)
                        .putString("my_private_key", privKey)
                        .apply()
                }
            }
            getString("session", "")?.let {
                if (it.isNotEmpty()) {
                    startActivity(
                        Intent(this@LoginActivity, MainActivity::class.java)
                    )
                    finish()
                }
            }

        }

        intent?.data?.let {
            Log.d("Response", it.toString())
            val nonce = it.getQueryParameter("nonce") ?: ""
            val phantomEncryptKey = it.getQueryParameter("phantom_encryption_public_key") ?: ""
            val data = it.getQueryParameter("data") ?: ""

            val prefs = getSharedPreferences("Key_Encrypt", Context.MODE_PRIVATE)
            val priv = prefs.getString("my_private_key", "") ?: ""


            val bytes = Base58.decode(data)

            val box = TweetNaclFast.Box(Base58.decode(phantomEncryptKey), Base58.decode(priv))
            box.open(bytes, Base58.decode(nonce))?.let { msg ->
                Log.d("Message", String(msg))
                val res = Gson().fromJson(String(msg), PhantomResponse::class.java)
                connect(res.publicKey, prefs.getString("fcm_token", "")!!, res.session, nonce)
            }

        }
    }

    private fun connect(publicKey: String, fcmToken: String, session: String, nonce: String) {
        GlobalScope.launch {
            val res = apiService.connect(ConnectRequest(publicKey, fcmToken))
            Log.d("id", res.newUser.id)
            launch(Dispatchers.Main) {
                getSharedPreferences("Key_Encrypt", Context.MODE_PRIVATE)
                    .edit()
                    .putString("public_key", publicKey)
                    .putString("session", session)
                    .putString("nonce", nonce)
                    .apply()
                startActivity(
                    Intent(this@LoginActivity, MainActivity::class.java)
                )
                finish()
            }
        }
    }

    private fun initRetrofit() {
        val client = OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://958b-183-91-4-124.ap.ngrok.io/")
            .client(client)
            .addConverterFactory(
                GsonConverterFactory
                    .create(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create())
            )
            .build()
        apiService = retrofit.create(ApiService::class.java)


    }
}

@Composable
fun LoginView(onClicked: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        Button(onClick = onClicked) {
            Text(text = "Sign in with Phantom")
        }
    }
}