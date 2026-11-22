# Diff AI Studio

Android-native, offline-first AI image generation studio (ARM64-first).

## Source implementation
- Jetpack Compose dark studio shell, Create/Gallery/Models/Settings navigation
- Local model import/list/delete and persisted model selection
- Background model download worker with progress, cancel and resume support
- stable-diffusion.cpp pinned native ARM64 CPU baseline integration source
- JNI model load/release, txt2img parameters, progress/cancellation and bitmap output source
- Prompt/negative prompt, steps, CFG, seed and resolution controls
- Device RAM/architecture reporting and conservative pre-generation memory guard
- Prompt presets: photo-realistic, cinematic, anime, fantasy, product, portrait, landscape and architecture
- Automatically saved private generation history with PNG files and metadata
- Gallery/history display and deletion

## Validation caveat
The repository is being advanced source-first. **Build/CI and device validation are currently deferred.** JNI/upstream API compatibility, model-format support and actual inference are not yet verified. Do not consider the app production-ready.

## Documentation
- [Roadmap](docs/ROADMAP.md)
- [Model downloads](docs/MODEL_DOWNLOADS.md)
- [Engine notes](docs/ENGINE_NOTES.md)
