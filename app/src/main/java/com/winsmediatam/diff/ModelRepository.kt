package com.winsmediatam.diff

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

data class LocalModel(val id: String, val name: String, val fileName: String, val sizeBytes: Long)

class ModelRepository(context: Context) {
    private val appContext = context.applicationContext
    private val folder = File(appContext.filesDir, "models").apply { mkdirs() }
    private val prefs = appContext.getSharedPreferences("diff_models", Context.MODE_PRIVATE)

    fun list(): List<LocalModel> = folder.listFiles()?.filter { it.isFile && it.length() > 0L }?.map { file ->
        LocalModel(file.absolutePath, file.nameWithoutExtension, file.name, file.length())
    }?.sortedBy { it.name.lowercase() } ?: emptyList()

    fun importModel(uri: Uri): LocalModel {
        val name = queryDisplayName(uri).ifBlank { "model_${UUID.randomUUID()}.gguf" }
        val safeName = name.replace(Regex("[^A-Za-z0-9._-]"), "_")
        require(safeName.substringAfterLast(".", "").lowercase() in setOf("gguf", "safetensors", "ckpt", "pt", "pth")) { "Unsupported model file type" }
        val requestedTarget = File(folder, safeName)
        val target = if (requestedTarget.exists()) File(folder, "${requestedTarget.nameWithoutExtension}_${UUID.randomUUID().toString().take(8)}.${requestedTarget.extension}") else requestedTarget
        val temp = File(folder, "${target.name}.importing_${UUID.randomUUID()}")
        appContext.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Unable to open selected file" }
            temp.outputStream().use { output -> input.copyTo(output) }
        }
        require(temp.length() > 0) { "Imported file is empty" }
        require(temp.renameTo(target)) { "Could not finalize imported model file" }
        prefs.edit().putString(target.absolutePath, target.name).apply()
        return LocalModel(target.absolutePath, target.nameWithoutExtension, target.name, target.length())
    }

    fun delete(model: LocalModel) {
        File(model.id).delete()
        prefs.edit().remove(model.id).apply()
    }

    private fun queryDisplayName(uri: Uri): String {
        appContext.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) return cursor.getString(0) ?: ""
        }
        return uri.lastPathSegment ?: ""
    }

    companion object {
        fun formatSize(bytes: Long): String = when {
            bytes >= 1024L * 1024L * 1024L -> "${"%.2f".format(bytes / (1024.0 * 1024.0 * 1024.0))} GB"
            bytes >= 1024L * 1024L -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
            bytes >= 1024L -> "${"%.1f".format(bytes / 1024.0)} KB"
            else -> "$bytes B"
        }
    }
}
