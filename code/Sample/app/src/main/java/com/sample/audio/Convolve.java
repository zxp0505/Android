package com.sample.audio;

/**
 * step 4: HRTF convolution 卷积变换
 * <p>
 * http://www.digenis.co.uk/?p=81
 * <p>
 * https://github.com/krruzic/convolver/blob/master/src/convolve.c
 */

public class Convolve {

    static void Convolve(short[] input,
                         int inputLength,
                         float[] filter,
                         int filterLength,
                         short[] output) {
        // int lengthOfOutput = inputLength + filterLength - 1;

//        for(int i = 0; i < lengthOfOutput; ++i) {
//            output[i] = 0;
//        }

        for (int i = 0; i < inputLength; ++i) {
            for (int j = 0; j < filterLength; ++j) {
                output[i + j] += input[i] * filter[j];
            }
        }

        /*
        int lengthOfOutput = inputLength;
        for(int i = 0; i < lengthOfOutput; ++i)
        {
            output[i] = 0;

            for(int j=0; j<filterLength; j++){
                output[i] += input[i] * filter[j];
            }
        }
        */
    }
}
