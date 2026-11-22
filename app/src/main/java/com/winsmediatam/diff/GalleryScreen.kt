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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun GalleryScreen(modifier: Modifier, history: GenerationHistoryRepository) {
    var entries by remember { mutableStateOf(history.list()) }
    var actionMessage by remember { mutableStateOf<String?>(null) }
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
        actionMessage?.let { item { Text(it, color = MaterialTheme.colorScheme.primary) } }
        if (entries.isEmpty()) item { Text("No saved images yet. Generate an image to add it here.") }
        items(entries, key = { it.id }) { entry ->
            val bitmap = remember(entry.filePath) { BitmapFactory.decodeFile(entry.filePath) }
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (bitmap != null) Image(bitmap.asImageBitmap(), contentDescription = entry.prompt, modifier = Modifier.fillMaxWidth())
                    Text(entry.prompt, style = MaterialTheme.typography.titleMedium)
                    Text("${entry.width}×${entry.height} • ${entry.steps} steps • CFG ${entry.cfg} • Seed ${entry.seed}", style = MaterialTheme.typography.bodySmall)
                    Text(entry.negativePrompt.takeIf { it.isNotBlank() }?.let { "Negative: $it" } ?: "No negative prompt", style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(onClick = { runCatching { history.exportToPictures(entry) }.onSuccess { actionMessage = \"Exported to Pictures/Diff AI Studio\" }.onFailure { actionMessage = it.message ?: \"Export failed\" } }) { Text(\"Export\") }
                        OutlinedButton(onClick = { runCatching {
                            val fileUri = FileProvider.getUriForFile(context, context.packageName + \".fileprovider\", File(entry.filePath))
                            val shareIntent = Intent(Intent.ACTION_SEND).apply { type = \"image/png\"; putExtra(Intent.EXTRA_STREAM, fileUri); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
                            context.startActivity(Intent.createChooser(shareIntent, \"Share generated image\"))
                        }.onFailure { actionMessage = it.message ?: \"Share failed\" } }) { Text(\"Share\") }
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
