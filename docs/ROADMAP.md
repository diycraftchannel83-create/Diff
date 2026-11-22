# Diff AI Studio Roadmap

## Phase 1 — Android foundation
- [x] Compose Android project, dark studio navigation and prompt controls
- [x] Model manager import/list/delete and selection persistence
- [x] Background downloader with progress, cancel and resumable partial-file handling
- [x] CI workflow configured (build validation currently deferred)
- [ ] Confirm baseline build and install on a physical ARM64 device

## Phase 2 — Local inference
- [x] ARM64 CMake/JNI stable-diffusion.cpp integration source
- [x] Native model load/release, txt2img option wiring and Bitmap conversion source
- [x] Progress callback and cancellation wiring source
- [x] Device capability report and conservative RAM/resolution guard
- [ ] Confirm native API signatures against pinned upstream revision and compile/link
- [ ] Validate model formats and quantization compatibility
- [ ] Benchmark model load, generation, peak RAM and thermal behavior

## Phase 3 — Image workflows
- [x] Prompt preset library (photorealistic, cinematic, anime, fantasy, product, portrait, landscape, architecture)
- [ ] Image-to-image input picker, image conversion and verified native API
- [ ] Inpainting mask editor and native mask pipeline
- [ ] Batch generation queue with per-item progress/cancel
- [ ] Sampler/scheduler controls after API verification
- [ ] Prompt preset editor and custom user presets

## Phase 4 — Library
- [x] Automatically save generated PNGs in private app storage
- [x] Persist generation metadata: prompt, negative prompt, dimensions, steps, CFG, seed, timestamp
- [x] Gallery/history view and delete
- [x] Export images to public Pictures/Diff AI Studio via MediaStore
- [x] Share images using Android Sharesheet/FileProvider
- [ ] Favorites and filtering/search
- [ ] Metadata details, copy/reuse prompt, sort controls
- [ ] Upscale workflow

## Phase 5 — Release quality
- [x] Model extension unit tests
- [x] Pre-generation low-memory/resolution guard source
- [ ] History/preset repository unit tests
- [ ] UI tests and accessibility pass
- [ ] Native memory/performance optimization
- [ ] Device compatibility matrix and real-device tests
- [ ] Build/install/smoke tests and APK/AAB release pipeline
- [ ] Privacy policy, model/source licenses and third-party notices
- [ ] Release checklist and Play Store readiness

## Current validation status
Source changes are committed incrementally. **Build, CI, and device validation are intentionally not run yet.** Source-level items are not considered verified. The remaining native inference, image workflows, tests, device validation, and release gates must be completed before production readiness.
