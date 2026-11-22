package com.winsmediatam.diff

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun GalleryScreen(modifier: Modifier, history: GenerationHistoryRepository, onReusePrompt: (GenerationHistoryEntry) -> Unit) {
    var entries by remember { mutableStateOf(history.list()) }
    var actionMessage by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }
    var favoritesOnly by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf("Newest") }
    var expandedMetadata by remember { mutableStateOf<Set<String>>(emptySet()) }
    var sortMenu by remember { mutableStateOf(false) }
    val filteredEntries = entries.filter { (!favoritesOnly || it.favorite) && (searchText.isBlank() || it.prompt.contains(searchText, true) || it.negativePrompt.contains(searchText, true)) }.let { list -> when (sortOrder) { "Oldest" -> list.sortedBy { it.createdAt }; "Favorites first" -> list.sortedByDescending { it.favorite }; else -> list.sortedByDescending { it.createdAt } } }
    val context = androidx.compose.ui.platform.LocalContext.current
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("IMAGE HISTORY", style = MaterialTheme.typography.headlineSmall)
            Text("Generated images are saved privately on this device.", style = MaterialTheme.typography.bodySmall)
        }
        item { OutlinedTextField(value = searchText, onValueChange = { searchText = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Search prompts") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { FilterChip(selected = favoritesOnly, onClick = { favoritesOnly = !favoritesOnly }, label = { Text("Favorites") }, leadingIcon = { Icon(Icons.Default.Favorite, null) }); Box { OutlinedButton(onClick = { sortMenu = true }) { Text("Sort: $sortOrder") }; DropdownMenu(expanded = sortMenu, onDismissRequest = { sortMenu = false }) { listOf("Newest", "Oldest", "Favorites first").forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { sortOrder = option; sortMenu = false }) } } } } }
        actionMessage?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }
        if (entries.isEmpty()) item { Text("No saved images yet. Generate an image to add it here.") } else if (filteredEntries.isEmpty()) item { Text("No images match this filter.") }
        items(filteredEntries, key = { it.id }) { entry ->
            val bitmap = remember(entry.filePath) { BitmapFactory.decodeFile(entry.filePath) }
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (bitmap != null) Image(bitmap.asImageBitmap(), contentDescription = entry.prompt, modifier = Modifier.fillMaxWidth())
                    Text(entry.prompt, style = MaterialTheme.typography.titleMedium)
                    Text("${entry.width}×${entry.height} • ${entry.steps} steps • CFG ${entry.cfg} • Seed ${entry.seed}", style = MaterialTheme.typography.bodySmall)
                    Text(entry.negativePrompt.takeIf { it.isNotBlank() }?.let { "Negative: $it" } ?: "No negative prompt", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { expandedMetadata = if (entry.id in expandedMetadata) expandedMetadata - entry.id else expandedMetadata + entry.id }) { Text(if (entry.id in expandedMetadata) "Hide metadata" else "Details / metadata") }
                    if (entry.id in expandedMetadata) {
                        Text("Created: " + java.text.DateFormat.getDateTimeInstance().format(java.util.Date(entry.createdAt)), style = MaterialTheme.typography.bodySmall)
                        Text("Resolution: ${entry.width} × ${entry.height} • Steps: ${entry.steps} • CFG: ${entry.cfg} • Seed: ${entry.seed}", style = MaterialTheme.typography.bodySmall)
                        Text("Prompt: ${entry.prompt}", style = MaterialTheme.typography.bodySmall)
                        Text("Negative prompt: ${entry.negativePrompt.ifBlank { "None" }}", style = MaterialTheme.typography.bodySmall)
                        Text(if (entry.initImagePath != null) "Image-to-image source: available • Strength ${(entry.strength * 100).toInt()}%" else "Mode: Text-to-image", style = MaterialTheme.typography.bodySmall)\n                        Text(if (entry.maskImagePath != null) "Inpainting mask: saved" else "Mask: none", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(onClick = { val updated = history.toggleFavorite(entry); entries = entries.map { if (it.id == entry.id) updated else it } }) { Icon(Icons.Default.Favorite, null, tint = if (entry.favorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant); Text(if (entry.favorite) "Favorited" else "Favorite") }
                        OutlinedButton(onClick = { onReusePrompt(entry) }) { Text("Reuse prompt") }
                        OutlinedButton(onClick = { runCatching { history.exportToPictures(entry) }.onSuccess { actionMessage = "Exported to Pictures/Diff AI Studio" }.onFailure { actionMessage = it.message ?: "Export failed" } }) { Text("Export") }
                        OutlinedButton(onClick = { runCatching {
                            val fileUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", File(entry.filePath))
                            val shareIntent = Intent(Intent.ACTION_SEND).apply { type = "image/png"; putExtra(Intent.EXTRA_STREAM, fileUri); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
                            context.startActivity(Intent.createChooser(shareIntent, "Share generated image"))
                        }.onFailure { actionMessage = it.message ?: "Share failed" } }) { Text("Share") }
                    }
                    OutlinedButton(onClick = { history.delete(entry); entries = history.list() }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Delete")
                    }
                }
            }
        }
    }
}
