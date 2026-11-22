package com.winsmediatam.diff

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GenerationRequestRulesTest {
    @Test
    fun acceptsValidTextToImageRequest() {
        assertNull(GenerationRequestRules.validate("a cinematic landscape", 512, 512, 25, 7f, 1, -1))
    }

    @Test
    fun rejectsBlankPrompt() {
        assertEquals("Please enter a prompt first.", GenerationRequestRules.validate(" ", 512, 512, 25, 7f, 1, -1))
    }

    @Test
    fun rejectsInvalidDimensionsAndNonMultipleOf64() {
        assertEquals("Resolution must be 256–1024 and divisible by 64.", GenerationRequestRules.validate("prompt", 255, 512, 25, 7f, 1, -1))
        assertEquals("Resolution must be 256–1024 and divisible by 64.", GenerationRequestRules.validate("prompt", 512, 500, 25, 7f, 1, -1))
    }

    @Test
    fun rejectsOutOfRangeStepsAndCfg() {
        assertEquals("Steps must be between 1 and 60.", GenerationRequestRules.validate("prompt", 512, 512, 61, 7f, 1, -1))
        assertEquals("CFG scale must be between 1 and 20.", GenerationRequestRules.validate("prompt", 512, 512, 25, 0.5f, 1, -1))
    }

    @Test
    fun validatesBatchAndSeedOverflow() {
        assertEquals("Batch count must be between 1 and 4.", GenerationRequestRules.validate("prompt", 512, 512, 25, 7f, 5, -1))
        assertEquals("Seed is too large for the selected batch count.", GenerationRequestRules.validate("prompt", 512, 512, 25, 7f, 4, Long.MAX_VALUE))
        assertNull(GenerationRequestRules.validate("prompt", 512, 512, 25, 7f, 4, Long.MAX_VALUE - 3))
    }

    @Test
    fun rejectsInvalidNegativeSeed() {
        assertEquals("Seed must be -1 (random) or a non-negative integer.", GenerationRequestRules.validate("prompt", 512, 512, 25, 7f, 1, -2))
    }
}
