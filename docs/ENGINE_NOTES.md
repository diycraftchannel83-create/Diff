# Engine Evaluation Notes

Diff targets offline on-device inference, arm64-v8a first.

## Native bridge status
- Android Gradle external native build (CMake 3.22.1)
- JNI shared library `libdiff_native.so`
- Runtime architecture and bridge status are exposed to Compose UI
- Bridge scaffolding only: no model inference or diffusion engine is compiled in.

## Candidate engine
CMake root enables both C and CXX because GGML performs C compiler feature checks during configure. Pinned upstream `stable-diffusion.cpp` revision `532d2c15c430a785a712b2d94281f04e733008a4` is fetched via CMake FetchContent with recursive submodules. Android app native build is configured for ARM64 CPU-only baseline (OpenMP/OpenCL/Vulkan disabled). Build success only verifies native compilation/linking; model inference API is not yet wired into JNI. Evaluate Vulkan/OpenCL acceleration experimentally after CPU baseline.

## Integration requirements
- Isolate native engine symbols/shared library to avoid collisions.
- JNI API: model loading, generation, progress, cancellation and errors.
- User-selected model files; current app has a SAF importer storing files in app-private `files/models`.
- Show model license and storage details before runtime loading.
- Detect available memory and limit default resolution on constrained phones.
- Verify model format, quantization and architecture compatibility before enabling Generate.

Generation remains unavailable until model loading, inference, image output and device testing are complete.
