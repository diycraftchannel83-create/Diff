package com.winsmediatam.diff
import android.graphics.Bitmap
object NativeRuntime {
    private val loaded = try { System.loadLibrary("diff_native"); true } catch (_: UnsatisfiedLinkError) { false }
    external fun getRuntimeStatus(): String
    external fun getNativeArchitecture(): String
    external fun loadModel(modelPath: String, threads: Int): Boolean
    external fun releaseModel()
    external fun cancelGeneration()
    external fun getGenerationProgress(): IntArray
    external fun generateBitmap(prompt: String, negativePrompt: String, width: Int, height: Int, steps: Int, cfg: Float, seed: Long, sampler: String, scheduler: String): Bitmap?
    fun status() = if (loaded) getRuntimeStatus() else "Native library failed to load. Check ABI/build configuration."
    fun architecture() = if (loaded) getNativeArchitecture() else "Unavailable"
    fun isAvailable() = loaded
}
