package com.winsmediatam.diff

/**
 * Pure input validation shared by UI and tests.
 * The native engine has stricter model-specific constraints which still require runtime verification.
 */
object GenerationRequestRules {
    fun validate(
        prompt: String,
        width: Int,
        height: Int,
        steps: Int,
        cfg: Float,
        batchCount: Int,
        seed: Long
    ): String? {
        if (prompt.isBlank()) return "Please enter a prompt first."
        if (width !in 256..1024 || height !in 256..1024 || width % 64 != 0 || height % 64 != 0) {
            return "Resolution must be 256–1024 and divisible by 64."
        }
        if (steps !in 1..60) return "Steps must be between 1 and 60."
        if (!cfg.isFinite() || cfg !in 1f..20f) return "CFG scale must be between 1 and 20."
        if (batchCount !in 1..4) return "Batch count must be between 1 and 4."
        if (seed < -1L) return "Seed must be -1 (random) or a non-negative integer."
        if (seed >= 0L && seed > Long.MAX_VALUE - (batchCount - 1).toLong()) {
            return "Seed is too large for the selected batch count."
        }
        return null
    }
}
