package com.winsmediatam.diff

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptAndModelRulesTest {
    @Test
    fun supportedModelExtensionsAreCaseInsensitive() {
        listOf("model.gguf", "model.safetensors", "model.ckpt", "model.pt", "model.pth", "MODEL.GGUF").forEach {
            assertTrue("Expected supported model: $it", ModelFileRules.isSupported(it))
        }
    }

    @Test
    fun unsupportedModelExtensionsAreRejected() {
        listOf("model.zip", "model.bin", "model.txt", "no_extension").forEach {
            assertFalse("Expected unsupported model: $it", ModelFileRules.isSupported(it))
        }
    }

    @Test
    fun builtInPromptPresetsAreNonEmptyAndHaveUniqueNames() {
        assertTrue(PromptPresets.all.isNotEmpty())
        assertEquals(PromptPresets.all.size, PromptPresets.all.map { it.first }.distinct().size)
        PromptPresets.all.forEach { (name, prompt) ->
            assertTrue(name.isNotBlank())
            assertTrue(prompt.isNotBlank())
        }
    }

    @Test
    fun builtInPresetCategoriesArePresent() {
        val names = PromptPresets.all.map { it.first }.toSet()
        listOf("Photorealistic", "Cinematic", "Anime", "Fantasy", "Product", "Portrait", "Landscape", "Architecture").forEach {
            assertTrue("Missing preset category: $it", it in names)
        }
    }
}
