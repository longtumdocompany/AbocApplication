cmake_minimum_required(VERSION 3.6)

set(CMAKE_VERBOSE_MAKEFILE on)
set(libs "${CMAKE_SOURCE_DIR}/../jniLibs")

include_directories(include)

#--------------------------------------------------- import ---------------------------------------------------#
add_library(libopencv_java3 SHARED IMPORTED )
set_target_properties(libopencv_java3 PROPERTIES
    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_java3.so")

add_library(libpngo SHARED IMPORTED )
set_target_properties(libpngo PROPERTIES
    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libpngo.so")

add_library(libopencv_core STATIC IMPORTED )
set_target_properties(libopencv_core PROPERTIES
    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_core.a")

add_library(libopencv_features2d STATIC IMPORTED )
set_target_properties(libopencv_features2d PROPERTIES
    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_features2d.a")

add_library(libopencv_flann STATIC IMPORTED )
set_target_properties(libopencv_flann PROPERTIES
    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_flann.a")

add_library(libopencv_highgui STATIC IMPORTED )
set_target_properties(libopencv_highgui PROPERTIES
    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_highgui.a")

add_library(libopencv_ml STATIC IMPORTED )
set_target_properties(libopencv_ml PROPERTIES
    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_ml.a")

add_library(libopencv_objdetect STATIC IMPORTED )
set_target_properties(libopencv_objdetect PROPERTIES
    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_objdetect.a")

add_library(libopencv_shape STATIC IMPORTED )
set_target_properties(libopencv_shape PROPERTIES
    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_shape.a")

add_library(libopencv_stitching STATIC IMPORTED )
set_target_properties(libopencv_stitching PROPERTIES
    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_stitching.a")

add_library(libopencv_superres STATIC IMPORTED )
set_target_properties(libopencv_superres PROPERTIES
    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_superres.a")

# this library is missing from some ABIs but if you need it, uncomment the lines
# and add libopencv_ts to target_link_libraries below
#add_library(libopencv_ts STATIC IMPORTED )
#set_target_properties(libopencv_ts PROPERTIES
#    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_ts.a")

########################### Detection Based Tracker ###########################
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11 -fexceptions -frtti")
add_library(detectionBasedTracker SHARED DetectionBasedTracker_jni.cpp)

# add lib dependencies
target_link_libraries(detectionBasedTracker android log
    libopencv_java3 libopencv_core libopencv_features2d libopencv_flann libopencv_highgui
    libopencv_ml libopencv_objdetect libopencv_shape libopencv_stitching libopencv_superres )
