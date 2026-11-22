# Engine Evaluation Notes

Diff targets offline on-device inference, arm64-v8a first.

## Native bridge status
- Android Gradle external native build (CMake 3.22.1)
- JNI shared library `libdiff_native.so`
- Runtime architecture and bridge status are exposed to Compose UI
- Bridge scaffolding only: no model inference or diffusion engine is compiled in.

## Candidate engine
Evaluate upstream `stable-diffusion.cpp` at a pinned revision. Android build support exists, but backend compatibility is device-dependent. Start with CPU/arm64 reliability, then evaluate Vulkan/OpenCL acceleration experimentally. Do not treat a successful native compile as proof GPU acceleration works.

## Integration requirements
- Isolate native engine symbols/shared library to avoid collisions.
- JNI API: model loading, generation, progress, cancellation and errors.
- User-selected model files; show model license and storage details.
- Detect available memory and limit default resolution on constrained phones.
- Verify model format, quantization and architecture compatibility before enabling Generate.

Generation remains unavailable until model loading, inference, image output and device testing are complete.
