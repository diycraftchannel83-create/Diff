package com.winsmediatam.diff

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream
import kotlin.coroutines.coroutineContext

object OfflineModelDownloader {
    const val MODEL_ZIP_URL = "https://huggingface.co/cuesetac/sd15-mediapipe/resolve/main/sd15-sdcpp.zip"
    const val MODEL_ZIP_SIZE = 1_882_717_824L

    suspend fun downloadAndInstall(context: Context, onProgress: (Long, Long) -> Unit): LocalModel = withContext(Dispatchers.IO) {
        val tempDir = File(context.cacheDir, "model_download").apply { mkdirs() }
        val zipFile = File(tempDir, "sd15-sdcpp.zip.part")
        val connection = (URL(MODEL_ZIP_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = 30_000
            readTimeout = 60_000
            instanceFollowRedirects = true
            setRequestProperty("User-Agent", "Diff-AI-Studio-Android")
        }
        try {
            connection.connect()
            val total = connection.contentLengthLong.takeIf { it > 0 } ?: MODEL_ZIP_SIZE
            connection.inputStream.use { input ->
                zipFile.outputStream().buffered().use { output ->
                    val buffer = ByteArray(1024 * 1024)
                    var downloaded = 0L
                    while (true) {
                        coroutineContext.ensureActive()
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        downloaded += count
                        withContext(Dispatchers.Main) { onProgress(downloaded, total) }
                    }
                }
            }
        } finally {
            connection.disconnect()
        }
        val extracted = File(tempDir, "sd15-q8c16.gguf")
        var found = false
        ZipInputStream(zipFile.inputStream().buffered()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                coroutineContext.ensureActive()
                if (!entry.isDirectory && entry.name.endsWith(".gguf", ignoreCase = true)) {
                    extracted.outputStream().buffered().use { out -> zip.copyTo(out) }
                    found = true
                    break
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
        require(found && extracted.length() > 0L) { "ZIP archive did not contain a GGUF model" }
        val installed = ModelRepository(context).importDownloadedModel(extracted, "sd15-q8c16.gguf")
        zipFile.delete()
        extracted.delete()
        installed
    }
}
