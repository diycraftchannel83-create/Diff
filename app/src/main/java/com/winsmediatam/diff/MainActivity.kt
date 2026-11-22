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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.compose.runtime.collectAsState

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
    val historyRepository = remember(context) { GenerationHistoryRepository(context) }
    var localModels by remember { mutableStateOf(repository.list()) }
    var importError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val downloadWorkInfos by remember(context) { WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(OfflineModelDownloadWorker.WORK_NAME) }.collectAsState(initial = emptyList())
    val downloadWorkInfo = downloadWorkInfos.firstOrNull()
    val isDownloading = downloadWorkInfo?.state == WorkInfo.State.RUNNING || downloadWorkInfo?.state == WorkInfo.State.ENQUEUED
    val downloadProgress = (downloadWorkInfo?.progress?.getInt(OfflineModelDownloadWorker.KEY_PERCENT, 0) ?: 0) / 100f
    val downloadedBytes = downloadWorkInfo?.progress?.getLong(OfflineModelDownloadWorker.KEY_DOWNLOADED, 0L) ?: 0L
    val downloadTotalBytes = downloadWorkInfo?.progress?.getLong(OfflineModelDownloadWorker.KEY_TOTAL, OfflineModelDownloader.MODEL_ZIP_SIZE) ?: OfflineModelDownloader.MODEL_ZIP_SIZE
    val downloadError = if (downloadWorkInfo?.state == WorkInfo.State.FAILED) "Background download failed. Try again; partial download may resume." else null
    LaunchedEffect(downloadWorkInfo?.state) {
        if (downloadWorkInfo?.state == WorkInfo.State.SUCCEEDED) localModels = repository.list()
    }
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
    var sampler by remember { mutableStateOf("auto") }
    var scheduler by remember { mutableStateOf("auto") }
    var showAdvanced by remember { mutableStateOf(false) }
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var generating by remember { mutableStateOf(false) }
    var generationProgress by remember { mutableFloatStateOf(0f) }
    var generationMessage by remember { mutableStateOf("Select a compatible GGUF model to begin.") }
    var loadedModelPath by remember { mutableStateOf<String?>(null) }

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
                0 -> CreateScreen(Modifier.padding(padding), prompt, { prompt = it }, negativePrompt, { negativePrompt = it }, localModels, selectedModelId, { id -> selectedModelId = id; selectionPrefs.edit().putString("selected_model_id", id).apply() }, steps, { steps = it }, cfg, { cfg = it }, width, { width = it }, height, { height = it }, seed, { seed = it }, showAdvanced, { showAdvanced = it }, generatedBitmap, generating, generationMessage, { bitmap -> generatedBitmap = bitmap }, { generating = it }, { generationMessage = it }, { loadedModelPath = it }, generationProgress, { generationProgress = it }, historyRepository, sampler, { sampler = it }, scheduler, { scheduler = it })
                1 -> GalleryScreen(Modifier.padding(padding), historyRepository) { entry -> prompt = entry.prompt; negativePrompt = entry.negativePrompt; selectedTab = 0 }
                2 -> ModelManagerScreen(Modifier.padding(padding), localModels, { modelPicker.launch(arrayOf("*/*")) }, { item -> repository.delete(item); localModels = repository.list() }, importError, isDownloading, downloadProgress, downloadedBytes, downloadTotalBytes, downloadError, {
                    OfflineModelDownloadWorker.enqueue(context)
                }, { OfflineModelDownloadWorker.cancel(context) })
                else -> SettingsScreen(Modifier.padding(padding))
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
    seed: String, onSeed: (String) -> Unit, advanced: Boolean, onAdvanced: (Boolean) -> Unit,
    generatedBitmap: Bitmap?, generating: Boolean, generationMessage: String, onBitmap: (Bitmap?) -> Unit, onGenerating: (Boolean) -> Unit, onGenerationMessage: (String) -> Unit, onLoadedModel: (String?) -> Unit, generationProgress: Float, onProgress: (Float) -> Unit, historyRepository: GenerationHistoryRepository, sampler: String, onSampler: (String) -> Unit, scheduler: String, onScheduler: (String) -> Unit
) {
    var modelMenuExpanded by remember { mutableStateOf(false) }
    var presetMenuExpanded by remember { mutableStateOf(false) }
    val promptPresets = PromptPresets.all
    val userPresetRepository = remember(context) { PromptPresetRepository(context) }
    var userPresets by remember { mutableStateOf(userPresetRepository.list()) }
    var showSavePreset by remember { mutableStateOf(false) }
    var showManagePresets by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }
    val selectedModel = models.firstOrNull { it.id == selectedModelId }
    val coroutineScope = rememberCoroutineScope()
    val deviceCapabilities = remember(context) { DeviceCapabilities.current(context) }
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
                    Box {
                        TextButton(onClick = { presetMenuExpanded = true }) { Icon(Icons.Default.AutoAwesome, null); Spacer(Modifier.width(6.dp)); Text("Prompt presets") }
                        DropdownMenu(expanded = presetMenuExpanded, onDismissRequest = { presetMenuExpanded = false }) {
                            promptPresets.forEach { (name, preset) -> DropdownMenuItem(text = { Text(name) }, onClick = { onPrompt(if (prompt.isBlank()) preset else "$prompt, $preset"); presetMenuExpanded = false }) }
                            userPresets.forEach { item -> DropdownMenuItem(text = { Text("★ " + item.name) }, onClick = { onPrompt(item.prompt); presetMenuExpanded = false }) }
                        }
                    }
                    OutlinedTextField(value = prompt, onValueChange = onPrompt, modifier = Modifier.fillMaxWidth(), minLines = 4, placeholder = { Text("Describe the image you want to create…") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { presetName = ""; showSavePreset = true }, enabled = prompt.isNotBlank()) { Text("Save custom preset") }
                        if (userPresets.isNotEmpty()) OutlinedButton(onClick = { showManagePresets = true }) { Text("Manage presets") }
                    }
                    Text("Negative prompt", fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = negative, onValueChange = onNegative, modifier = Modifier.fillMaxWidth(), minLines = 2, placeholder = { Text("Optional: elements to avoid") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedButton(onClick = { imagePicker.launch("image/*") }) { Text(if (initImagePath == null) "Add image-to-image source" else "Change source image") }; if (initImagePath != null) OutlinedButton(onClick = { initImagePath = null; initImagePreview = null }) { Text("Remove source") } }
                    initImagePreview?.let { Image(it.asImageBitmap(), contentDescription = "Image-to-image source", modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp)) }
                    if (initImagePath != null) { Text("Image-to-image strength: ${(imageStrength * 100).toInt()}%"); Slider(value = imageStrength, onValueChange = { imageStrength = it }, valueRange = 0.05f..1f) }
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
                        Text("Sampler", color = TextMuted, fontSize = 12.sp)
                        SettingsDropdown("Sampler", sampler, listOf("auto", "euler", "euler_a", "heun", "dpm2", "dpm++2m", "lcm", "ddim_trailing", "tcd", "lms"), onSampler)
                        Text("Scheduler", color = TextMuted, fontSize = 12.sp)
                        SettingsDropdown("Scheduler", scheduler, listOf("auto", "discrete", "karras", "exponential", "ays", "gits", "sgm_uniform", "simple", "smoothstep", "kl_optimal", "lcm", "beta"), onScheduler)
                    }
                }
            }
        }
        item {
            generatedBitmap?.let { bitmap ->
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(18.dp)) {
                    Image(bitmap.asImageBitmap(), contentDescription = "Generated image preview", modifier = Modifier.fillMaxWidth())
                }
            }
            Button(onClick = {
                if (prompt.isBlank()) { onGenerationMessage("Please enter a prompt first."); return@Button }
                if (selectedModel == null) { onGenerationMessage("Select a local model first."); return@Button }
                val requestedWidth = width.toIntOrNull()
                val requestedHeight = height.toIntOrNull()
                if (requestedWidth == null || requestedHeight == null || requestedWidth !in 256..1024 || requestedHeight !in 256..1024 || requestedWidth % 64 != 0 || requestedHeight % 64 != 0) {
                    onGenerationMessage("Resolution must be 256–1024 and divisible by 64.")
                    return@Button
                }
                val activityManager = context.getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
                val memoryInfo = android.app.ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(memoryInfo)
                val requiredMemoryMb = (requestedWidth.toLong() * requestedHeight * 8L / (1024L * 1024L)).coerceAtLeast(512L)
                val availableMemoryMb = memoryInfo.availMem / (1024L * 1024L)
                if (memoryInfo.lowMemory || availableMemoryMb < requiredMemoryMb) {
                    onGenerationMessage("Not enough available RAM for ${requestedWidth}×${requestedHeight}. Available ${availableMemoryMb} MB; estimated safe minimum ${requiredMemoryMb} MB. Try 512×512.")
                    return@Button
                }
                onGenerating(true); onProgress(0f); onGenerationMessage("RAM check passed (${availableMemoryMb} MB available). Loading model and generating…")
                coroutineScope.launch {
                    try {
                        if (loadedModelPath != selectedModel.id) {
                            withContext(Dispatchers.IO) {
                                NativeRuntime.loadModel(selectedModel.id, Runtime.getRuntime().availableProcessors().coerceIn(1, 4))
                            }
                            onLoadedModel(selectedModel.id)
                            onGenerationMessage("Model loaded. Starting generation…")
                        }
                        val parsedSeed = seed.toLongOrNull()?.takeIf { it >= 0L } ?: -1L
                        val generationJob = coroutineScope.async(Dispatchers.IO) {
                            NativeRuntime.generateBitmap(prompt, negative, requestedWidth, requestedHeight, steps.toInt(), cfg, parsedSeed, sampler, scheduler, initImagePath ?: "", imageStrength)
                        }
                        var bitmap: Bitmap? = null
                        while (generationJob.isActive) {
                            val progress = withContext(Dispatchers.IO) { NativeRuntime.getGenerationProgress() }
                            if (progress.size >= 2 && progress[1] > 0) onProgress((progress[0].toFloat() / progress[1]).coerceIn(0f, 1f))
                            delay(400)
                        }
                        bitmap = generationJob.await()
                        onBitmap(bitmap)
                        if (bitmap != null) withContext(Dispatchers.IO) { historyRepository.save(bitmap, prompt, negative, requestedWidth, requestedHeight, steps.toInt(), cfg, parsedSeed) }
                        onProgress(1f)
                        onGenerationMessage(if (bitmap != null) "Generation complete." else "No image was returned.")
                    } catch (e: CancellationException) {
                        onGenerationMessage("Generation cancelled.")
                    } catch (e: Exception) {
                        onGenerationMessage(e.message ?: "Generation failed.")
                    } finally { onGenerating(false) }
                }
            }, enabled = !generating, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(15.dp)) {
                if (generating) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) else Icon(Icons.Default.AutoAwesome, null)
                Spacer(Modifier.width(8.dp)); Text(if (generating) "GENERATING…" else "GENERATE IMAGE")
            }
            if (generating) {
                LinearProgressIndicator(progress = { generationProgress }, modifier = Modifier.fillMaxWidth())
                Text("Sampling ${(generationProgress * 100).toInt()}%", color = TextMuted, fontSize = 12.sp)
                OutlinedButton(onClick = { NativeRuntime.cancelGeneration(); onGenerationMessage("Cancellation requested…") }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Stop, null); Spacer(Modifier.width(8.dp)); Text("CANCEL GENERATION")
                }
            }
            Text(generationMessage, color = TextMuted, fontSize = 12.sp)
            Text("Native ARM64 inference is connected. Model compatibility depends on the selected checkpoint.", color = TextMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun DimensionField(label: String, value: String, onValue: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(value = value, onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) onValue(it) }, modifier = modifier, label = { Text(label) }, singleLine = true)
}

@Composable
private fun ModelManagerScreen(modifier: Modifier, models: List<LocalModel>, onImport: () -> Unit, onDelete: (LocalModel) -> Unit, error: String?, downloading: Boolean, progress: Float, downloaded: Long, total: Long, downloadError: String?, onDownload: () -> Unit, onCancelDownload: () -> Unit) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 18.dp), contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("LOCAL MODELS", fontWeight = FontWeight.Black, fontSize = 22.sp)
            Text("Import compatible model files. Files are stored privately in app storage.", color = TextMuted, fontSize = 13.sp)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("DOWNLOAD MODEL OFFLINE", color = Accent, fontWeight = FontWeight.Bold)
                    Text("Stable Diffusion 1.5 Q8 GGUF • ~1.88 GB • CreativeML OpenRAIL-M", color = TextMuted, fontSize = 12.sp)
                    Text("Download inside the app. ZIP extraction and model import are automatic. If canceled or interrupted, the partial download is kept and resumed on the next attempt when the server supports Range requests.", fontSize = 13.sp)
                    if (downloading) {
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                        Text("${(progress * 100).toInt()}% • ${ModelRepository.formatSize(downloaded)} / ${ModelRepository.formatSize(total)}", color = TextMuted, fontSize = 12.sp)
                        OutlinedButton(onClick = onCancelDownload, modifier = Modifier.fillMaxWidth()) { Text("Cancel download") }
                    } else Button(onClick = onDownload, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Download, null); Spacer(Modifier.width(8.dp)); Text("Download SD 1.5 Offline Model") }
                    if (downloadError != null) Text(downloadError, color = Color(0xFFFF6B6B), fontSize = 12.sp)
                    Text("Source: Hugging Face • Internet required • Allow about 4 GB free. Cancel keeps partial data for resume.", color = TextMuted, fontSize = 11.sp)
                }
            }
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
private fun SettingsDropdown(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("$label: $selected")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelect(option); expanded = false })
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
