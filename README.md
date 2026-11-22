# Diff AI Studio

Android-native, offline-first AI image generation studio (ARM64-first).

## Implemented at source level
- Jetpack Compose dark studio shell with Create/Gallery/Models/Settings
- Local model import/list/delete and persisted selection
- Background model downloader with progress, cancel and resume
- Pinned stable-diffusion.cpp ARM64 native integration source
- JNI model load/release, txt2img options, progress/cancel and Bitmap output source
- Prompt/negative prompt, dimensions, steps, CFG, seed
- Sampler/scheduler selection with model-default auto mode
- Device RAM/architecture report and heuristic generation guard
- Built-in and user-managed custom prompt presets
- Image-to-image picker, private source-image copy, preview, strength slider and native option wiring source
- Private generation history and metadata
- Gallery favorites, prompt search, filtering, sorting and metadata details
- Reuse prompt, delete, export to Pictures and share through Android Sharesheet
- Runtime/privacy information screen

## Validation caveat
Build/CI and device validation are deferred. Native API wiring, image-to-image option behavior, generation, history, export/share and UI have not been compiled or runtime tested. This is not production-ready.

## Docs
- [Roadmap](docs/ROADMAP.md)
- [Model downloads](docs/MODEL_DOWNLOADS.md)
- [Engine notes](docs/ENGINE_NOTES.md)
