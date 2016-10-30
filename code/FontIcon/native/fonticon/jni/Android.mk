# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# the purpose of this sample is to demonstrate how one can
# generate two distinct shared libraries and have them both
# uploaded in
#

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := libfreetype2-static 
LOCAL_SRC_FILES := ../../freetype/obj/local/$(TARGET_ARCH_ABI)/libfreetype2-static.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
FREETYPE_SRC_PATH := ../../freetype/
SRC_PATH := ../src/

LOCAL_CFLAGS := -DANDROID_NDK \
		-DFT2_BUILD_LIBRARY=1

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include_all \
		$(FREETYPE_SRC_PATH)include
		
LOCAL_MODULE    := fonticon
LOCAL_SRC_FILES := \
	$(SRC_PATH)fonticon.c \
	$(SRC_PATH)loadglyph.c \
	$(SRC_PATH)generatebmp.c

LOCAL_LDLIBS := -ldl -llog -ljnigraphics -landroid

LOCAL_STATIC_LIBRARIES := libfreetype2-static

include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_EXECUTABLE)
