package com.winsmediatam.diff

import android.os.Bundle
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
    var selectedTab by remember { mutableStateOf(0) }
    var prompt by remember { mutableStateOf("") }
    var negativePrompt by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("Model not installed") }
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
                0 -> CreateScreen(Modifier.padding(padding), prompt, { prompt = it }, negativePrompt, { negativePrompt = it }, model, { model = it }, steps, { steps = it }, cfg, { cfg = it }, width, { width = it }, height, { height = it }, seed, { seed = it }, showAdvanced, { showAdvanced = it })
                1 -> EmptyScreen(Modifier.padding(padding), "Gallery", "Generated images will appear here.")
                2 -> EmptyScreen(Modifier.padding(padding), "Model Manager", "Import or download compatible models to your device.")
                else -> EmptyScreen(Modifier.padding(padding), "Settings", "Local generation preferences and app configuration.")
            }
        }
    }
}

@Composable
private fun CreateScreen(
    modifier: Modifier, prompt: String, onPrompt: (String) -> Unit,
    negative: String, onNegative: (String) -> Unit, model: String, onModel: (String) -> Unit,
    steps: Float, onSteps: (Float) -> Unit, cfg: Float, onCfg: (Float) -> Unit,
    width: String, onWidth: (String) -> Unit, height: String, onHeight: (String) -> Unit,
    seed: String, onSeed: (String) -> Unit, advanced: Boolean, onAdvanced: (Boolean) -> Unit
) {
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
                    OutlinedButton(onClick = { onModel(if (model == "Model not installed") "Choose model (coming soon)" else "Model not installed" ) }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Storage, null); Spacer(Modifier.width(8.dp)); Text(model)
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
            Text("UI scaffold is ready. Local model inference is the next implementation phase.", color = TextMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun DimensionField(label: String, value: String, onValue: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(value = value, onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) onValue(it) }, modifier = modifier, label = { Text(label) }, singleLine = true)
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
