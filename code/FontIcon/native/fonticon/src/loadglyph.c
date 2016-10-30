/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

#include "fonticon.h"

#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_GLYPH_H
#include FT_IMAGE_H

int WIDTH  = 0;
int HEIGHT = 0;
BYTE *IMAGE = NULL;

#define PIXLE_UINT 64

void print_global_metric(FT_Face pFTFace)
{
	printf("****************************************\n");

	FT_Size_Metrics metric = pFTFace->size->metrics;

	const int EM_UNIT = (int)pFTFace->units_per_EM;

	printf("EM SIZE:%d \n", (int)pFTFace->units_per_EM);
	printf("FT_IS_SCALABLE:%d \n", (int)(FT_IS_SCALABLE(pFTFace)));

	printf("Global Metric: \nascender:%f \ndescender:%f \nheight(LINE HGIEHT):%f\n",
		(float)(pFTFace->ascender)/EM_UNIT, (float)(pFTFace->descender) / EM_UNIT, (float)(pFTFace->height) / EM_UNIT);

	printf("max_advance_width:%f \n", (float)pFTFace->max_advance_width / EM_UNIT);
	printf("max_advance_height:%f \n", (float)pFTFace->max_advance_height / EM_UNIT);

	printf("Box Size, x:[%f, %f], y:[%f, %f]\n",
		(float)pFTFace->bbox.xMin / EM_UNIT, (float)pFTFace->bbox.xMax / EM_UNIT,
		(float)pFTFace->bbox.yMin / EM_UNIT, (float)pFTFace->bbox.yMax / EM_UNIT);

	printf("++++Face Size Metric++++:\n");
	printf("metric.ascender:%d \n", (int)metric.ascender/ PIXLE_UINT);
	printf("metric.descender:%d \n", (int)metric.descender/ PIXLE_UINT);
	printf("metric.height:%d \n", (int)metric.height/ PIXLE_UINT);

	printf("metric.max_advance:%d \n", (int)metric.max_advance/ PIXLE_UINT);

	printf("metric.x_ppem:%d \n", (int)metric.x_ppem);
	printf("metric.x_scale:%d \n", (int)metric.x_scale);

	printf("metric.y_ppem:%d \n", (int)metric.y_ppem);
	printf("metric.y_scale:%d \n", (int)metric.y_scale);
}

void print_glyph_metric(FT_GlyphSlot slot)
{
	printf("*************************************\n");

	printf("slot->metrics.width: %d \n", (int)(slot->metrics.width) / PIXLE_UINT);
	printf("slot->metrics.height: %d \n", (int)(slot->metrics.height) / PIXLE_UINT);

	printf("slot->metrics.horiAdvance: %d \n", (int)(slot->metrics.horiAdvance) / PIXLE_UINT);
	printf("slot->metrics.horiBearingX: %d \n", (int)(slot->metrics.horiBearingX) / PIXLE_UINT);
	printf("slot->metrics.horiBearingY: %d \n", (int)(slot->metrics.horiBearingY) / PIXLE_UINT);

	printf("slot->metrics.vertAdvance: %d \n", (int)(slot->metrics.vertAdvance) / PIXLE_UINT);
	printf("slot->metrics.vertBearingX: %d \n", (int)(slot->metrics.vertBearingX) / PIXLE_UINT);
	printf("slot->metrics.vertBearingY: %d \n", (int)(slot->metrics.vertBearingY) / PIXLE_UINT);

	printf("*************************************\n");

	printf("slot->bitmap_left: %d \n", (int)(slot->bitmap_left));
	printf("slot->bitmap_top: %d \n", (int)(slot->bitmap_top));
	printf("slot->bitmap.width: %d \n", (int)(slot->bitmap.width));
	printf("slot->bitmap.rows: %d \n", (int)(slot->bitmap.rows));
}

static void draw_bitmap(FT_Bitmap* bitmap, FT_Int x, FT_Int y)
{
	FT_Int  i, j, p, q;
	FT_Int  x_max = x + bitmap->width;
	FT_Int  y_max = y + bitmap->rows;

	for (i = x, p = 0; i < x_max; i++, p++)
	{
		for (j = y, q = 0; j < y_max; j++, q++)
		{
			if (i < 0 || j < 0 ||
				i >= WIDTH || j >= HEIGHT)
				continue;

			*(IMAGE + j * WIDTH + i) = bitmap->buffer[q * bitmap->width + p];
		}
	}
}

static void show_image(void)
{
	int  i, j;
	for (i = 0; i < HEIGHT; i++)
	{
		for (j = 0; j < WIDTH; j++)
		{
			BYTE value = *(IMAGE + i * WIDTH + j);
			putchar(value == 0 ? ' '
				: value < 128 ? '+'
				: '*');
		}
		putchar('\n');
	}
}

FT_Face load_face(FT_Library pFTLib)
{
	FT_Face     pFTFace = NULL;
	FT_Error    error = 0;

#ifdef _MSC_VER
	const char *pFontPath = "C:\\fontGqqV2.ttf";
#else
	const char *pFontPath = "/data/local/tmp/fontGqqV2.ttf";
	#define  FONT_FILE "fontGqqV2.ttf"
#endif // _MSC_VER

#ifndef _MSC_VER

	void *font_data = NULL;
	int   font_size = 0;
	font_size = load_asset(FONT_FILE, &font_data);
	if (font_data != NULL)
	{
		error = FT_New_Memory_Face(pFTLib, font_data, font_size, 0, &pFTFace);
		if (error){
			LOGE("New Memory Face Failed, error:%d.\n", error);
		}
		free(font_data); font_data = NULL;
	}
	else 
	{
		LOGE("Load Asset Failed:%d.\n", error);
	}

#endif // !_MSC_VER

	if (pFTFace != NULL){
		LOGD("Load Face from Asset Succeeded.\n");
		return pFTFace;
	}

	error = FT_New_Face(pFTLib, pFontPath, 0, &pFTFace);
	if (error) {
		LOGE("New Face Failed, error:%d.\n", error);
		return NULL;
	}

	return pFTFace;
}

int load_char_bitmap(long charCode, int textSize)
{
	FT_Library  pFTLib = NULL;
	FT_Face     pFTFace = NULL;
	FT_Error    error = 0;

	FT_GlyphSlot  slot  = NULL;
	FT_Glyph      glyph = NULL; /* a handle to the glyph image */
	FT_BBox		  bbox;

	int ret = 0;

	do 
	{
		error = FT_Init_FreeType(&pFTLib);
		if (error){
			LOGE("Init FreeType Failed, error:%d\n", error);
			ret = -1;
			break;
		}

		pFTFace = load_face(pFTLib);
		if (pFTFace == NULL){
			LOGE("Load Face Failed.\n");
			ret = -2;
			break;
		}

		LOGI("Load Face succeeded.\n");

		error = FT_Set_Pixel_Sizes(pFTFace, textSize, textSize);
		if (error){
			LOGE("Set Pixel Size Failed, error:%d\n", error);
			ret = -3;
			break;
		}

		print_global_metric(pFTFace);

		error = FT_Load_Char(pFTFace, charCode, FT_LOAD_RENDER);
		if (error){
			LOGE("Load Char Failed, error:%d.\n", error);
			ret = -4;
			break;
		}

		slot = pFTFace->glyph;
		print_glyph_metric(slot);

		error = FT_Get_Glyph(pFTFace->glyph, &glyph);
		if (error){
			LOGE("Get Glyph Failed, error:%d.\n", error);
			ret = -5;
			break;
		}

		LOGI("Load Glyph Succeeded.\n");

		FT_Glyph_Get_CBox(glyph, FT_GLYPH_BBOX_UNSCALED, &bbox);

		LOGD("*************************************\n");
		LOGD("Glyph Box Size, x:[%d, %d], y:[%d, %d].\n",
			(int)bbox.xMin / PIXLE_UINT, (int)bbox.xMax / PIXLE_UINT,
			(int)bbox.yMin / PIXLE_UINT, (int)bbox.yMax / PIXLE_UINT);

		assert((int)bbox.xMin / PIXLE_UINT == slot->bitmap_left);
		assert((int)bbox.yMax / PIXLE_UINT == slot->bitmap_top);

		assert((int)bbox.xMax / PIXLE_UINT - (int)bbox.xMin / PIXLE_UINT == slot->bitmap.width);
		assert((int)bbox.yMax / PIXLE_UINT - (int)bbox.yMin / PIXLE_UINT == slot->bitmap.rows);

		/* float 转 int, 可能有一个像素的误差 */
		const float faceBoxHeightEM = ((float)(pFTFace->bbox.yMax - pFTFace->bbox.yMin)) / pFTFace->units_per_EM;
		const int faceBoxHeightPixel = (int)(faceBoxHeightEM * textSize);

		const float faceBoxTopEM = ((float)(pFTFace->bbox.yMax)) / pFTFace->units_per_EM;
		const int   faceBoxTopPixel = (int)(faceBoxTopEM * textSize);

		const int glyphWidth   = slot->metrics.width / PIXLE_UINT;
		const int glyphHeight  = slot->metrics.height / PIXLE_UINT;
		const int glyphHoriAdvance = slot->metrics.horiAdvance / PIXLE_UINT;

		assert(glyphWidth == slot->bitmap.width);
		assert(glyphHeight == slot->bitmap.rows);

		WIDTH  = glyphHoriAdvance;
		HEIGHT = faceBoxHeightPixel;

		LOGD("Gobal Face Box Height:%d.\n", faceBoxHeightPixel);
		LOGD("Gobal Face Box Top:%d.\n", faceBoxTopPixel);
		
		LOGD("Glyph Size:[%d, %d].\n", glyphWidth, glyphHeight);
		LOGD("Glyph Hori Advance:%d.\n", glyphHoriAdvance);

		IMAGE = (BYTE*)malloc(WIDTH * HEIGHT);
		memset(IMAGE, 0x00, WIDTH * HEIGHT);

		assert(faceBoxTopPixel >= slot->bitmap_top);
		draw_bitmap(&slot->bitmap, slot->bitmap_left, faceBoxTopPixel - slot->bitmap_top);
	} while (0);

	if (glyph){
		FT_Done_Glyph(glyph);
	}

	if (pFTFace){
		FT_Done_Face(pFTFace);
	}

	if (pFTLib){
		FT_Done_FreeType(pFTLib);
	}

	return ret;
}



#ifdef _MSC_VER

int save_as_bmp(char* szPathName, void* lpBits, int w, int h);

#endif

int main()
{
	int textSize = 48;
	int textColor = 0xffff0000;
	FT_ULong charCode = 0x4000;

	void *buff = NULL;

	int ret = load_char_bitmap(charCode, textSize);
	if (ret == 0)
	{
		show_image();

#ifdef _MSC_VER

		buff = create_rgb32_from_char_bitmap(IMAGE, textColor);
		if (buff != NULL) {
			save_as_bmp("c:\\font.bmp", buff, WIDTH, HEIGHT);
		}

#endif

	}

	free(buff);
	return 0;
}
