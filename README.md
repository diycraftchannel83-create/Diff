# Diff AI Studio

Android-native, offline-first AI image generation studio.

## Implemented
- Kotlin + Jetpack Compose foundation and dark studio UI
- ARM64 JNI/CMake status bridge
- Prompt, negative prompt, dimensions, steps, CFG and seed controls
- Local model manager: import from Android file picker, list, size display and delete
- Supported file extension validation and safer temporary import/finalize flow
- GitHub Actions Debug APK build

## Important
The native diffusion engine is not integrated yet. Model import and management do not imply that files can currently be loaded or used for generation. Generation remains disabled until inference is implemented and verified.

Roadmap: [docs/ROADMAP.md](docs/ROADMAP.md). Engine notes: [docs/ENGINE_NOTES.md](docs/ENGINE_NOTES.md).
