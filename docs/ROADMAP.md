# Diff AI Studio Roadmap

## Phase 1 — Android foundation
- [x] Compose Android project, dark studio navigation and prompt controls
- [x] Model manager import/list/delete and selection persistence
- [x] Background downloader with progress, cancel and resumable partial-file handling
- [x] CI workflow configured (build validation is currently deferred per developer workflow request)
- [ ] Confirm baseline build and install on a physical ARM64 device

## Phase 2 — Local inference
- [x] ARM64 CMake/JNI stable-diffusion.cpp integration source
- [x] Native model load/release, txt2img option wiring and Bitmap conversion source
- [x] Progress callback and cancellation wiring source
- [x] Device capability report and conservative RAM/resolution guard
- [ ] Confirm native API signatures against pinned upstream revision and compile/link
- [ ] Validate model formats and quantization compatibility
- [ ] Benchmark model load, generation, peak RAM and thermal behavior on devices

## Phase 3 — Image workflows
- [x] Prompt preset library (photorealistic, cinematic, anime, fantasy, product, portrait, landscape, architecture)
- [ ] Image-to-image: image picker, native input tensor conversion and verified upstream API wiring
- [ ] Inpainting mask editor and native mask pipeline
- [ ] Batch generation queue and per-item progress/cancel
- [ ] Sampler/scheduler controls after native API compatibility verification
- [ ] Prompt preset editor and user-saved presets

## Phase 4 — Library
- [x] Private image history: automatically save successful generations
- [x] Generation metadata: prompt, negative prompt, dimensions, steps, CFG, seed and timestamp
- [x] History/gallery view and deletion
- [ ] Favorites and filters
- [ ] Metadata details/edit/copy prompt
- [ ] Export to public Pictures/Downloads using MediaStore
- [ ] Share image via Android Sharesheet
- [ ] Image upscale workflow

## Phase 5 — Release quality
- [x] Model file extension unit tests
- [x] Pre-generation low-memory and resolution safety guard (source)
- [ ] UI and repository unit tests for generation history, presets and gallery
- [ ] Memory/performance optimization and native error handling review
- [ ] Device compatibility matrix and real-device test plan/results
- [ ] Build, install, smoke test, APK/AAB release pipeline
- [ ] Privacy policy, model/source licenses and third-party notices
- [ ] Release checklist, versioning and Play Store readiness

## Current validation status
Source work is being committed in small increments. **Build/CI/device validation is intentionally not being run yet.** Items marked complete are implemented at source level, not necessarily build- or runtime-verified. Native inference, image workflows, public export/share and release qualification remain pending.
