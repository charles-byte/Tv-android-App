package com.charles.tvhome

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.charles.tvhome.ui.home.HomeScreen
import com.charles.tvhome.ui.theme.TvHomeTheme

class MainActivity : ComponentActivity() {

    private var homePresses by mutableIntStateOf(0)
    private var resumes by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is the home screen: Back has nowhere to go, so it does nothing.
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = Unit
            },
        )

        setContent {
            TvHomeTheme {
                HomeScreen(homePresses = homePresses, resumes = resumes)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumes++
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasCategory(Intent.CATEGORY_HOME)) homePresses++
    }
}
