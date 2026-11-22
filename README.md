# Diff AI Studio

Android-native, offline-first AI image generation studio.

## Implemented
- Kotlin + Jetpack Compose foundation and dark studio UI
- ARM64 JNI/CMake status bridge\n- Pinned stable-diffusion.cpp CPU backend configured and linked for Android ARM64
- Prompt, negative prompt, dimensions, steps, CFG and seed controls
- Local model manager: import from Android file picker, list, size display and delete\n- Create screen model selector with persisted selection\n- Basic device RAM/architecture report and suggested starter resolution
- Supported file extension validation and safer temporary import/finalize flow
- Background model download worker infrastructure with foreground notification and progress/cancel API (UI wiring pending)\n- GitHub Actions Debug APK build

## Important
The stable-diffusion.cpp native library is now configured to compile/link into the Android ARM64 JNI library. The JNI inference API and model loading are not wired yet. Imported files still cannot generate images, and Generate remains disabled. Model selection and basic device readiness information are now implemented.

Offline model downloads: [docs/MODEL_DOWNLOADS.md](docs/MODEL_DOWNLOADS.md). Roadmap: [docs/ROADMAP.md](docs/ROADMAP.md). Engine notes: [docs/ENGINE_NOTES.md](docs/ENGINE_NOTES.md).
