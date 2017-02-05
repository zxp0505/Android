package com.sample.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class PlaySound {

    private final static String TAG = "PlaySound";

    Context mContext;
    int minBufferSize;

    IR mIr;

    volatile float azimuth = -1f;
    volatile float elevation = -1f;

    public void setPos(float azimuth, float elevation) {
        //Log.d(TAG, "setPos, azimuth: " + azimuth + ", elevation:" + elevation);
        this.azimuth = azimuth;
        this.elevation = elevation;
    }

    public PlaySound(Context context) {
        mContext = context;
        minBufferSize = AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        mIr = new IR();
        try {
            InputStream is = mContext.getAssets().open("kemar_L.bin");

            byte[] fileBytes = new byte[is.available()];
            is.read(fileBytes);
            is.close();

            mIr.loadHrir(fileBytes);
        } catch (IOException E) {
            Log.e(TAG, "loadHrir exception: " + E);
        }
    }

    private int mSampelRate;
    private int mChannels;
    private MediaCodec mMedaiCodec;

    public void play() {
        AssetFileDescriptor assetFileDescriptor;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            assetFileDescriptor = mContext.getAssets().openFd("core3.mp3");
            mediaExtractor.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());

            int tracks = mediaExtractor.getTrackCount();
            for (int i = 0; i < tracks; i++) {
                MediaFormat format = mediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio/")) {
                    int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    int channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                    MediaCodec mediaCodec = MediaCodec.createDecoderByType(mime);
                    mediaCodec.configure(format, null, null, 0);

                    mSampelRate = sampleRate;
                    mChannels = channels;
                    mMedaiCodec = mediaCodec;
                }
            }

            AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, mSampelRate,
                    AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize, AudioTrack.MODE_STREAM);
            at.play();

            short[] lastOut1 = null;
            short[] lastOut2 = null;

            ByteBuffer totalBuff = ByteBuffer.allocate(5 * 1024 * 1024);

            mMedaiCodec.start();
            mediaExtractor.selectTrack(0);

            ByteBuffer[] inputBuffer = mMedaiCodec.getInputBuffers();

            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            info.flags = 0;
            boolean endOs = false;

            while ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) == 0) {

                if (!endOs) {
                    int inIndex = mMedaiCodec.dequeueInputBuffer(-1);
                    if (inIndex >= 0) {
                        ByteBuffer dec_inBuffer = inputBuffer[inIndex];
                        int sampleSize = mediaExtractor.readSampleData(dec_inBuffer, 0);
                        if (sampleSize < 0) {
                            mMedaiCodec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            endOs = true;
                        } else {
                            mMedaiCodec.queueInputBuffer(inIndex, 0, sampleSize, mediaExtractor.getSampleTime(), 0);
                            mediaExtractor.advance();
                        }
                    }
                }


                int outIndex = mMedaiCodec.dequeueOutputBuffer(info, -1);
                if (outIndex >= 0) {
                    ByteBuffer[] outputBuffers = mMedaiCodec.getOutputBuffers();
                    ByteBuffer dec_outBuffer = outputBuffers[outIndex];
                    if (info.size > 0) {
                        byte[] outb = new byte[info.size];
                        dec_outBuffer.get(outb);
                        totalBuff.put(outb);

                        float temAzi = azimuth;
                        float temElev = elevation;
                        IR.IR_Item[] items = mIr.interpolateHRIR(temAzi, temElev);
                        Log.d(TAG, "temAzi:" + temAzi + ", temElev:" + temElev);

                        short[] shorts = new short[outb.length / 2];
                        ByteBuffer.wrap(outb).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

                        //left
                        short[] shortout1 = new short[shorts.length + items[0].size - 1];
                        zeroByte(shortout1);
                        if (lastOut1 != null) {
                            System.arraycopy(lastOut1, lastOut1.length - (items[0].size - 1), shortout1, 0, items[0].size - 1);
                        }

                        Convolve.Convolve(shorts, shorts.length, items[0].data, items[0].size, shortout1);
                        lastOut1 = shortout1;

                        //right
                        short[] shortout2 = new short[shorts.length + items[1].size - 1];
                        zeroByte(shortout2);
                        if (lastOut2 != null) {
                            System.arraycopy(lastOut2, lastOut2.length - (items[1].size - 1), shortout2, 0, items[1].size - 1);
                        }

                        Convolve.Convolve(shorts, shorts.length, items[1].data, items[1].size, shortout2);
                        lastOut2 = shortout2;

                        //out
                        short[] out = new short[shorts.length * 2];
                        for (int i = 0; i < shorts.length; i += 1) {
                            out[i * 2] = shortout1[i];
                            out[i * 2 + 1] = shortout2[i];
                        }
                        at.write(out, 0, out.length);

                        /**
                         float temAzi  = azimuth;
                         float temElev = elevation;
                         IR.IR_Item [] items = mIr.interpolateHRIR(temAzi, temElev);

                         if (items == null){
                         Log.w(TAG, "get ir items failed, azimuth:" + temAzi + ",elevation:" + temElev);

                         byte []out = new byte[info.size * 2];
                         for (int i=0; i<info.size; i+=2){

                         out[2*i]   = outb[i];
                         out[2*i+1] = outb[i+1];

                         out[i*2+2] = outb[i];
                         out[i*2+3] = outb[i+1];
                         }
                         at.write(out, 0, info.size  * 2);
                         }
                         else {
                         short[] shorts = new short[outb.length / 2];
                         ByteBuffer.wrap(outb).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

                         //ok
                         short[] shortout1 = new short[outb.length / 2 + 199];
                         Convolve.Convolve(shorts, shorts.length, items[0].data, items[0].size, shortout1);

                         short[] shortout2 = new short[outb.length / 2 + 199];
                         Convolve.Convolve(shorts, shorts.length, items[1].data, items[1].size, shortout2);

                         //out
                         short[] out = new short[shortout1.length + shortout2.length];
                         for (int i = 0; i < shortout1.length; i += 1) {
                         out[i * 2]     = shortout1[i];
                         out[i * 2 + 1] = shortout2[i];
                         }
                         at.write(out, 199, out.length -199 * 2);
                         }
                         */
                    }

                    mMedaiCodec.releaseOutputBuffer(outIndex, false);
                }
            }

            // col(at, totalBuff);

            mMedaiCodec.release();
            mediaExtractor.release();

            at.stop();
            at.release();

        } catch (IOException e) {
            Log.e(TAG, "exception: " + e);
        }
    }

    private void col(AudioTrack at, ByteBuffer totalBuff) {

        totalBuff.position(0);
        int totalSize = totalBuff.remaining();
        byte[] outb = new byte[totalSize / 12];
        totalBuff.get(outb);

        float temAzi = azimuth;
        float temElev = elevation;
        IR.IR_Item[] items = mIr.interpolateHRIR(temAzi, temElev);

        // to shorts
        short[] shorts = new short[outb.length / 2];
        ByteBuffer.wrap(outb).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

        //ok
        short[] shortout1 = new short[shorts.length + items[0].size - 1];
        zeroByte(shortout1);

        Convolve.Convolve(shorts, shorts.length, items[0].data, items[0].size, shortout1);

        short[] shortout2 = new short[shorts.length + items[1].size - 1];
        zeroByte(shortout2);

        Convolve.Convolve(shorts, shorts.length, items[1].data, items[1].size, shortout2);

        //out
        short[] out = new short[shortout1.length + shortout2.length];

        for (int i = 0; i < shortout1.length; i += 1) {
            out[i * 2] = shortout1[i];
            out[i * 2 + 1] = shortout2[i];
        }
        at.write(out, 0, out.length);
    }

    void zeroByte(short[] out) {

        for (int i = 0; i < out.length; ++i) {
            out[i] = 0;
        }
    }
}
