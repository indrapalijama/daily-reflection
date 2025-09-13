package com.example.dailyreflection.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyreflection.DailyReflectionApp
import com.example.dailyreflection.data.local.FavoriteReflection
import com.example.dailyreflection.viewmodel.FavoritesViewModel
import com.example.dailyreflection.viewmodel.ReflectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainReflectionScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as DailyReflectionApp

    val reflectionViewModel: ReflectionViewModel = viewModel(
        factory = ViewModelFactory(app.favoritesRepository)
    )
    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = ViewModelFactory(app.favoritesRepository)
    )

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFavorite by remember { mutableStateOf<FavoriteReflection?>(null) }

    var isReflectionFullscreen by remember { mutableStateOf(false) }
    var isFavoriteDetailFullscreen by remember { mutableStateOf(false) }

    val shouldShowBottomBar = !isReflectionFullscreen && !isFavoriteDetailFullscreen

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(50)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(50)
                )
            ) {
                ReflectionBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { newTab ->
                        if (newTab == 0 && selectedTab != 0) {
                            reflectionViewModel.refreshCurrentFavoriteStatus()
                        }
                        selectedTab = newTab
                        selectedFavorite = null
                        isReflectionFullscreen = false
                        isFavoriteDetailFullscreen = false
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> ReflectionScreen(
                    viewModel = reflectionViewModel,
                    isFullscreen = isReflectionFullscreen,
                    onFullscreenToggle = { isReflectionFullscreen = !isReflectionFullscreen }
                )
                1 -> SourcesScreen(
                    selectedSourceId = reflectionViewModel.selectedSource.value,
                    onSourceSelected = { newSource ->
                        reflectionViewModel.selectSource(newSource)
                        selectedTab = 0
                    }
                )
                2 -> {
                    if (selectedFavorite == null) {
                        FavoritesScreen(
                            viewModel = favoritesViewModel,
                            onFavoriteClick = { selectedFavorite = it }
                        )
                    } else {
                        FavoriteDetailScreen(
                            favorite = selectedFavorite!!,
                            onBack = {
                                selectedFavorite = null
                                isFavoriteDetailFullscreen = false
                            },
                            isFullscreen = isFavoriteDetailFullscreen,
                            onFullscreenToggle = { isFavoriteDetailFullscreen = !isFavoriteDetailFullscreen }
                        )
                    }
                }
            }
        }
    }
}