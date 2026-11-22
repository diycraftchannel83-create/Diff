# Engine Evaluation Notes

Diff AI Studio targets offline on-device inference, ARM64 first.

## Current source status
- Android CMake/JNI bridge configured to fetch a pinned stable-diffusion.cpp revision
- Native model loading and txt2img option wiring are implemented in source
- Progress callback, cancellation request, and tensor-to-Android-Bitmap conversion are source-integrated
- Native API and build compatibility are **not verified**; build/CI is intentionally deferred
- Input image, inpainting mask, batch generation, and upscale require additional verified upstream API wiring

## Device constraints
- ARM64 CPU baseline is the target for the initial implementation
- GPU/Vulkan acceleration remains experimental and is not claimed
- Model compatibility and peak memory depend on checkpoint, quantization, resolution and device
- App shows device RAM/architecture and applies a conservative request guard, but this is heuristic only

## Release gates
1. Compile the pinned engine and JNI bridge.
2. Confirm JNI API signatures against the pinned upstream headers.
3. Test loading supported model formats and quantization.
4. Generate multiple sizes and verify bitmap orientation/color correctness.
5. Validate cancel, failure, progress and memory behavior.
6. Run real-device performance and thermal tests.
7. Complete license, privacy, export/share and Play release review.
