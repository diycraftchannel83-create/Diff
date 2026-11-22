package com.winsmediatam.diff

import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF0B0D12)
private val SurfaceDark = Color(0xFF171A22)
private val Accent = Color(0xFF8B5CF6)
private val TextMuted = Color(0xFF9298A8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DiffApp() }
    }
}

@Composable
fun DiffApp() {
    val context = LocalContext.current
    val repository = remember(context) { ModelRepository(context) }
    var localModels by remember { mutableStateOf(repository.list()) }
    var importError by remember { mutableStateOf<String?>(null) }
    val modelPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching { repository.importModel(uri) }
                .onSuccess { localModels = repository.list(); importError = null }
                .onFailure { importError = it.message ?: "Import failed" }
        }
    }
    var selectedTab by remember { mutableStateOf(0) }
    var prompt by remember { mutableStateOf("") }
    var negativePrompt by remember { mutableStateOf("") }
    val selectionPrefs = remember(context) { context.getSharedPreferences("diff_selection", android.content.Context.MODE_PRIVATE) }
    var selectedModelId by remember { mutableStateOf(selectionPrefs.getString("selected_model_id", null)) }
    val selectedModel = localModels.firstOrNull { it.id == selectedModelId }
    LaunchedEffect(localModels, selectedModelId) {
        if (selectedModelId != null && localModels.none { it.id == selectedModelId }) {
            selectedModelId = null
            selectionPrefs.edit().remove("selected_model_id").apply()
        }
    }
    var steps by remember { mutableFloatStateOf(25f) }
    var cfg by remember { mutableFloatStateOf(7f) }
    var width by remember { mutableStateOf("512") }
    var height by remember { mutableStateOf("512") }
    var seed by remember { mutableStateOf("-1") }
    var showAdvanced by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme(primary = Accent, background = Bg, surface = SurfaceDark)) {
        Scaffold(containerColor = Bg, bottomBar = {
            NavigationBar(containerColor = SurfaceDark) {
                val items = listOf("Create", "Gallery", "Models", "Settings")
                val icons = listOf(Icons.Default.AutoAwesome, Icons.Default.Image, Icons.Default.Storage, Icons.Default.Settings)
                items.forEachIndexed { i, label ->
                    NavigationBarItem(selected = selectedTab == i, onClick = { selectedTab = i },
                        icon = { Icon(icons[i], label) }, label = { Text(label) })
                }
            }
        }) { padding ->
            when (selectedTab) {
                0 -> CreateScreen(Modifier.padding(padding), prompt, { prompt = it }, negativePrompt, { negativePrompt = it }, localModels, selectedModelId, { id -> selectedModelId = id; selectionPrefs.edit().putString("selected_model_id", id).apply() }, steps, { steps = it }, cfg, { cfg = it }, width, { width = it }, height, { height = it }, seed, { seed = it }, showAdvanced, { showAdvanced = it })
                1 -> EmptyScreen(Modifier.padding(padding), "Gallery", "Generated images will appear here.")
                2 -> ModelManagerScreen(Modifier.padding(padding), localModels, { modelPicker.launch(arrayOf("*/*")) }, { item -> repository.delete(item); localModels = repository.list() }, importError)
                else -> EmptyScreen(Modifier.padding(padding), "Settings", "Local generation preferences and app configuration.")
            }
        }
    }
}

@Composable
private fun CreateScreen(
    modifier: Modifier, prompt: String, onPrompt: (String) -> Unit,
    negative: String, onNegative: (String) -> Unit, models: List<LocalModel>, selectedModelId: String?, onModelSelected: (String?) -> Unit,
    steps: Float, onSteps: (Float) -> Unit, cfg: Float, onCfg: (Float) -> Unit,
    width: String, onWidth: (String) -> Unit, height: String, onHeight: (String) -> Unit,
    seed: String, onSeed: (String) -> Unit, advanced: Boolean, onAdvanced: (Boolean) -> Unit
) {
    var modelMenuExpanded by remember { mutableStateOf(false) }
    val selectedModel = models.firstOrNull { it.id == selectedModelId }
    val deviceCapabilities = remember { DeviceCapabilities.current(LocalContext.current) }
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 18.dp), contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(42.dp).background(Accent, RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.AutoAwesome, null, tint = Color.White) }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("DIFF", fontWeight = FontWeight.Black, fontSize = 22.sp, letterSpacing = 2.sp)
                    Text("AI IMAGE STUDIO", color = TextMuted, fontSize = 11.sp, letterSpacing = 1.5.sp)
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Prompt", fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = prompt, onValueChange = onPrompt, modifier = Modifier.fillMaxWidth(), minLines = 4, placeholder = { Text("Describe the image you want to create…") })
                    Text("Negative prompt", fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = negative, onValueChange = onNegative, modifier = Modifier.fillMaxWidth(), minLines = 2, placeholder = { Text("Optional: elements to avoid") })
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Model", fontWeight = FontWeight.Bold)
                    Box(Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { modelMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Storage, null); Spacer(Modifier.width(8.dp)); Text(selectedModel?.name ?: "Select imported model")
                        }
                        DropdownMenu(expanded = modelMenuExpanded, onDismissRequest = { modelMenuExpanded = false }) {
                            if (models.isEmpty()) DropdownMenuItem(text = { Text("No imported models") }, onClick = { modelMenuExpanded = false })
                            models.forEach { item -> DropdownMenuItem(text = { Text(item.name) }, onClick = { onModelSelected(item.id); modelMenuExpanded = false }) }
                            if (selectedModelId != null) DropdownMenuItem(text = { Text("Clear selection") }, onClick = { onModelSelected(null); modelMenuExpanded = false })
                        }
                    }
                    selectedModel?.let { Text("Selected file: ${it.fileName} • ${ModelRepository.formatSize(it.sizeBytes)}", color = TextMuted, fontSize = 12.sp) }
                    Card(colors = CardDefaults.cardColors(containerColor = Bg), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("DEVICE READINESS", color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("RAM: ${deviceCapabilities.totalRamMb} MB • Available now: ${deviceCapabilities.availableRamMb} MB", fontSize = 12.sp)
                            Text("Architecture: ${deviceCapabilities.architecture} • ${deviceCapabilities.recommendedPreset}", color = TextMuted, fontSize = 12.sp)
                            Text("Suggested starting resolution: ${deviceCapabilities.suggestedResolution}", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DimensionField("Width", width, onWidth, Modifier.weight(1f))
                        DimensionField("Height", height, onHeight, Modifier.weight(1f))
                    }
                    Text("Steps: ${steps.toInt()}")
                    Slider(value = steps, onValueChange = onSteps, valueRange = 1f..60f)
                    Text("CFG Scale: ${"%.1f".format(cfg)}")
                    Slider(value = cfg, onValueChange = onCfg, valueRange = 1f..20f)
                    TextButton(onClick = { onAdvanced(!advanced) }) { Text(if (advanced) "Hide advanced settings" else "Advanced settings") }
                    if (advanced) {
                        OutlinedTextField(value = seed, onValueChange = onSeed, modifier = Modifier.fillMaxWidth(), label = { Text("Seed (-1 = random)") })
                        Text("Sampler and scheduler selection will be added with the inference engine.", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
        item {
            Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(15.dp)) {
                Icon(Icons.Default.AutoAwesome, null); Spacer(Modifier.width(8.dp)); Text("GENERATION ENGINE NOT READY")
            }
            Text(if (selectedModel == null) "Import a compatible model and select it above. Inference is not yet connected." else "Model selection is saved. Native loading and inference integration are next.", color = TextMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun DimensionField(label: String, value: String, onValue: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(value = value, onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) onValue(it) }, modifier = modifier, label = { Text(label) }, singleLine = true)
}

@Composable
private fun ModelManagerScreen(modifier: Modifier, models: List<LocalModel>, onImport: () -> Unit, onDelete: (LocalModel) -> Unit, error: String?) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 18.dp), contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("LOCAL MODELS", fontWeight = FontWeight.Black, fontSize = 22.sp)
            Text("Import compatible model files. Files are stored privately in app storage.", color = TextMuted, fontSize = 13.sp)
        }
        item {
            Button(onClick = onImport, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.FileOpen, null); Spacer(Modifier.width(8.dp)); Text("Import model file")
            }
        }
        if (error != null) item { Text(error, color = Color(0xFFFF6B6B)) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Formats", fontWeight = FontWeight.Bold)
                    Text("GGUF, SafeTensors, checkpoint files. Compatibility is engine/model dependent; inference engine integration is still in progress.", color = TextMuted, fontSize = 12.sp)
                }
            }
        }
        if (models.isEmpty()) item { Text("No local models yet.", color = TextMuted) }
        items(models, key = { it.id }) { model ->
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, null, tint = Accent, modifier = Modifier.size(30.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(model.name, fontWeight = FontWeight.SemiBold)
                        Text("${model.fileName} • ${ModelRepository.formatSize(model.sizeBytes)}", color = TextMuted, fontSize = 12.sp)
                    }
                    IconButton(onClick = { onDelete(model) }) { Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFFF6B6B)) }
                }
            }
        }
    }
}

@Composable
private fun EmptyScreen(modifier: Modifier, title: String, message: String) {
    Column(modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.AutoAwesome, null, tint = Accent, modifier = Modifier.size(52.dp))
        Spacer(Modifier.height(16.dp))
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(message, color = TextMuted)
    }
}
