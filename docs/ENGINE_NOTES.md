# Engine Evaluation Notes

Diff AI Studio targets offline on-device inference, ARM64 first.

## Source integration status
- Pinned stable-diffusion.cpp CMake/JNI integration
- Model load/release, txt2img options, progress/cancel and bitmap conversion
- Sampler/scheduler option wiring
- Image-to-image picker copies source into app-private storage, downsamples/normalizes it to PNG, and passes init_image plus strength
- Mask editor paints white strokes on a black mask and passes mask_image with the selected source image
- Sequential batch UI runs up to four separate native calls and stores each successful result
- History restore reinstates sampler/scheduler and image-to-image source, strength and mask if source files remain available
- Custom prompt preset names/prompts are normalized and validated before persistence
- Gallery offers 2× filtered bitmap resize (not AI upscaling); bitmap memory is released in a finally path and partial output files are removed on failure
- History JSON writes retain the previous valid snapshot as a recovery fallback when the primary snapshot is unreadable
- Source option/API wiring is not compiled or runtime verified

## Device constraints
- ARM64 CPU baseline target; no GPU acceleration claim
- Model compatibility, quantization and peak memory depend on checkpoint, resolution and device
- RAM guard is heuristic, not an OOM guarantee
- Gallery uses private local PNG history; source and mask images are kept in app-private storage

## Remaining native gates
1. Compile and link pinned engine/JNI.
2. Confirm option names/signatures and supported image formats.
3. Test txt2img, img2img and inpainting using supported checkpoints.
4. Validate mask polarity and inpaint behavior.
5. Validate batch, progress, cancellation, errors, image orientation/color, memory and thermals.
6. Add an actual AI upscaler if supported models/backend become available; the current 2× resize is conventional interpolation.
7. Complete tests, compatibility, privacy/licensing and Play release qualification.
