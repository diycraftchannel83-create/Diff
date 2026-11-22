package com.winsmediatam.diff

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun MaskEditorDialog(source: Bitmap, onDismiss: () -> Unit, onApply: (Bitmap) -> Unit) {
    val strokes = remember { mutableStateListOf<List<Offset>>() }
    val current = remember { mutableStateListOf<Offset>() }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(Modifier.fillMaxWidth().padding(14.dp)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Inpainting Mask", style = MaterialTheme.typography.titleLarge)
                Text("Paint over areas to replace. White strokes represent the editable mask.", style = MaterialTheme.typography.bodySmall)
                Canvas(Modifier.fillMaxWidth().height(320.dp).pointerInput(Unit) {
                    detectDragGestures(onDragStart = { point -> current.clear(); current.add(Offset(point.x / size.width, point.y / size.height)) }, onDrag = { change, _ -> current.add(Offset(change.position.x / size.width, change.position.y / size.height)); change.consume() }, onDragEnd = { if (current.size > 1) strokes.add(current.toList()); current.clear() }, onDragCancel = { current.clear() })
                }) {
                    drawImage(source.asImageBitmap(), dstSize = androidx.compose.ui.unit.IntSize(size.width.toInt(), size.height.toInt()))
                    val all = strokes + listOf(current.toList())
                    all.forEach { points ->
                        if (points.size > 1) {
                            val path = Path().apply { moveTo(points.first().x * size.width, points.first().y * size.height); points.drop(1).forEach { lineTo(it.x * size.width, it.y * size.height) } }
                            drawPath(path, Color.Red.copy(alpha = 0.65f), style = Stroke(width = 20.dp.toPx()))
                            drawPath(path, Color.White, style = Stroke(width = 12.dp.toPx()))
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { strokes.clear(); current.clear() }) { Text("Clear mask") }
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Button(onClick = {
                        val mask = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
                        val canvas = android.graphics.Canvas(mask)
                        canvas.drawColor(android.graphics.Color.BLACK)
                        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.WHITE; style = android.graphics.Paint.Style.STROKE; strokeCap = android.graphics.Paint.Cap.ROUND; strokeJoin = android.graphics.Paint.Join.ROUND; strokeWidth = maxOf(source.width, source.height) * 0.035f }
                        (strokes + listOf(current.toList())).forEach { points ->
                            if (points.size > 1) {
                                val path = android.graphics.Path()
                                path.moveTo(points.first().x * source.width, points.first().y * source.height)
                                points.drop(1).forEach { path.lineTo(it.x * source.width, it.y * source.height) }
                                canvas.drawPath(path, paint)
                            }
                        }
                        onApply(mask)
                    }) { Text("Apply mask") }
                }
            }
        }
    }
}
