# Auto-generated module by script
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := libz
LOCAL_C_INCLUDES := 
LOCAL_CFLAGS := 
LOCAL_CPPFLAGS := 
LOCAL_LDLIBS := 
LOCAL_SHARED_LIBRARIES := 
LOCAL_STATIC_LIBRARIES := 
LOCAL_PREBUILTS := 
LOCAL_SRC_FILES := ./adler32.c \
./compress.c \
./crc32.c \
./deflate.c \
./gzclose.c \
./gzlib.c \
./gzread.c \
./gzwrite.c \
./infback.c \
./inffast.c \
./inflate.c \
./inftrees.c \
./trees.c \
./uncompr.c \
./zutil.c \

include $(BUILD_STATIC_LIBRARY)