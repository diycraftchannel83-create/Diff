# Diff AI Studio

Android-first local AI image generation studio. Current build is a Compose scaffold with a native JNI status bridge and local model-file manager.

## Current status
- Android Kotlin + Jetpack Compose UI
- Debug APK CI workflow
- ARM64 JNI/CMake bridge scaffold
- Prompt and generation parameter UI
- Model import from Android document picker to app-private storage
- Imported model listing and deletion

**Inference is not integrated yet.** Imported files are managed locally, but generation remains disabled until the native diffusion engine is integrated and tested.

See [Roadmap](docs/ROADMAP.md) and [Engine notes](docs/ENGINE_NOTES.md).
