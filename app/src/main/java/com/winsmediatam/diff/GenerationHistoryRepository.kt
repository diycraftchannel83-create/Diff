package com.winsmediatam.diff

import android.content.Context
import android.graphics.Bitmap
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class GenerationHistoryEntry(
    val id: String,
    val filePath: String,
    val prompt: String,
    val negativePrompt: String,
    val width: Int,
    val height: Int,
    val steps: Int,
    val cfg: Float,
    val seed: Long,
    val createdAt: Long,
    val favorite: Boolean = false,
    val initImagePath: String? = null,
    val strength: Float = 0.65f
)

class GenerationHistoryRepository(context: Context) {
    private val appContext = context.applicationContext
    private val directory = File(appContext.filesDir, "generated").apply { mkdirs() }
    private val prefs = appContext.getSharedPreferences("diff_generation_history", Context.MODE_PRIVATE)
    private val key = "entries"

    @Synchronized
    fun save(bitmap: Bitmap, prompt: String, negative: String, width: Int, height: Int, steps: Int, cfg: Float, seed: Long, initImagePath: String? = null, strength: Float = 0.65f, maskImagePath: String? = null): GenerationHistoryEntry {
        val id = UUID.randomUUID().toString()
        val file = File(directory, "$id.png")
        file.outputStream().use { stream ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) { "Could not save generated image" }
        }
        val entry = GenerationHistoryEntry(id, file.absolutePath, prompt, negative, width, height, steps, cfg, seed, System.currentTimeMillis(), false, initImagePath, strength, maskImagePath)
        val entries = list().toMutableList().also { it.add(0, entry) }
        val retained = entries.take(100)
        entries.drop(100).forEach { File(it.filePath).delete() }
        write(retained)
        return entry
    }

    @Synchronized
    fun list(): List<GenerationHistoryEntry> {
        val raw = prefs.getString(key, "[]") ?: "[]"
        val array = runCatching { JSONArray(raw) }.getOrElse { JSONArray() }
        val result = mutableListOf<GenerationHistoryEntry>()
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val entry = runCatching {
                GenerationHistoryEntry(
                    item.getString("id"), item.getString("file"), item.optString("prompt"),
                    item.optString("negative"), item.optInt("width"), item.optInt("height"),
                    item.optInt("steps"), item.optDouble("cfg").toFloat(), item.optLong("seed"),
                    item.optLong("created"), item.optBoolean("favorite", false), item.optString("initImagePath").takeIf { it.isNotBlank() }, item.optDouble("strength", 0.65).toFloat(), item.optString("maskImagePath").takeIf { it.isNotBlank() }
                )
            }.getOrNull() ?: continue
            if (File(entry.filePath).exists()) result.add(entry)
        }
        return result
    }

    @Synchronized
    fun toggleFavorite(entry: GenerationHistoryEntry): GenerationHistoryEntry {
        val updated = entry.copy(favorite = !entry.favorite)
        write(list().map { if (it.id == entry.id) updated else it })
        return updated
    }

    fun exportToPictures(entry: GenerationHistoryEntry): String {
        val source = File(entry.filePath)
        require(source.exists()) { "Saved image file is missing" }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Diff_${entry.id}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Diff AI Studio")
        }
        val resolver = appContext.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: error("Unable to create export file")
        try {
            resolver.openOutputStream(uri)?.use { output -> source.inputStream().use { it.copyTo(output) } } ?: error("Unable to open export stream")
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            throw e
        }
        return uri.toString()
    }

    @Synchronized
    fun delete(entry: GenerationHistoryEntry) {
        File(entry.filePath).delete()
        write(list().filterNot { it.id == entry.id })
    }

    private fun write(entries: List<GenerationHistoryEntry>) {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(JSONObject().put("id", entry.id).put("file", entry.filePath)
                .put("prompt", entry.prompt).put("negative", entry.negativePrompt)
                .put("width", entry.width).put("height", entry.height).put("steps", entry.steps)
                .put("cfg", entry.cfg.toDouble()).put("seed", entry.seed).put("created", entry.createdAt).put("favorite", entry.favorite).put("initImagePath", entry.initImagePath ?: "").put("strength", entry.strength).put("maskImagePath", entry.maskImagePath ?: ""))
        }
        prefs.edit().putString(key, array.toString()).apply()
    }
}
