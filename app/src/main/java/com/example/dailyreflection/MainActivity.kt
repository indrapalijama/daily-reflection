package com.example.dailyreflection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.dailyreflection.ui.MainReflectionScreen
import com.example.dailyreflection.viewmodel.ReflectionViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by lazy { ReflectionViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainReflectionScreen(viewModel)
        }
    }
}