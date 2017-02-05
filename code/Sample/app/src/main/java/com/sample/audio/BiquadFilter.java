package com.sample.audio;

import android.util.Log;

/**
 * 根据频率，滤波变换，过滤出低频声音，直接输出。低频声音方向性不强，不用进行HRTF变换。
 * <p>
 * https://github.com/hosackm/BiquadFilter
 * https://codeandsound.wordpress.com/2015/04/08/implementing-binaural-hrtf-panner-node-with-web-audio-api/
 */

public class BiquadFilter {

    private final static String TAG = "BiquadFilter";
    private final static float M_PI = 3.141592654f;

    public enum FILTER_TYPES {
        LOWPASS,
        HIGHPASS,
        BANDPASS,
        NOTCH,
        PEAK,
        LOWSHELF,
        HIGHSHELF,
    }

    public static class biquad {
        public float a0;
        public float a1;
        public float a2;
        public float b0;
        public float b1;
        public float b2;
        public float prev_input_1;
        public float prev_input_2;
        public float prev_output_1;
        public float prev_output_2;
        String type;
    }

    public biquad bq_new(FILTER_TYPES filter_type,
                         float frequency,
                         float Q,
                         float dbGain,
                         int sample_rate) {

        biquad tmp = new biquad();

// Calculate helper variables for
// generating 'a' and 'b' coefficients
//////////////////////////////////////
        float A = (float) Math.pow(10, dbGain / 40); //convert to db
        float omega = 2 * M_PI * frequency / sample_rate;
        float sn = (float) Math.sin(omega);
        float cs = (float) Math.cos(omega);
        float alpha = sn / (2 * Q);
        float beta = (float) Math.sqrt(A + A);


// Load 'a' and 'b' coefficients
// into tmp biquad
/////////////////////////////////
        bq_load_coefficients(tmp, filter_type,
                A, omega,
                sn, cs,
                alpha, beta);

//Scale coeffs to a0
////////////////////
        // tmp.a1 = (tmp.a1) / (tmp.a0);
        // tmp.a2 = (tmp.a2) / (tmp.a0);
        // tmp.b0 = (tmp.b0) / (tmp.a0);
        // tmp.b1 = (tmp.b1) / (tmp.a0);
        // tmp.b2 = (tmp.b2) / (tmp.a0);
        tmp.a1 /= (tmp.a0);
        tmp.a2 /= (tmp.a0);
        tmp.b0 /= (tmp.a0);
        tmp.b1 /= (tmp.a0);
        tmp.b2 /= (tmp.a0);


// Load rest of data
/////////////////////////////////
        tmp.prev_input_1 = 0.0f;
        tmp.prev_input_2 = 0.0f;
        tmp.prev_output_1 = 0.0f;
        tmp.prev_output_2 = 0.0f;

        return tmp;
    }

    float bq_process(biquad bq, float input) {
        float output = (bq.b0 * input) +
                (bq.b1 * bq.prev_input_1) +
                (bq.b2 * bq.prev_input_2) -
                (bq.a1 * bq.prev_output_1) -
                (bq.a2 * bq.prev_output_2);

        bq.prev_input_2 = bq.prev_input_1;
        bq.prev_input_1 = input;
        bq.prev_output_2 = bq.prev_output_1;
        bq.prev_output_1 = output;
        //update last samples...
        return output;
    }

    void bq_destroy(biquad bq) {
    }

    void bq_print_info(biquad bq) {
        Log.d(TAG, "A0:" + bq.a0);
        Log.d(TAG, "A1:" + bq.a1);
        Log.d(TAG, "A2:" + bq.a2);
        Log.d(TAG, "B0:" + bq.b0);
        Log.d(TAG, "B1:" + bq.b1);
        Log.d(TAG, "B2:" + bq.b2);
        Log.d(TAG, "TYPE:" + bq.type);
        Log.d(TAG, "Last input:" + bq.prev_input_1);
        Log.d(TAG, "Second to last input:" + bq.prev_input_2);
        Log.d(TAG, "Last output:" + bq.prev_output_1);
        Log.d(TAG, "Second to last output:" + bq.prev_output_2);
    }

    void bq_load_coefficients(biquad bq, FILTER_TYPES filter_type,
                              float A, float omega,
                              float sn, float cs,
                              float alpha, float beta) {

        switch (filter_type) {
            case LOWPASS:
                bq.b0 = (1.0f - cs) / 2.0f;
                bq.b1 = 1.0f - cs;
                bq.b2 = (1.0f - cs) / 2.0f;
                bq.a0 = 1.0f + alpha;
                bq.a1 = -2.0f * cs;
                bq.a2 = 1.0f - alpha;
                bq.type = "LOWPASS";
                break;

            case HIGHPASS:
                bq.b0 = (1f + cs) / 2.0f;
                bq.b1 = -(1f + cs);
                bq.b2 = (1f + cs) / 2.0f;
                bq.a0 = 1 + alpha;
                bq.a1 = -2 * cs;
                bq.a2 = 1 - alpha;
                bq.type = "HIGHPASS";
                break;

            case BANDPASS:
                bq.b0 = alpha;
                bq.b1 = 0;
                bq.b2 = -alpha;
                bq.a0 = 1 + alpha;
                bq.a1 = -2 * cs;
                bq.a2 = 1 - alpha;
                bq.type = "BANDPASS";
                break;

            case NOTCH:
                bq.b0 = 1;
                bq.b1 = -2 * cs;
                bq.b2 = 1;
                bq.a0 = 1 + alpha;
                bq.a1 = -2 * cs;
                bq.a2 = 1 - alpha;
                bq.type = "NOTCH";
                break;

            case PEAK:
                bq.b0 = 1 + (alpha * A);
                bq.b1 = -2 * cs;
                bq.b2 = 1 - (alpha * A);
                bq.a0 = 1 + (alpha / A);
                bq.a1 = -2 * cs;
                bq.a2 = 1 - (alpha / A);
                bq.type = "PEAK";
                break;

            case LOWSHELF:
                bq.b0 = A * ((A + 1) - (A - 1) * cs + beta * sn);
                bq.b1 = 2 * A * ((A - 1) - (A + 1) * cs);
                bq.b2 = A * ((A + 1) - (A - 1) * cs - beta * sn);
                bq.a0 = (A + 1) + (A - 1) * cs + beta * sn;
                bq.a1 = -2 * ((A - 1) + (A + 1) * cs);
                bq.a2 = (A + 1) + (A - 1) * cs - beta * sn;
                bq.type = "LOWSHELF";
                break;

            case HIGHSHELF:
                bq.b0 = A * ((A + 1) + (A - 1) * cs + beta * sn);
                bq.b1 = -2 * A * ((A - 1) + (A + 1) * cs);
                bq.b2 = A * ((A + 1) + (A - 1) * cs - beta * sn);
                bq.a0 = (A + 1) - (A - 1) * cs + beta * sn;
                bq.a1 = 2 * ((A - 1) - (A + 1) * cs);
                bq.a2 = (A + 1) - (A - 1) * cs - beta * sn;
                bq.type = "HIGHSHELF";
                break;

            default:
                break;
        }
    }
}
