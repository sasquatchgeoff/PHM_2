LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := detector
LOCAL_SRC_FILES := detector.c

LOCAL_LDLIBS += -landroid

include $(BUILD_SHARED_LIBRARY)