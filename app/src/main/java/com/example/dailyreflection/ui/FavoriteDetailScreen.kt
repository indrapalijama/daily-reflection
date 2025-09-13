package com.example.dailyreflection.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyreflection.data.local.FavoriteReflection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDetailScreen(
    favorite: FavoriteReflection,
    onBack: () -> Unit,
    isFullscreen: Boolean = false,
    onFullscreenToggle: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            if (!isFullscreen) {
                TopAppBar(
                    title = { Text("Favorit", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MonoBackground,
                        titleContentColor = MonoText
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullscreen) PaddingValues(0.dp) else paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MonoCardBg,
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(2.dp, MonoAccent)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            favorite.title,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoText
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Bacaan: ${favorite.passage}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MonoText
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            favorite.content,
                            fontSize = 20.sp,
                            color = MonoText,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Sumber: ${favorite.source}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light,
                            color = MonoAccent,
                            modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.End)
                        )
                    }
                }
            }

            // Floating Fullscreen Toggle Button
            FloatingActionButton(
                onClick = onFullscreenToggle,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MonoAccent,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = if (isFullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                    contentDescription = if (isFullscreen) "Exit Fullscreen" else "Enter Fullscreen",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}