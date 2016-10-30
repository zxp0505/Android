#ifndef __INCLUDE_H__
#define __INCLUDE_H__

#ifdef _MSC_VER
#include "stdafx.h"
#else  // _MSC_VER
#include <jni.h>
#include <time.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <android/asset_manager.h>
#include <stdlib.h>
typedef unsigned char BYTE;
#endif 

#include <assert.h>

#ifdef _MSC_VER
#define  LOGD(format, ...)  fprintf(stdout, format, __VA_ARGS__)
#define  LOGI(format, ...)  fprintf(stdout, format, __VA_ARGS__)
#define  LOGE(format, ...)  fprintf(stderr, format, __VA_ARGS__)
#else // _MSC_VER
#define  LOG_TAG    "IconFontTextView"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#endif 

extern int WIDTH;
extern int HEIGHT;
extern BYTE *IMAGE;

int load_asset(const char *file, void **data);

int  load_char_bitmap(long charCode, int textSize);
void free_char_bitmap(int nativeBmp);

void *create_rgb32_from_char_bitmap(int nativeBmp, int textColor);
int  get_char_bitmap_gray(int nativeBmp, int x, int y);

#endif
