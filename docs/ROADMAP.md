# Diff AI Studio Roadmap

## Phase 1 — Android foundation
- [x] Compose Android project, dark studio navigation and prompt controls
- [x] Model manager import/list/delete and selection persistence
- [x] Background downloader with progress, cancel and resumable partial-file handling
- [x] CI workflow configured (build validation deferred)
- [ ] Confirm baseline build and install on physical ARM64 device

## Phase 2 — Local inference
- [x] ARM64 CMake/JNI stable-diffusion.cpp integration source
- [x] Native model load/release, txt2img options and Bitmap conversion source
- [x] Progress callback and cancellation wiring source
- [x] Correct context-local progress callback signature against pinned upstream header
- [x] Device capability report and heuristic RAM/resolution guard
- [x] Verify upstream API declarations and key names from pinned docs
- [ ] Compile/link native engine and verify JNI calls
- [ ] Validate model formats/quantization and generate on-device
- [ ] Benchmark load/generation, memory and thermal behavior

## Phase 3 — Image workflows
- [x] Prompt presets (photo-realistic, cinematic, anime, fantasy, product, portrait, landscape, architecture)
- [x] Sampler/scheduler UI options and JNI wiring; automatic model defaults preserved (source only)
- [x] Confirm upstream image keys `init_image`, `strength`, `batch_count`, `sample.method`, `sample.scheduler`
- [ ] Image-to-image picker, RGB tensor conversion and native JNI pipeline
- [ ] Inpainting mask editor and `mask_image` native pipeline
- [ ] Batch generation queue with per-item progress/cancel
- [ ] Prompt preset editor/custom presets

## Phase 4 — Library
- [x] Automatically save generated PNGs in private app storage
- [x] Persist prompt, negative prompt, dimensions, steps, CFG, seed and timestamp
- [x] Gallery/history view and delete
- [x] Reuse prompt/negative prompt from gallery
- [x] Export to Pictures/Diff AI Studio via MediaStore
- [x] Share images using Android Sharesheet/FileProvider
- [ ] Favorites, search and filtering
- [ ] Metadata detail view and sort controls
- [ ] Upscale workflow

## Phase 5 — Release quality
- [x] Model extension unit tests
- [x] Pre-generation memory/resolution guard source
- [x] Runtime/privacy information Settings screen
- [ ] History/preset repository unit tests
- [ ] UI tests/accessibility review
- [ ] Native memory/performance optimization
- [ ] Device compatibility matrix and real-device tests
- [ ] Build/install/smoke tests and APK/AAB release pipeline
- [ ] Privacy policy, model/source licenses and third-party notices
- [ ] Release checklist and Play Store readiness

## Current validation status
Build, CI and device validation are intentionally deferred. Source-level items are not runtime verified. Image-to-image, inpainting, batch, custom presets, favorites/search, upscale, tests and release qualification remain.
