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

/** return the alpha byte from a SkColor value */
#define SkColorGetA(color)      (((color) >> 24) & 0xFF)
/** return the red byte from a SkColor value */
#define SkColorGetR(color)      (((color) >> 16) & 0xFF)
/** return the green byte from a SkColor value */
#define SkColorGetG(color)      (((color) >>  8) & 0xFF)
/** return the blue byte from a SkColor value */
#define SkColorGetB(color)      (((color) >>  0) & 0xFF)

#ifdef _MSC_VER

#define SkColorSetARGBMacro(a, r, g, b) ( ((a) << 24) | ((r) << 16) | ((g) << 8) | ((b) << 0) )

#else

#define SkColorSetARGBMacro(a, r, g, b) ( ((a) << 24) | ((b) << 16) | ((g) << 8) | ((r) << 0) )

#endif // _MSC_VER

int computer_argb(int gray, int textColor)
{
	// http://web.comhem.se/~u34598116/content/FreeType2/main.html

	const  int A = SkColorGetA(textColor);
	const  int R = SkColorGetR(textColor);
	const  int G = SkColorGetG(textColor);
	const  int B = SkColorGetB(textColor);

	return SkColorSetARGBMacro(gray, R, G, B);
}

void *create_rgb32_from_char_bitmap(int nativeBmp, int textColor)
{
	int i, j;
	int width, height;
	BYTE *src;
	void *buff;

	width  = WIDTH;
	height = HEIGHT;
	src = (void *)nativeBmp;

	if (src == NULL){
		LOGE("Glyph image is null.\n");
		return NULL;
	}

	buff = malloc(width * height * 4);
	memset(buff, 0x00, width * height * 4);

	for (i = 0; i < height; i++)
	{
		for (j = 0; j < width; j++)
		{
			BYTE *row = (BYTE *)buff + width * i * 4;
			const int gray = src[i * width + j];
			int value = 0;
			if (gray != 0) {
				value = computer_argb(gray, textColor);
				*((int*)(row + j * 4)) = value;
			}
		}
	}

	return buff;
}

int get_char_bitmap_gray(int nativeBmp, int x, int y)
{
	int width, height;
	BYTE *src;

	width = WIDTH;
	height = HEIGHT;
	src = (void *)nativeBmp;

	if (src == NULL) {
		LOGE("Glyph image is null.\n");
		return 0;
	}

	if (x >= width || y >= height){
		LOGE("Invalid Pos.\n");
		return 0;
	}

	const int gray = src[y * width + x];
	return gray;
}

void free_char_bitmap(int nativeBmp)
{
	BYTE *src;
	src = (void *)nativeBmp;
	free(src);
}