# Diff AI Studio

Android-native, offline-first AI image generation studio.

## Implemented
- Kotlin + Jetpack Compose foundation and dark studio UI
- ARM64 JNI/CMake status bridge\n- Pinned stable-diffusion.cpp CPU backend configured and linked for Android ARM64
- Prompt, negative prompt, dimensions, steps, CFG and seed controls
- Native JNI model loading and txt2img source integration: selected local model, generation options, RGB bitmap preview, and surfaced native errors (build/runtime validation pending)
- Local model manager: import from Android file picker, list, size display and delete\n- Create screen model selector with persisted selection\n- Basic device RAM/architecture report and suggested starter resolution
- Supported file extension validation and safer temporary import/finalize flow
- Background model download worker with foreground notification, progress and cancel; Model Manager UI is wired to WorkManager status\n- GitHub Actions Debug APK build

## Important
The stable-diffusion.cpp C API is now connected in JNI source for model loading and basic txt2img generation. The Create screen calls native inference on an IO coroutine and displays the returned bitmap. **This source integration has not been build- or device-validated yet**; supported model format and memory requirements remain model/device dependent.

Offline model downloads: [docs/MODEL_DOWNLOADS.md](docs/MODEL_DOWNLOADS.md). Roadmap: [docs/ROADMAP.md](docs/ROADMAP.md). Engine notes: [docs/ENGINE_NOTES.md](docs/ENGINE_NOTES.md).
