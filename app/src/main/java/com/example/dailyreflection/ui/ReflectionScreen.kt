package com.example.dailyreflection.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyreflection.viewmodel.ReflectionViewModel
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.dailyreflection.R

val MonoBackground = Color(0xFFFAFAFA)
val MonoText = Color(0xFF111111)
val MonoCardBg = Color(0xFFFFFFFF)
val MonoAccent = Color(0xFF444444)
val MonoError = Color(0xFF000000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflectionScreen(viewModel: ReflectionViewModel) {
    val reflection by viewModel.reflection
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val isRefreshing = isLoading

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.fetchReflection() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MonoBackground)
            .pullRefresh(pullRefreshState)
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {}
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.server_error),
                            contentDescription = "Error illustration",
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error ?: "",
                            color = MonoError,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 24.dp),
                            style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                        )
                        Button(
                            onClick = { viewModel.fetchReflection() },
                            colors = ButtonDefaults.buttonColors(containerColor = MonoAccent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        ) {
                            Text("RETRY", fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
            reflection != null -> {
                reflection?.let {
                    ScrollableContentWithAutoHideScrollbar(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 20.dp)
                    ) {
                        ReflectionCard(it)
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp),
            scale = true
        )
    }
}

@Composable
fun ScrollableContentWithAutoHideScrollbar(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    var containerHeightPx by remember { mutableIntStateOf(0) }
    var contentHeightPx by remember { mutableIntStateOf(1) }

    var isScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(scrollState.value) {
        isScrolling = true
        kotlinx.coroutines.delay(1500)
        isScrolling = false
    }

    val alphaAnim by animateFloatAsState(if (isScrolling) 1f else 0f, label = "")

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .onGloballyPositioned { coordinates ->
                    containerHeightPx = coordinates.size.height
                },
            content = content
        )

        LaunchedEffect(scrollState.maxValue) {
            if (containerHeightPx > 0) {
                contentHeightPx = containerHeightPx + scrollState.maxValue
            }
        }

        if (contentHeightPx > containerHeightPx) {
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.Transparent)
                    .graphicsLayer { alpha = alphaAnim }
            ) {
                val maxThumbHeightPx = 60.dp.toPx()
                val minThumbHeightPx = 12.dp.toPx()
                val proportionVisible = containerHeightPx.toFloat() / contentHeightPx.toFloat()
                val scrollFraction = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
                val rawThumbHeight = size.height * proportionVisible
                val thumbHeight = rawThumbHeight.coerceIn(minThumbHeightPx, maxThumbHeightPx)
                val thumbOffset = (size.height - thumbHeight) * scrollFraction

                drawRoundRect(
                    color = Color.DarkGray.copy(alpha = 0.7f),
                    topLeft = Offset(x = 0f, y = thumbOffset),
                    size = androidx.compose.ui.geometry.Size(width = size.width, height = thumbHeight),
                    cornerRadius = CornerRadius(3.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun ReflectionCard(reflection: com.example.dailyreflection.model.ReflectionData) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = MonoCardBg,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, MonoAccent)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    reflection.title,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonoText
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    formatDateFromBackend(reflection.date),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MonoAccent
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Bacaan: ${reflection.passage}",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MonoText
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                reflection.content,
                fontSize = 20.sp,
                color = MonoText,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Sumber: ${reflection.source}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                color = MonoAccent,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

fun formatDateFromBackend(dateStr: String): String {
    return try {
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        val date = LocalDate.parse(dateStr, inputFormatter)
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault())
        date.format(outputFormatter)
    } catch (_: Exception) {
        dateStr.take(10)
    }
}