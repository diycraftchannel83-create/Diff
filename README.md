# Diff AI Studio

Android-native, offline-first AI image generation studio (ARM64-first).

## Implemented at source level
- Jetpack Compose dark studio shell with Create/Gallery/Models/Settings
- Local model import/list/delete and persisted selection
- Background model download worker with progress, cancellation and resume
- stable-diffusion.cpp pinned ARM64 native integration source
- JNI model load/release, txt2img parameters, progress/cancel and Bitmap output source
- Prompt/negative prompt, steps, CFG, seed and resolution controls
- Device RAM/architecture report and heuristic generation safety guard
- Prompt presets: photorealistic, cinematic, anime, fantasy, product, portrait, landscape, architecture
- Automatic private PNG history with generation metadata
- Gallery, delete and reuse-prompt actions
- Export to Pictures/Diff AI Studio and Android Sharesheet share action
- Runtime/native/privacy information Settings screen

## Validation caveat
**Build/CI and device validation are deferred.** Native API declarations and option keys have been checked against the pinned upstream source/docs, but compile, model loading, inference, export/share and runtime behavior are not tested. This is not production-ready.

## Docs
- [Roadmap](docs/ROADMAP.md)
- [Model downloads](docs/MODEL_DOWNLOADS.md)
- [Engine notes](docs/ENGINE_NOTES.md)
