# Engine Evaluation Notes

Diff is intended to support on-device inference where device capability and model license allow.

Engine integration is intentionally not wired into the UI scaffold yet. Evaluate:
- Android ABI support (arm64-v8a first)
- Vulkan/GPU acceleration and CPU fallback
- Model formats and conversion workflow
- Memory usage and thermal behavior
- License compatibility
- Stable API for JNI wrapper

Do not advertise generation as functional until the native engine, model loading, and output pipeline are integrated and tested.
