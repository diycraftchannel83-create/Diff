package com.winsmediatam.diff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(modifier: Modifier) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 18.dp), contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("SETTINGS & STATUS", style = MaterialTheme.typography.headlineSmall)
            Text("Runtime and privacy information", style = MaterialTheme.typography.bodySmall)
        }
        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("NATIVE RUNTIME", style = MaterialTheme.typography.titleMedium)
                    Text(NativeRuntime.status())
                    Text("Architecture: ${NativeRuntime.architecture()}")
                    Text("Native library available: ${NativeRuntime.isAvailable()}")
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("OFFLINE & PRIVACY", style = MaterialTheme.typography.titleMedium)
                    Text("Imported model files and generated history are stored in app-private storage. Prompt text and generated images are not sent to a cloud service by this app's local workflow.")
                    Text("Model downloads require an internet connection. Third-party model license and usage restrictions apply.")
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("VALIDATION STATUS", style = MaterialTheme.typography.titleMedium)
                    Text("Source integration is in progress. Build, native inference, memory behavior, export/share and device compatibility are not yet verified.")
                }
            }
        }
    }
}
