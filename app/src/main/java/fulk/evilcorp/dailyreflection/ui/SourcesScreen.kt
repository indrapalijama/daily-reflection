package fulk.evilcorp.dailyreflection.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class ReflectionSource(val id: String, val displayName: String)

val reflectionSources = listOf(
    ReflectionSource("sh", "Santapan Harian"),
    ReflectionSource("rh", "Renungan Harian"),
    ReflectionSource("roc", "Renungan Oswald Chambers")
)

@Composable
fun SourcesScreen(
    selectedSourceId: String? = null,
    onSourceSelected: (String) -> Unit = {}
) {
    var currentSelected by remember { mutableStateOf(selectedSourceId ?: reflectionSources.first().id) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        items(reflectionSources) { source ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        currentSelected = source.id
                        onSourceSelected(source.id)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (currentSelected == source.id)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else
                        MaterialTheme.colorScheme.surface
                ),
                border = if (currentSelected == source.id)
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                else
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Text(
                    text = source.displayName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(vertical = 20.dp, horizontal = 24.dp)
                )
            }
        }
    }
}