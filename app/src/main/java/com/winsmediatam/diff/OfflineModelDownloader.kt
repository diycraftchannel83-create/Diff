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
    private const val READ_TIMEOUT_MS = 60_000

    suspend fun downloadAndInstall(context: Context, onProgress: (Long, Long) -> Unit): LocalModel = withContext(Dispatchers.IO) {
        val tempDir = File(context.filesDir, "model_download").apply { mkdirs() }
        val zipFile = File(tempDir, "sd15-sdcpp.zip.part")
        val finalZip = File(tempDir, "sd15-sdcpp.zip")
        val initialOffset = zipFile.length()

        val connection = (URL(MODEL_ZIP_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            instanceFollowRedirects = true
            setRequestProperty("User-Agent", "Diff-AI-Studio-Android")
            if (initialOffset > 0L) setRequestProperty("Range", "bytes=$initialOffset-")
        }

        var totalBytes = MODEL_ZIP_SIZE
        var downloadedBytes = initialOffset
        try {
            connection.connect()
            val responseCode = connection.responseCode
            val supportsResume = initialOffset > 0L && responseCode == HttpURLConnection.HTTP_PARTIAL
            if (initialOffset > 0L && !supportsResume) {
                // Server did not honor Range; restart safely from byte zero.
                zipFile.delete()
                downloadedBytes = 0L
            }
            val responseLength = connection.contentLengthLong
            if (responseLength > 0L) totalBytes = if (supportsResume) initialOffset + responseLength else responseLength

            val append = supportsResume
            connection.inputStream.use { input ->
                zipFile.outputStream().buffered().use { output ->
                    val buffer = ByteArray(1024 * 1024)
                    var current = downloadedBytes
                    while (true) {
                        coroutineContext.ensureActive()
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        current += count
                        downloadedBytes = current
                        withContext(Dispatchers.Main) { onProgress(current, totalBytes) }
                    }
                }
            }
        } finally {
            connection.disconnect()
        }

        require(zipFile.exists() && zipFile.length() > 0L) { "Model download is empty" }
        if (totalBytes > 0L && zipFile.length() < totalBytes) {
            throw IllegalStateException("Download incomplete. Partial file kept for resume: ${ModelRepository.formatSize(zipFile.length())} of ${ModelRepository.formatSize(totalBytes)}")
        }
        require(zipFile.renameTo(finalZip) || run { zipFile.copyTo(finalZip, overwrite = true); zipFile.delete(); true }) { "Could not finalize downloaded archive" }

        val extracted = File(tempDir, "sd15-q8c16.gguf.tmp")
        val finalExtracted = File(tempDir, "sd15-q8c16.gguf")
        var found = false
        ZipInputStream(finalZip.inputStream().buffered()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                coroutineContext.ensureActive()
                if (!entry.isDirectory && entry.name.endsWith(".gguf", ignoreCase = true)) {
                    extracted.outputStream().buffered().use { out ->
                        val buffer = ByteArray(1024 * 1024)
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
        require(found && extracted.length() > 0L) { "ZIP archive did not contain a GGUF model" }
        require(extracted.renameTo(finalExtracted) || run { extracted.copyTo(finalExtracted, overwrite = true); extracted.delete(); true }) { "Could not finalize extracted model" }

        val installed = ModelRepository(context).importDownloadedModel(finalExtracted, "sd15-q8c16.gguf")
        finalZip.delete()
        finalExtracted.delete()
        installed
    }

    fun partialDownloadBytes(context: Context): Long =
        File(context.filesDir, "model_download/sd15-sdcpp.zip.part").length()

    fun clearPartialDownload(context: Context): Boolean =
        File(context.filesDir, "model_download/sd15-sdcpp.zip.part").delete()
}
