package com.winsmediatam.diff
object NativeRuntime {
    private val loaded = try { System.loadLibrary("diff_native"); true } catch (_: UnsatisfiedLinkError) { false }
    external fun getRuntimeStatus(): String
    external fun getNativeArchitecture(): String
    fun status() = if (loaded) getRuntimeStatus() else "Native library failed to load. Check ABI/build configuration."
    fun architecture() = if (loaded) getNativeArchitecture() else "Unavailable"
}
