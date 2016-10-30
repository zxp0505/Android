#include "fonticon.h"

#ifndef _MSC_VER

static AAssetManager *gAssetMgr = NULL;

int load_asset(const char *file, void **data)
{
	if (gAssetMgr == NULL) {
		LOGE("AssetManager is null.\n");
		return -1;
	}

	int ret = 0;
	do {
		AAsset *pAsset = AAssetManager_open(gAssetMgr, file, AASSET_MODE_UNKNOWN);
		if (pAsset == NULL) {
			LOGE("Asset open failed.\n");
			ret = -2;
			break;
		}

		size_t size = AAsset_getLength(pAsset);
		if (size <= 0) {
			LOGE("Asset getLength failed.\n");
			ret = -3;
			break;
		}

		if (data != NULL)
		{
			*data = malloc(size);
			int read = AAsset_read(pAsset, *data, size);
			if (read <= 0) {
				LOGE("Asset read failed.\n");
				ret = -3;
				free(*data);
				*data = NULL;
			}
			else {
				LOGD("Asset read succeeeded.\n");
				ret = size;
			}
		}

		AAsset_close(pAsset);
	} while (0);

	return ret;
}

void set_glyph_info(JNIEnv* env, jobject this, int width, int height, void *image)
{
	jfieldID fid;

	LOGI("Set Glyph Size to Java:(%d, %d).\n", width, height);

	jclass cls = (*env)->GetObjectClass(env, this);

	fid = (*env)->GetFieldID(env, cls, "mCharBitmapW", "I");
	(*env)->SetIntField(env, this, fid, width);

	fid = (*env)->GetFieldID(env, cls, "mCharBitmapH", "I");
	(*env)->SetIntField(env, this, fid, height);

	fid = (*env)->GetFieldID(env, cls, "mCharBitmap", "I");
	(*env)->SetIntField(env, this, fid, (jint)(image));
}

jint
Java_com_tencent_gamejoy_ui_global_widget_IconFontTextView_setAssetManager(JNIEnv* env, jobject this, jobject assetManager)
{
	if (gAssetMgr != NULL){
		return 0;
	}

	gAssetMgr = (AAssetManager *)AAssetManager_fromJava(env, assetManager);
	if (gAssetMgr == NULL) {
		LOGE("Get AssetManager failed.\n");
		return -1;
	}

	return 0;
}

jint
Java_com_tencent_gamejoy_ui_global_widget_IconFontTextView_loadCharBitmap(JNIEnv* env,
	jobject  this,
	jint     textCode,
	jint     textSize)
{
	int ret = load_char_bitmap(textCode, textSize);
	if (ret == 0){
		LOGD("Load Glyph succeeded, Width:%d, Height:%d.\n", WIDTH, HEIGHT);
		set_glyph_info(env, this, WIDTH, HEIGHT, IMAGE);
	}
	return ret;
}

jint
Java_com_tencent_gamejoy_ui_global_widget_IconFontTextView_destroyCharBitmap(JNIEnv* env,
	jobject  this,
	jint nativeBmp)
{
	free_char_bitmap(nativeBmp);
	return 0;
}

jint
Java_com_tencent_gamejoy_ui_global_widget_IconFontTextView_getCharBitmapPixel(JNIEnv* env,
	jobject this,
	jint nativeBmp,
	jint x, 
	jint y)
{
	return get_char_bitmap_gray(nativeBmp, x, y);
}

jint
Java_com_tencent_gamejoy_ui_global_widget_IconFontTextView_updateIconBitmap(JNIEnv* env,
	jobject  this,
	jint nativeBmp,
	jint textColor,
	jobject  bitmap)
{
	AndroidBitmapInfo  info;
	void*              pixels;
	int                ret;

	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d.\n", ret);
		return -1;
	}

	if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
		LOGE("Bitmap format is not RGB_888!\n");
		return -2;
	}

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d.\n", ret);
		return -3;
	}

	void *buff = create_rgb32_from_char_bitmap(nativeBmp, textColor);
	if (buff != NULL){
		memcpy(pixels, buff, info.width * info.height * 4);
		free(buff);
		ret = 0;
	}
	else { ret = -4; }

	AndroidBitmap_unlockPixels(env, bitmap);
	return ret;
}




#endif // !_MSC_VER