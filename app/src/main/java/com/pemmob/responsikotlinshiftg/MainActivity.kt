package com.pemmob.responsikotlinshiftg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pemmob.responsikotlinshiftg.ui.navigation.AppNavigation
import com.pemmob.responsikotlinshiftg.ui.theme.ResponsiKotlinShiftGTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResponsiKotlinShiftGTheme {
                AppNavigation()
            }
        }
    }
}