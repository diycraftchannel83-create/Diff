# Engine Evaluation Notes

Diff AI Studio targets offline on-device inference, ARM64 first.

## Source integration status
- Pinned stable-diffusion.cpp CMake/JNI integration
- Model load/release and text-to-image options
- Progress callback, cancellation and tensor-to-Bitmap conversion
- Sampler/scheduler option wiring
- Image-to-image source picker copies source to private app files and passes path with init_image; strength passed via strength
- Source option/API wiring is not compiled or runtime verified

## Device constraints
- ARM64 CPU baseline target; no GPU acceleration claim
- Model compatibility, quantization and peak memory depend on checkpoint, resolution and device
- RAM guard is heuristic, not an OOM guarantee
- Gallery uses private local PNG history; input images are kept in app-private storage

## Remaining native gates
1. Compile and link pinned engine/JNI.
2. Confirm option names/signatures and supported image formats.
3. Test txt2img and img2img using supported checkpoints.
4. Add inpainting mask support and batch generation.
5. Validate progress, cancellation, error paths, image orientation/color, memory and thermal behavior.
6. Implement/benchmark upscale.
7. Complete tests, compatibility, privacy/licensing and Play release qualification.
