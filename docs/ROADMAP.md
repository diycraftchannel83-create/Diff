# Diff AI Studio Roadmap

## Phase 1 — Android foundation
- [x] Empty repository bootstrap
- [x] Kotlin + Jetpack Compose project
- [x] Dark UI scaffold and bottom navigation
- [x] Prompt and negative prompt controls
- [x] Basic generation parameters (steps, CFG, dimensions, seed)
- [x] Android CI workflow
- [x] Verify first CI build

## Phase 2 — Local inference
- [x] Android arm64 native CMake integration
- [x] JNI bridge skeleton and architecture reporting
- [x] Native runtime status displayed in app
- [x] Pin stable-diffusion.cpp revision (upstream commit 532d2c1)
- [x] Build stable-diffusion.cpp into Android ARM64 JNI shared library (CPU baseline)\n- [ ] Wire stable-diffusion.cpp opaque API to JNI model loading/inference
- [x] Basic device RAM/architecture capability report and resolution recommendation\n- [ ] Device benchmark and memory capability checks
- [x] Local model file import manager (SAF, app-private storage, list and delete)
- [x] Model extension validation and temporary-file import safety
- [x] Model Manager UI connected to import/list/delete actions\n- [x] Create screen model selector connected to imported models and persisted selection
- [x] In-app starter model downloader (SD 1.5 Q8 GGUF catalog entry, progress and cancel, ZIP extraction/install)\n- [ ] Model download resume and background transfer
- [ ] Generation progress, cancellation and errors
- [ ] Real txt2img pipeline

## Phase 3 — Image workflows
- [ ] Image-to-image
- [ ] Inpainting mask editor
- [ ] Batch generation
- [ ] Sampler and scheduler controls
- [ ] Prompt presets

## Phase 4 — Library
- [ ] Gallery database and thumbnails
- [ ] Favorites and history
- [ ] Metadata view
- [ ] Export/share
- [ ] Image upscale workflow

## Phase 5 — Release quality
- [x] Unit tests (model file extension rules)
- [ ] UI tests
- [ ] Memory/performance optimization
- [ ] Device compatibility matrix
- [ ] Release APK/AAB
- [ ] Privacy and licensing documentation
