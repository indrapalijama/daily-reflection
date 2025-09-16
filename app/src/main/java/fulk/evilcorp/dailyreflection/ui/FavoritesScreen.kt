package fulk.evilcorp.dailyreflection.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fulk.evilcorp.dailyreflection.data.local.FavoriteReflection
import fulk.evilcorp.dailyreflection.viewmodel.FavoritesViewModel
import java.text.SimpleDateFormat
import java.util.*
import fulk.evilcorp.dailyreflection.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onFavoriteClick: (FavoriteReflection) -> Unit = {}
) {
    val favorites by viewModel.favorites.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var favoriteToDelete by remember { mutableStateOf<FavoriteReflection?>(null) }

    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.empty),
                    contentDescription = "Empty illustration",
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Belum ada renungan favorit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(favorites, key = { it.id }) { favorite ->
                SwipeToDeleteFavoriteCard(
                    favorite = favorite,
                    onClick = { onFavoriteClick(favorite) },
                    onSwipeToDelete = {
                        favoriteToDelete = favorite
                        showDeleteDialog = true
                    }
                )
            }
        }
    }

    if (showDeleteDialog && favoriteToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                favoriteToDelete = null
            },
            title = {
                Text(
                    text = "Hapus Favorit",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Apakah Anda yakin ingin menghapus renungan ini dari favorit?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${favoriteToDelete!!.title}\"",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        favoriteToDelete?.let { viewModel.removeFavorite(it.id) }
                        showDeleteDialog = false
                        favoriteToDelete = null
                    }
                ) {
                    Text(
                        "Hapus",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        favoriteToDelete = null
                    }
                ) {
                    Text(
                        "Batal",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteFavoriteCard(
    favorite: FavoriteReflection,
    onClick: () -> Unit,
    onSwipeToDelete: () -> Unit
) {
    val swipeToDismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onSwipeToDelete()
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissState,
        modifier = Modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error, shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Hapus",
                        color = MaterialTheme.colorScheme.onError,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) {
        FavoriteCard(
            favorite = favorite,
            onClick = onClick
        )
    }
}

@Composable
private fun FavoriteCard(
    favorite: FavoriteReflection,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = favorite.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${favorite.source} (${favorite.passage})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ditambahkan pada ${formatDateAdded(favorite.dateAdded)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatDateAdded(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (_: Exception) {
        "Unknown"
    }
}