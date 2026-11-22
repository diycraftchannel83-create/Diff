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
- [x] Image-to-image picker, persistent private input copy, preview and strength slider
- [x] JNI option wiring for init_image and strength (source only)
- [x] Sequential batch generation up to 4 images with aggregate progress, cancel and per-image history (source)
- [ ] Validate image-to-image and batch native API behavior on-device
- [ ] Inpainting mask editor and mask pipeline
- [ ] Reuse full image-to-image configuration from history
- [ ] More verified sampler/scheduler compatibility options

## Phase 4 — Library
- [x] Private PNG generation history with metadata
- [x] Prompt/negative prompt, dimensions, steps, CFG, seed and timestamp
- [x] Favorites toggle and persisted favorite state
- [x] Prompt search and favorites filter
- [x] Sort newest/oldest/favorites-first
- [x] Metadata detail view, including image-to-image source/strength and batch seed
- [x] Delete, reuse prompt, export to Pictures and Android share
- [ ] Upscale workflow

## Phase 5 — Release quality
- [x] Model extension tests source
- [x] Heuristic pre-generation RAM/resolution guard
- [x] Runtime/privacy Settings screen
- [ ] Unit tests for history and preset persistence
- [ ] UI tests, accessibility and localization review
- [ ] Native memory/performance optimization
- [ ] Real-device compatibility matrix
- [ ] Build/install/smoke tests, APK/AAB pipeline
- [ ] Privacy policy, model licenses and third-party notices
- [ ] Play Store release checklist

## Validation status
Source changes are committed incrementally. Build/CI/device validation remains intentionally deferred. Image-to-image, batch, gallery/history, export/share and preset UI are not compile/runtime verified. Production readiness requires completing the validation and remaining workflow items.
