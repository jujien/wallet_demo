package com.tree.demoapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tree.demoapp.ui.theme.DemoAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val isRefreshing = MutableStateFlow(false)
    private val list = MutableStateFlow(mutableListOf<String>())

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("Notification", "Granted")
        } else {
            Toast
                .makeText(baseContext, "Notification denied", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("Key_Encrypt", Context.MODE_PRIVATE)
        val publicKey = prefs.getString("public_key",  "") ?: ""

        setContent {
            DemoAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PublicKey(text = publicKey)
                        List(isRefreshing = isRefreshing.asStateFlow(), list = list.asStateFlow(), onRefresh = {
                            onRefresh()
                        })
                    }
                }
            }
        }

    }

    private fun onRefresh() {
        Log.d("onRefresh", "Refreshing")
        isRefreshing.update { true }
        GlobalScope.launch {
            delay(3000)
            launch(Dispatchers.Main) {
                list.update { mutableListOf("1", "2", "3", "4", "5", "6", "7", "8") }
                isRefreshing.update { false }
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
                Log.d("Post Notification", "Granted")
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
                Log.d("Post Notification", "should show request")
            } else {
                // Directly ask for the permission
                Log.d("Post Notification", "Directly ask for permission")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            Log.d("Notification", "Granted")
        }
    }
}

@Composable
fun PublicKey(text: String) {
    Box(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = "Address: $text")
    }
}

@Composable
fun List(isRefreshing: StateFlow<Boolean>, list: StateFlow<MutableList<String>>, onRefresh: () -> Unit) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing.collectAsState().value)
    val value = list.collectAsState().value

    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh, modifier = Modifier
        .fillMaxSize()
        .padding(
            vertical = 32.dp,
        )) {
        LazyColumn {
            items(value) {
                Row {
                    Text(text = "Index: $it")
                    Spacer(modifier = Modifier.fillParentMaxWidth())
                }
            }
        }
    }


}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DemoAppTheme {
        Greeting("Android")
    }
}