# Diff AI Studio Release Checklist

## Source / build validation
- [ ] Run debug build on clean CI and fix all compiler/linker errors
- [ ] Run unit tests and record report
- [ ] Build release APK/AAB with signing configuration
- [ ] Verify arm64-v8a native library packaging
- [ ] Install and smoke test on a physical ARM64 phone
- [ ] Confirm model import/download, model selection, load/release
- [ ] Verify text-to-image, image-to-image, mask/inpainting, batch and cancel
- [ ] Verify progress UI, native errors, app background/foreground
- [ ] Verify gallery history, favorite, search, sorting, delete
- [ ] Verify export and share on supported Android versions
- [ ] Verify 2x resize memory guard and output quality
- [ ] Test low RAM, thermal throttling and large source images

## Compatibility / UX
- [ ] Test Android 8 through current Android versions supported by minSdk
- [ ] Confirm permissions and export behavior on pre-Android 10
- [ ] Accessibility labels, font scaling, contrast and touch targets
- [ ] Localization and RTL review
- [ ] Verify app restart, history persistence and model selection

## Privacy / legal / distribution
- [ ] Review PRIVACY.md against final network behavior and permissions
- [ ] Fill publisher contact
- [ ] Verify pinned stable-diffusion.cpp license and notices
- [ ] Audit third-party Gradle dependency licenses
- [ ] Add model download sources, model licenses and restrictions
- [ ] Finalize Play Store listing, screenshots, icon, rating and declarations

## Release decision
- [ ] No known crash/blocking defect
- [ ] Build/test artifacts reviewed and archived
- [ ] Version code/name updated
- [ ] Rollout and rollback plan prepared
- [ ] Production readiness approved
