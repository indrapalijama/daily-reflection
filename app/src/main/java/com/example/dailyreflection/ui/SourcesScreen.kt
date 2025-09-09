package com.example.dailyreflection.ui

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
            .padding(horizontal = 8.dp, vertical = 20.dp)
    ) {
        items(reflectionSources) { source ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        currentSelected = source.id
                        onSourceSelected(source.id)
                    },
                color = if (currentSelected == source.id) MonoAccent.copy(alpha = 0.2f) else MonoCardBg,
                border = BorderStroke(2.dp, MonoAccent),
                tonalElevation = if (currentSelected == source.id) 4.dp else 0.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = source.displayName,
                    color = MonoText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 24.dp)
                )
            }
        }
    }
}