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
    private const val CONNECT_TIMEOUT_MS = 30_000
    private const val READ_TIMEOUT_MS = 90_000
    private const val BUFFER_SIZE = 256 * 1024

    suspend fun downloadAndInstall(context: Context, onProgress: (Long, Long) -> Unit): LocalModel = withContext(Dispatchers.IO) {
        val tempDir = File(context.filesDir, "model_download").apply { check(mkdirs() || isDirectory) }
        val zipFile = File(tempDir, "sd15-sdcpp.zip.part")
        var offset = zipFile.length().coerceAtLeast(0L)
        var totalBytes = MODEL_ZIP_SIZE
        val connection = (URL(MODEL_ZIP_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            instanceFollowRedirects = true
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Diff-AI-Studio-Android/1.0")
            setRequestProperty("Accept", "application/octet-stream")
            if (offset > 0) setRequestProperty("Range", "bytes=$offset-")
        }
        try {
            connection.connect()
            val code = connection.responseCode
            require(code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_PARTIAL) {
                "Model server returned HTTP $code. Please retry when the server is available."
            }
            val resumeAccepted = offset > 0 && code == HttpURLConnection.HTTP_PARTIAL
            if (offset > 0 && !resumeAccepted) {
                zipFile.delete()
                offset = 0L
            }
            val contentRange = connection.getHeaderField("Content-Range")
            val rangeTotal = Regex("/(\\d+)$").find(contentRange.orEmpty())?.groupValues?.getOrNull(1)?.toLongOrNull()
            val contentLength = connection.contentLengthLong
            totalBytes = rangeTotal ?: when {
                resumeAccepted && contentLength > 0 -> offset + contentLength
                contentLength > 0 -> contentLength
                else -> MODEL_ZIP_SIZE
            }
            require(totalBytes > offset) { "Invalid download size reported by server." }
            var current = offset
            var lastReported = offset
            connection.inputStream.use { input ->
                java.io.FileOutputStream(zipFile, resumeAccepted).buffered(BUFFER_SIZE).use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    while (true) {
                        coroutineContext.ensureActive()
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        current += count
                        if (current - lastReported >= 512 * 1024 || current >= totalBytes) {
                            lastReported = current
                            onProgress(current, totalBytes)
                        }
                    }
                    output.flush()
                }
            }
        } finally {
            connection.disconnect()
        }
        require(zipFile.exists() && zipFile.length() > 0L) { "Download returned an empty file." }
        if (zipFile.length() < totalBytes) {
            throw IllegalStateException("Download interrupted at ${ModelRepository.formatSize(zipFile.length())} of ${ModelRepository.formatSize(totalBytes)}. Partial file retained; tap Retry.")
        }
        if (zipFile.length() > totalBytes && totalBytes > 0) {
            throw IllegalStateException("Downloaded file is larger than expected. Please retry.")
        }

        val extracted = File(tempDir, "sd15-q8c16.gguf.tmp")
        val finalExtracted = File(tempDir, "sd15-q8c16.gguf")
        extracted.delete()
        var found = false
        ZipInputStream(zipFile.inputStream().buffered(BUFFER_SIZE)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                coroutineContext.ensureActive()
                if (!entry.isDirectory && entry.name.endsWith(".gguf", ignoreCase = true)) {
                    extracted.outputStream().buffered(BUFFER_SIZE).use { out ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        while (true) {
                            coroutineContext.ensureActive()
                            val count = zip.read(buffer)
                            if (count < 0) break
                            out.write(buffer, 0, count)
                        }
                    }
                    found = true
                    zip.closeEntry()
                    break
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
        require(found && extracted.length() > 0L) { "Downloaded archive is invalid or does not contain a GGUF model." }
        require(extracted.renameTo(finalExtracted) || run { extracted.copyTo(finalExtracted, overwrite = true); extracted.delete(); true }) {
            "Could not finalize extracted model file."
        }
        val installed = ModelRepository(context).importDownloadedModel(finalExtracted, "sd15-q8c16.gguf")
        zipFile.delete()
        finalExtracted.delete()
        installed
    }

    fun partialDownloadBytes(context: Context): Long = File(context.filesDir, "model_download/sd15-sdcpp.zip.part").length()
    fun clearPartialDownload(context: Context): Boolean = File(context.filesDir, "model_download/sd15-sdcpp.zip.part").delete()
}
