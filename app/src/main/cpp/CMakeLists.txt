cmake_minimum_required(VERSION 3.22.1)
project(diff_native_bridge LANGUAGES C CXX)

option(DIFF_ENABLE_SDCPP "Fetch and compile stable-diffusion.cpp native backend" ON)

if(DIFF_ENABLE_SDCPP)
    include(FetchContent)
    set(SD_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
    set(SD_BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
    set(SD_BUILD_SHARED_GGML_LIB OFF CACHE BOOL "" FORCE)
    set(SD_WEBP OFF CACHE BOOL "" FORCE)
    set(SD_WEBM OFF CACHE BOOL "" FORCE)
    set(SD_OPENCL OFF CACHE BOOL "" FORCE)
    set(SD_VULKAN OFF CACHE BOOL "" FORCE)
    set(GGML_OPENMP OFF CACHE BOOL "" FORCE)
    set(GGML_NATIVE OFF CACHE BOOL "" FORCE)
    set(GGML_CPU_ALL_VARIANTS OFF CACHE BOOL "" FORCE)

    FetchContent_Declare(
        stable_diffusion_cpp
        GIT_REPOSITORY https://github.com/leejet/stable-diffusion.cpp.git
        GIT_TAG 532d2c15c430a785a712b2d94281f04e733008a4
        GIT_SUBMODULES_RECURSE TRUE
        GIT_SHALLOW FALSE
    )
    FetchContent_MakeAvailable(stable_diffusion_cpp)
endif()

add_library(diff_native SHARED native_bridge.cpp)
find_library(log-lib log)

if(DIFF_ENABLE_SDCPP)
    target_link_libraries(diff_native PRIVATE stable-diffusion)
    target_compile_definitions(diff_native PRIVATE DIFF_HAS_SDCPP=1)
endif()

target_link_libraries(diff_native PRIVATE ${log-lib})
target_compile_features(diff_native PRIVATE cxx_std_17)
