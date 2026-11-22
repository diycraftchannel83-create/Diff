# Diff AI Studio Roadmap

## Phase 1 — Android foundation
- [x] Compose Android project, dark studio navigation and prompt controls
- [x] Local model import/list/delete and persisted selection
- [x] Background model downloader with progress, cancellation and resume
- [x] CI workflow configured (validation intentionally deferred)
- [ ] Baseline build and install on ARM64 device

## Phase 2 — Local inference
- [x] ARM64 native stable-diffusion.cpp JNI integration source
- [x] Model load/release and text-to-image option wiring
- [x] Native progress callback, cancellation and bitmap conversion source
- [x] Sampler/scheduler UI and JNI option wiring
- [x] RAM/architecture report and heuristic safety guard
- [ ] Compile/link against pinned upstream revision
- [ ] Confirm model formats/quantization and on-device generation
- [ ] Benchmark model load, generation, peak memory and thermals

## Phase 3 — Image workflows
- [x] Built-in prompt presets
- [x] User custom prompt presets: save, select and manage/delete
- [x] Image-to-image picker, persistent private input copy, downscaled preview/input and strength slider
- [x] JNI option wiring for init_image and strength (source only)
- [x] Sequential batch generation up to 4 images with aggregate progress, cancel and per-image history (source)
- [x] Interactive inpainting mask editor and mask_image JNI option wiring (source)
- [ ] Validate image-to-image, batch and inpainting native API behavior on-device
- [x] Restore generation settings from history: prompt, negative prompt, dimensions, steps, CFG, seed, sampler, scheduler, source image, strength and mask (source)
- [ ] More verified sampler/scheduler compatibility options

## Phase 4 — Library
- [x] Private PNG generation history with metadata
- [x] Prompt/negative prompt, dimensions, steps, CFG, seed and timestamp
- [x] Image-to-image source path, strength and inpainting mask metadata
- [x] Favorites toggle and persisted favorite state
- [x] Prompt search and favorites filter
- [x] Sort newest/oldest/favorites-first
- [x] Metadata detail view
- [x] Delete, reuse prompt, export to Pictures and Android share
- [x] History cap with pruning of evicted generated PNGs
- [x] 2x filtered resize workflow (non-AI; explicitly labeled)
- [ ] AI upscale model/pipeline

## Phase 5 — Release quality
- [x] Pure unit test source for model extension rules, built-in prompt presets and generation request validation
- [x] Shared request validation for prompt, dimensions, steps, CFG, batch size and seed overflow
- [x] UI rejects malformed seed text instead of silently treating it as random
- [x] Heuristic pre-generation RAM/resolution guard
- [x] Runtime/privacy Settings screen
- [x] Pure custom prompt preset validation tests (name/prompt normalization and limits)
- [x] History metadata recovery fallback (primary/backup JSON snapshot)
- [ ] Android persistence tests for history and custom presets
- [ ] UI tests, accessibility and localization review
- [x] Gallery 2× resize bitmap cleanup on success/failure and partial-file cleanup (source)
- [ ] Native memory/performance optimization
- [ ] Real-device compatibility matrix
- [ ] Build/install/smoke tests, APK/AAB pipeline
- [x] Privacy policy draft and third-party license/model notice checklist added (final legal review pending)
- [x] Release qualification checklist file added
- [ ] Complete checklist and Play Store release qualification

## Validation status
Source changes are committed incrementally. Build/CI/device validation remains intentionally deferred. Image-to-image, batch, inpainting, gallery/history, export/share and preset UI are not compile/runtime verified. Production readiness requires completing validation and remaining workflow items.
