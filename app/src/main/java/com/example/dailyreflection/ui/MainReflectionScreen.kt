package com.example.dailyreflection.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.dailyreflection.viewmodel.ReflectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainReflectionScreen(viewModel: ReflectionViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            ReflectionBottomBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> ReflectionScreen(viewModel)
                1 -> SourcesScreen(
                    selectedSourceId = viewModel.selectedSource.value,
                    onSourceSelected = { newSource ->
                        viewModel.selectSource(newSource)
                        selectedTab = 0
                    }
                )
                2 -> FavoritesScreen()
            }
        }
    }
}