# Engine Evaluation Notes

Diff AI Studio targets offline on-device inference, ARM64 first.

## Source status
- Android CMake/JNI bridge fetches a pinned stable-diffusion.cpp revision
- Native model load and txt2img option wiring are source implemented
- Native progress callback and cancel wiring are integrated
- The progress callback declaration was checked against the pinned public header and uses the context-local progress struct
- Upstream C API/docs confirm image input key `init_image`, image mask `mask_image`, image strength `strength`, batch count `batch_count`, and sampler/scheduler keys `sample.method` / `sample.scheduler`
- JNI/API source is still **not compiled or device tested**

## Device constraints
- ARM64 CPU baseline target; no GPU acceleration claims
- Model compatibility, quantization and peak memory depend on checkpoint, resolution and device
- The RAM guard is a heuristic, not a guarantee against OOM
- Private generation history currently stores PNGs and metadata locally

## Release gates
1. Build pinned native engine/JNI and fix compile/link issues.
2. Validate native loading and inference with a compatible model.
3. Add image-to-image image picker, tensor conversion and native option.
4. Add inpainting mask editor and batch/sampler/scheduler controls.
5. Test cancel, progress, failure, output orientation/color and memory behavior.
6. Add upscale and optimization/benchmark tests.
7. Complete UI/repository tests, device matrix, privacy/licensing and Play release qualification.
