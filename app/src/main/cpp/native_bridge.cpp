#include <jni.h>
#include <string>
extern "C" JNIEXPORT jstring JNICALL
Java_com_winsmediatam_diff_NativeRuntime_getRuntimeStatus(JNIEnv* env, jobject) {
    const std::string status = "Native bridge loaded (arm64-v8a). Diffusion engine is not bundled yet.";
    return env->NewStringUTF(status.c_str());
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_winsmediatam_diff_NativeRuntime_getNativeArchitecture(JNIEnv* env, jobject) {
#if defined(__aarch64__)
    return env->NewStringUTF("ARM64");
#elif defined(__arm__)
    return env->NewStringUTF("ARM32");
#elif defined(__x86_64__)
    return env->NewStringUTF("x86_64");
#else
    return env->NewStringUTF("Unknown");
#endif
}
