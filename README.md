# Diff AI Studio

Android-native, offline-first AI image generation studio (ARM64-first).

## Implemented at source level
- Jetpack Compose dark studio shell with Create/Gallery/Models/Settings navigation
- Local model import/list/delete and selection persistence
- Background model download worker with progress, cancellation and resume
- stable-diffusion.cpp pinned native ARM64 CPU baseline integration source
- JNI model load/release, txt2img parameters, progress/cancellation and Bitmap output source
- Prompt/negative prompt, steps, CFG, seed and resolution controls
- Device RAM/architecture report and heuristic pre-generation safety guard
- Prompt presets for photorealistic, cinematic, anime, fantasy, product, portrait, landscape and architecture
- Automatic private PNG history and metadata (prompt, negative prompt, dimensions, steps, CFG, seed, timestamp)
- Gallery/history display and delete
- Export to Pictures/Diff AI Studio
- Share generated images through Android Sharesheet

## Validation caveat
**Build/CI and device validation are deferred.** JNI/upstream API compatibility, model-format support, inference output, export and sharing have not been tested. This project is not production-ready.

## Docs
- [Roadmap](docs/ROADMAP.md)
- [Model downloads](docs/MODEL_DOWNLOADS.md)
- [Engine notes](docs/ENGINE_NOTES.md)
