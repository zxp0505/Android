all:
	@ndk-build -B -C ./freetype/jni
	@ndk-build -B -C ./fonticon/jni
	@cp ./fonticon/libs/armeabi/libfonticon.so ../FontIcon/app/src/main/jniLibs/armeabi/


clean:
	@rm -rf ./freetype/obj ./freetype/libs
	@rm -rf ./fonticon/obj ./fonticon/libs
	


