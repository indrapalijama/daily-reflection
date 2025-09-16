package fulk.evilcorp.dailyreflection.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
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
import fulk.evilcorp.dailyreflection.viewmodel.ReflectionViewModel
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import fulk.evilcorp.dailyreflection.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflectionScreen(
    viewModel: ReflectionViewModel,
    isFullscreen: Boolean = false,
    onFullscreenToggle: () -> Unit = {}
) {
    val reflection by viewModel.reflection
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val isFavorite by viewModel.isFavorite
    val isRefreshing = isLoading

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.fetchReflection() }
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .background(MaterialTheme.colorScheme.background)
                .pullRefresh(pullRefreshState)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
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
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 24.dp),
                                style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                            )
                            Button(
                                onClick = { viewModel.fetchReflection() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                            ) {
                                Text(
                                    "Coba lagi",
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
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
                            ReflectionCard(
                                reflection = it,
                                isFavorite = isFavorite,
                                onToggleFavorite = {
                                    viewModel.toggleFavorite(it)

                                    coroutineScope.launch {
                                        val message = if (!isFavorite) {
                                            "Berhasil ditambahkan ke Favorit"
                                        } else {
                                            "Berhasil dihapus dari Favorit"
                                        }

                                        snackbarHostState.showSnackbar(
                                            message = message,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = onFullscreenToggle,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = if (isFullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                    contentDescription = if (isFullscreen) "Exit Fullscreen" else "Enter Fullscreen",
                    modifier = Modifier.size(24.dp)
                )
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
    val scrollbarColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)

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
                    color = scrollbarColor,
                    topLeft = Offset(x = 0f, y = thumbOffset),
                    size = androidx.compose.ui.geometry.Size(width = size.width, height = thumbHeight),
                    cornerRadius = CornerRadius(3.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun ReflectionCard(
    reflection: fulk.evilcorp.dailyreflection.model.ReflectionData,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = formatDateFromBackendWithYear(reflection.date),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = reflection.title,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 40.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Bacaan: ${reflection.passage}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = reflection.content,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sumber: ${reflection.source}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) "Hapus dari Favorit" else "Tambah ke Favorit",
                    tint = if (isFavorite) Color(0xFFF44336) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

fun formatDateFromBackendWithYear(dateStr: String): String {
    return try {
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        val date = LocalDate.parse(dateStr, inputFormatter)
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
        date.format(outputFormatter)
    } catch (_: Exception) {
        try {
            val datePart = dateStr.take(10)
            val parts = datePart.split("-")
            if (parts.size == 3) {
                val year = parts[0]
                val month = parts[1].toInt()
                val day = parts[2].toInt()
                val monthNames = arrayOf(
                    "", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
                )
                "$day ${monthNames[month]} $year"
            } else {
                dateStr.take(10)
            }
        } catch (_: Exception) {
            dateStr.take(10)
        }
    }
}