package com.winsmediatam.diff

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelFileRulesTest {
    @Test fun acceptsSupportedModelExtensionsCaseInsensitively() {
        listOf("model.gguf", "weights.SAFETENSORS", "checkpoint.ckpt", "model.pt", "weights.pth").forEach {
            assertTrue("$it should be supported", ModelFileRules.isSupported(it))
        }
    }

    @Test fun rejectsUnsupportedAndExtensionlessFiles() {
        listOf("image.png", "archive.zip", "model", "model.txt").forEach {
            assertFalse("$it should not be supported", ModelFileRules.isSupported(it))
        }
    }
}
