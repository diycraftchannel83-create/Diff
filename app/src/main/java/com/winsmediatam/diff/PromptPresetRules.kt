package com.winsmediatam.diff

/** Pure validation/normalization for user prompt presets. */
object PromptPresetRules {
    fun normalizeName(name: String): String? = name.trim().takeIf { it.isNotBlank() && it.length <= 40 }
    fun normalizePrompt(prompt: String): String? = prompt.trim().takeIf { it.isNotBlank() && it.length <= 8000 }
    fun validate(name: String, prompt: String): String? {
        if (normalizeName(name) == null) return "Preset name must contain 1–40 characters."
        if (normalizePrompt(prompt) == null) return "Preset prompt must contain 1–8000 characters."
        return null
    }
}
