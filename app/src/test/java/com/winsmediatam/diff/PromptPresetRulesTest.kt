package com.winsmediatam.diff

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PromptPresetRulesTest {
    @Test
    fun trimsPresetNameAndPrompt() {
        assertEquals("Portrait", PromptPresetRules.normalizeName("  Portrait  "))
        assertEquals("cinematic portrait", PromptPresetRules.normalizePrompt(" cinematic portrait "))
    }

    @Test
    fun rejectsBlankOrTooLongNames() {
        assertNull(PromptPresetRules.normalizeName("   "))
        assertNull(PromptPresetRules.normalizeName("x".repeat(41)))
    }

    @Test
    fun rejectsBlankOrTooLongPrompt() {
        assertNull(PromptPresetRules.normalizePrompt("  "))
        assertNull(PromptPresetRules.normalizePrompt("x".repeat(8001)))
    }

    @Test
    fun validatesInputsWithReadableErrors() {
        assertEquals("Preset name must contain 1–40 characters.", PromptPresetRules.validate("", "some prompt"))
        assertEquals("Preset prompt must contain 1–8000 characters.", PromptPresetRules.validate("Name", ""))
        assertNull(PromptPresetRules.validate("Name", "Prompt"))
    }
}
