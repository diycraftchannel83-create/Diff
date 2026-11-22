#include <jni.h>
#include <android/bitmap.h>
#include <stable-diffusion.h>
#include <string>
#include <mutex>
#include <vector>
#include <cstdlib>
#include <cstring>
#include <atomic>
static sd_ctx_t* g_ctx = nullptr;
static std::mutex g_ctx_mutex;
static std::atomic<int> g_progress_step{0};
static std::atomic<int> g_progress_total{0};
static void progressCallback(const sd_generation_progress_t* progress, void*) { if (!progress) return; g_progress_step.store((int)progress->completed); g_progress_total.store((int)progress->total); }
static void throwJava(JNIEnv* env, const char* message) { jclass cls=env->FindClass("java/lang/IllegalStateException"); if(cls) env->ThrowNew(cls,message?message:"Native diffusion operation failed"); }
extern "C" JNIEXPORT jstring JNICALL Java_com_winsmediatam_diff_NativeRuntime_getRuntimeStatus(JNIEnv* env,jobject) {
#ifdef DIFF_HAS_SDCPP
 return env->NewStringUTF("stable-diffusion.cpp native inference API connected (ARM64 CPU backend).");
#else
 return env->NewStringUTF("Native bridge loaded. Diffusion engine is not compiled.");
#endif
}
extern "C" JNIEXPORT jstring JNICALL Java_com_winsmediatam_diff_NativeRuntime_getNativeArchitecture(JNIEnv* env,jobject) {
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
extern "C" JNIEXPORT jboolean JNICALL Java_com_winsmediatam_diff_NativeRuntime_loadModel(JNIEnv* env,jobject,jstring modelPath,jint threads) {
#ifdef DIFF_HAS_SDCPP
 if(!modelPath){throwJava(env,"Model path is missing.");return JNI_FALSE;}
 const char* path=env->GetStringUTFChars(modelPath,nullptr); if(!path)return JNI_FALSE;
 std::lock_guard<std::mutex> lock(g_ctx_mutex); if(g_ctx){sd_ctx_free(g_ctx);g_ctx=nullptr;}
 sd_ctx_opt_t* opts=sd_ctx_opt_create(); sd_status_t status=opts?sd_opt_set_string(opts,"model_path",path):SD_STATUS_OUT_OF_MEMORY;
 if(status==SD_STATUS_OK)status=sd_opt_set_int(opts,"n_threads",threads>0?threads:4);
 if(status==SD_STATUS_OK)status=sd_ctx_create(opts,&g_ctx); if(opts)sd_opt_free(opts);
 env->ReleaseStringUTFChars(modelPath,path);
 if(status!=SD_STATUS_OK||!g_ctx){const char* err=sd_get_last_error();throwJava(env,err?err:"Failed to load diffusion model.");if(g_ctx){sd_ctx_free(g_ctx);g_ctx=nullptr;}return JNI_FALSE;}
 return JNI_TRUE;
#else
 throwJava(env,"Stable Diffusion backend is not compiled."); return JNI_FALSE;
#endif
}
extern "C" JNIEXPORT void JNICALL Java_com_winsmediatam_diff_NativeRuntime_releaseModel(JNIEnv*,jobject) {
#ifdef DIFF_HAS_SDCPP
 std::lock_guard<std::mutex> lock(g_ctx_mutex);if(g_ctx){sd_ctx_free(g_ctx);g_ctx=nullptr;}
#endif
}
extern "C" JNIEXPORT void JNICALL Java_com_winsmediatam_diff_NativeRuntime_cancelGeneration(JNIEnv*,jobject) {
#ifdef DIFF_HAS_SDCPP
 if(g_ctx) sd_cancel_generation(g_ctx, SD_CANCEL_ALL);
#endif
}
extern "C" JNIEXPORT jintArray JNICALL Java_com_winsmediatam_diff_NativeRuntime_getGenerationProgress(JNIEnv* env,jobject) {
 jint values[2]={g_progress_step.load(),g_progress_total.load()};
 jintArray arr=env->NewIntArray(2); if(arr) env->SetIntArrayRegion(arr,0,2,values); return arr;
}
extern "C" JNIEXPORT jobject JNICALL Java_com_winsmediatam_diff_NativeRuntime_generateBitmap(JNIEnv* env,jobject,jstring prompt,jstring negative,jint width,jint height,jint steps,jfloat cfg,jlong seed,jstring sampler,jstring scheduler,jstring initImagePath,jfloat strength) {
#ifdef DIFF_HAS_SDCPP
 if(!prompt||width<64||height<64||width>2048||height>2048||steps<1||steps>100){throwJava(env,"Invalid generation parameters.");return nullptr;}
 const char* p=env->GetStringUTFChars(prompt,nullptr);const char* n=negative?env->GetStringUTFChars(negative,nullptr):nullptr;const char* sm=sampler?env->GetStringUTFChars(sampler,nullptr):nullptr;const char* sc=scheduler?env->GetStringUTFChars(scheduler,nullptr):nullptr;const char* ip=initImagePath?env->GetStringUTFChars(initImagePath,nullptr):nullptr;if(!p)return nullptr;
 std::lock_guard<std::mutex> lock(g_ctx_mutex);if(!g_ctx){env->ReleaseStringUTFChars(prompt,p);if(n)env->ReleaseStringUTFChars(negative,n);if(sm)env->ReleaseStringUTFChars(sampler,sm);if(sc)env->ReleaseStringUTFChars(scheduler,sc);if(ip)env->ReleaseStringUTFChars(initImagePath,ip);throwJava(env,"Load a model before generating.");return nullptr;}
 sd_img_gen_opt_t* opts=sd_img_gen_opt_create();sd_result_t* result=nullptr;sd_status_t status=opts?sd_opt_set_string(opts,"prompt",p):SD_STATUS_OUT_OF_MEMORY;
 if(status==SD_STATUS_OK&&n&&n[0])status=sd_opt_set_string(opts,"negative_prompt",n);\n if(status==SD_STATUS_OK&&ip&&ip[0])status=sd_opt_set_string(opts,"init_image",ip);\n if(status==SD_STATUS_OK&&ip&&ip[0])status=sd_opt_set_float(opts,"strength",strength);
 if(status==SD_STATUS_OK)status=sd_opt_set_int(opts,"width",width);
 if(status==SD_STATUS_OK)status=sd_opt_set_int(opts,"height",height);
 if(status==SD_STATUS_OK)status=sd_opt_set_int(opts,"seed",seed);
 if(status==SD_STATUS_OK)status=sd_opt_set_int(opts,"sample.steps",steps);
 if(status==SD_STATUS_OK)status=sd_opt_set_float(opts,"sample.guidance.txt_cfg",cfg);
 if(status==SD_STATUS_OK&&sm&&sm[0]&&strcmp(sm,"auto")!=0)status=sd_opt_set_string(opts,"sample.method",sm);
 if(status==SD_STATUS_OK&&sc&&sc[0]&&strcmp(sc,"auto")!=0)status=sd_opt_set_string(opts,"sample.scheduler",sc);
 if(status==SD_STATUS_OK){ g_progress_step.store(0); g_progress_total.store(0); sd_set_generation_progress_callback(g_ctx, progressCallback, nullptr); status=sd_generate_image(g_ctx,opts,&result); sd_set_generation_progress_callback(g_ctx,nullptr,nullptr); }
 env->ReleaseStringUTFChars(prompt,p);if(n)env->ReleaseStringUTFChars(negative,n);if(sm)env->ReleaseStringUTFChars(sampler,sm);if(sc)env->ReleaseStringUTFChars(scheduler,sc);if(ip)env->ReleaseStringUTFChars(initImagePath,ip);if(opts)sd_opt_free(opts);
 if(status!=SD_STATUS_OK||!result||sd_result_get_image_count(result)==0){const char* err=sd_get_last_error();throwJava(env,err?err:"Image generation failed.");if(result)sd_result_free(result);return nullptr;}
 const sd_tensor_t* tensor=sd_result_get_image(result,0);if(!tensor||sd_tensor_type(tensor)!=SD_TENSOR_U8||sd_tensor_rank(tensor)!=3){sd_result_free(result);throwJava(env,"Unsupported native output tensor.");return nullptr;}
 int64_t channels=sd_tensor_dim(tensor,0),outW=sd_tensor_dim(tensor,1),outH=sd_tensor_dim(tensor,2);const uint8_t* bytes=(const uint8_t*)sd_tensor_data(tensor);
 if(!bytes||channels<3||outW<=0||outH<=0||outW>4096||outH>4096){sd_result_free(result);throwJava(env,"Invalid native image tensor.");return nullptr;}
 jclass bc=env->FindClass("android/graphics/Bitmap");jclass cc=env->FindClass("android/graphics/Bitmap$Config");jfieldID cf=env->GetStaticFieldID(cc,"ARGB_8888","Landroid/graphics/Bitmap$Config;");jobject config=env->GetStaticObjectField(cc,cf);jmethodID create=env->GetStaticMethodID(bc,"createBitmap","(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");jobject bitmap=env->CallStaticObjectMethod(bc,create,(jint)outW,(jint)outH,config);
 if(!bitmap||env->ExceptionCheck()){sd_result_free(result);return nullptr;}
 std::vector<jint> pixels((size_t)outW*(size_t)outH);for(size_t i=0;i<pixels.size();i++){uint8_t r=bytes[i*3],g=bytes[i*3+1],b=bytes[i*3+2];pixels[i]=(jint)(0xFF000000u|((uint32_t)r<<16)|((uint32_t)g<<8)|b);}
 jmethodID set=env->GetMethodID(bc,"setPixels","([IIIIII)V");jintArray arr=env->NewIntArray((jsize)pixels.size());if(arr){env->SetIntArrayRegion(arr,0,(jsize)pixels.size(),pixels.data());env->CallVoidMethod(bitmap,set,arr,0,(jint)outW,0,0,(jint)outW,(jint)outH);env->DeleteLocalRef(arr);}
 sd_result_free(result);if(env->ExceptionCheck())return nullptr;return bitmap;
#else
 throwJava(env,"Stable Diffusion backend is not compiled.");return nullptr;
#endif
}
