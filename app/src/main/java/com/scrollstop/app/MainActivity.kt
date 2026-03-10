package com.scrollstop.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.scrollstop.app.data.PreferencesManager
import com.scrollstop.app.security.PinManager
import com.scrollstop.app.security.PinState
import com.scrollstop.app.ui.navigation.ScrollStopNavHost
import com.scrollstop.app.ui.theme.ScrollStopTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesManager = PreferencesManager(applicationContext)
        val pinManager = PinManager(preferencesManager)

        setContent {
            ScrollStopTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ScrollStopNavHost(
                        preferencesManager = preferencesManager,
                        pinManager = pinManager
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Lock PIN state when app goes to background
        PinState.lock()
    }
}
