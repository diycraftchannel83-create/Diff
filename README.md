# Diff AI Studio

Android-native, offline-first AI image generation studio (ARM64-first).

## Implemented at source level
- Jetpack Compose dark studio shell with Create/Gallery/Models/Settings
- Local model import/list/delete and persisted selection
- Background model download worker with progress, cancellation and resume
- Pinned stable-diffusion.cpp ARM64 native integration source
- JNI model load/release, txt2img options, progress/cancel and bitmap output source
- Prompt/negative prompt, steps, CFG, seed and resolution controls
- Sampler/scheduler selectors with automatic model defaults
- Device RAM/architecture report and heuristic generation safety guard
- Prompt presets for photorealistic, cinematic, anime, fantasy, product, portrait, landscape and architecture
- Private PNG history with generation metadata, gallery, delete and reuse prompt
- Export to Pictures/Diff AI Studio and share through Android Sharesheet
- Runtime/native/privacy information Settings screen

## Validation caveat
Build/CI and device validation are deferred. Native API declarations/options have been checked against pinned upstream documentation, but compile, model loading, inference, export/share and runtime behavior are not verified. This is not production-ready.

## Docs
- [Roadmap](docs/ROADMAP.md)
- [Model downloads](docs/MODEL_DOWNLOADS.md)
- [Engine notes](docs/ENGINE_NOTES.md)
