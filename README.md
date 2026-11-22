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
- Image-to-image picker, persistent private source-image copy normalized to reduced PNG, preview and strength slider
- Interactive mask painting, clear/apply workflow and mask_image option wiring source
- Sequential batch generation (1–4), aggregate progress, cancel and per-image history source
- Private generation history and metadata; capped at 100 entries with old generated PNG pruning
- Gallery favorites, prompt search, filtering, sorting and metadata details
- Local 2× filtered image resize (non-AI), reuse prompt, delete, export and Android share
- Runtime/privacy information screen
- Unit test source for model file rules and built-in preset integrity (not run yet)

## Validation caveat
Build/CI and device validation are deferred. Native API wiring, image-to-image, batch generation, inpainting, resize, history, export/share and UI have not been compiled or runtime tested. This is not production-ready.

## Docs
- [Roadmap](docs/ROADMAP.md)
- [Model downloads](docs/MODEL_DOWNLOADS.md)
- [Engine notes](docs/ENGINE_NOTES.md)
- [Privacy policy draft](PRIVACY.md)
- [Third-party notices checklist](THIRD_PARTY_NOTICES.md)
- [Release qualification checklist](RELEASE_CHECKLIST.md)
