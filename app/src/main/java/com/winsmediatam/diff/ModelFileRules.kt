package com.winsmediatam.diff

object ModelFileRules {
    private val supportedExtensions = setOf("gguf", "safetensors", "ckpt", "pt", "pth")
    fun isSupported(fileName: String): Boolean = fileName.substringAfterLast(".", "").lowercase() in supportedExtensions
}
