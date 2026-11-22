# Diff AI Studio Roadmap

## Phase 1 — Android foundation
- [x] Empty repository bootstrap
- [x] Kotlin + Jetpack Compose project
- [x] Dark UI scaffold and bottom navigation
- [x] Prompt and negative prompt controls
- [x] Basic generation parameters (steps, CFG, dimensions, seed)
- [x] Android CI workflow
- [ ] Verify first CI build

## Phase 2 — Local inference
- [x] Android arm64 native CMake integration
- [x] JNI bridge skeleton and architecture reporting
- [x] Native runtime status displayed in app
- [ ] Pin and evaluate stable-diffusion.cpp revision
- [ ] Build and integrate diffusion engine
- [ ] Device benchmark and memory capability checks
- [ ] Model import/download manager
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
- [ ] Unit tests
- [ ] UI tests
- [ ] Memory/performance optimization
- [ ] Device compatibility matrix
- [ ] Release APK/AAB
- [ ] Privacy and licensing documentation
