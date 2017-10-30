# Auto-generated module by script
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := libpng
LOCAL_C_INCLUDES := 
LOCAL_CFLAGS :=
LOCAL_CPPFLAGS := 
LOCAL_LDLIBS := 
LOCAL_SHARED_LIBRARIES := 
LOCAL_STATIC_LIBRARIES := libz
LOCAL_PREBUILTS := 
LOCAL_SRC_FILES := ./png.c \
./pngerror.c \
./pngget.c \
./pngmem.c \
./pngpread.c \
./pngread.c \
./pngrio.c \
./pngrtran.c \
./pngrutil.c \
./pngset.c \
./pngtrans.c \
./pngwio.c \
./pngwrite.c \
./pngwtran.c \
./pngwutil.c \
./arm/arm_init.c \
./arm/filter_neon.S \
./arm/filter_neon_intrinsics.c


include $(BUILD_STATIC_LIBRARY)

# Auto-generated module by script
include $(CLEAR_VARS)

LOCAL_MODULE := pngtest
LOCAL_C_INCLUDES := 
LOCAL_CFLAGS := 
LOCAL_CPPFLAGS := 
LOCAL_LDLIBS := 
LOCAL_SHARED_LIBRARIES := 
LOCAL_STATIC_LIBRARIES := libpng
LOCAL_PREBUILTS := 
LOCAL_SRC_FILES := ./pngtest.c

include $(BUILD_EXECUTABLE)