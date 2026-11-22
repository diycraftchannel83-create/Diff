package com.winsmediatam.diff

import android.app.ActivityManager
import android.content.Context

data class DeviceCapabilityReport(
    val totalRamMb: Long,
    val availableRamMb: Long,
    val architecture: String,
    val recommendedPreset: String,
    val suggestedResolution: String
)

object DeviceCapabilities {
    fun current(context: Context): DeviceCapabilityReport {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo().also(manager::getMemoryInfo)
        val total = info.totalMem / (1024L * 1024L)
        val available = info.availMem / (1024L * 1024L)
        val arch = System.getProperty("os.arch") ?: "unknown"
        val preset = when {
            total >= 12000 -> "High memory"
            total >= 8000 -> "Balanced"
            total >= 6000 -> "Low memory"
            else -> "Very low memory"
        }
        val resolution = when {
            total >= 12000 -> "512 × 512 (test 640 × 640 later)"
            total >= 8000 -> "512 × 512"
            total >= 6000 -> "384 × 384"
            else -> "256 × 256"
        }
        return DeviceCapabilityReport(total, available, arch, preset, resolution)
    }
}
