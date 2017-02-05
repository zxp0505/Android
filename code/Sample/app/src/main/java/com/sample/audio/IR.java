package com.sample.audio;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static com.sample.audio.IR.Direction.LEFT;
import static com.sample.audio.IR.Direction.RIGHT;

/**
 * https://codeandsound.wordpress.com/2015/04/08/implementing-binaural-hrtf-panner-node-with-web-audio-api/
 */
public class IR {

    private final static String TAG = "IR";

    public enum Direction {
        LEFT,
        RIGHT
    }

    public static class IR_Item {
        public Direction direction;
        public float azimuth;
        public float elevation;

        public float[] data;
        public int size;
    }

    public static class Point {
        public Point(float x, float y) {
            azimuth = x;
            elevation = y;
        }

        public float azimuth;
        public float elevation;

        @Override
        public String toString() {
            return "azimuth: " + azimuth + ", elevation: " + elevation;
        }
    }

    public List<IR_Item> mIrItems;
    public List<Point> mPoints;
    public List<Integer> mTriangles;

    public IR_Item[] getIrItem(float azimuth, float elevation) {
        final int count = mIrItems.size();
        for (int i = 0; i < count; i += 2) {
            IR_Item cur = mIrItems.get(i);
            if (Math.abs(cur.azimuth - azimuth) < 0.1f && Math.abs(cur.elevation - elevation) < 0.1f) {
                IR_Item[] ret = new IR_Item[2];
                ret[0] = cur;               //LEFT
                ret[1] = mIrItems.get(i + 1);//RIGHT
                return ret;
            }
        }
        return null;
    }

    IR_Item createIrItem(Direction direction, float azimuth, float elevation, byte[] hrir, int off, int size) {
        IR_Item item = new IR_Item();
        item.direction = direction;
        item.azimuth = azimuth;
        item.elevation = elevation;

        float[] floats1 = new float[size];
        ByteBuffer.wrap(hrir, off * 4, size * 4).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(floats1);

        item.size = size;
        item.data = floats1;
        return item;
    }

    public void loadHrir(byte[] buffer) {
        int azimuths[] = {-90, -80, -65, -55, -45, -40, -35, -30, -25, -20, -15, -10, -5, 0,
                5, 10, 15, 20, 25, 30, 35, 40, 45, 55, 65, 80, 90};

        List<Point> points = new ArrayList<>();
        List<IR_Item> irs = new ArrayList<>();

        final int hrirLength = 200;
        int k = 0;

        final int azimuthCount = azimuths.length;

        for (int i = 0; i < azimuthCount; ++i) {
            final int azi = azimuths[i];

            // -90 deg elevation
            IR_Item left = createIrItem(LEFT, azi, -90, buffer, k, hrirLength);
            k += hrirLength;

            IR_Item right = createIrItem(RIGHT, azi, -90, buffer, k, hrirLength);
            k += hrirLength;

            irs.add(left);
            irs.add(right);

            points.add(new Point(azi, -90f));

            // 50 elevations: -45 + 5.625 * (0:49)
            for (int j = 0; j < 50; ++j) {
                final float elv = -45 + 5.625f * j;

                left = createIrItem(LEFT, azi, elv, buffer, k, hrirLength);
                k += hrirLength;

                right = createIrItem(RIGHT, azi, elv, buffer, k, hrirLength);
                k += hrirLength;

                irs.add(left);
                irs.add(right);

                points.add(new Point(azi, elv));
            }

            // 270 deg elevation
            left = createIrItem(LEFT, azi, 270, buffer, k, hrirLength);
            k += hrirLength;

            right = createIrItem(RIGHT, azi, 270, buffer, k, hrirLength);
            k += hrirLength;

            irs.add(left);
            irs.add(right);

            points.add(new Point(azi, 270));
        }

        mIrItems = new ArrayList<>(irs);
        mPoints = new ArrayList<>(points);
        mTriangles = new Delaunay().triangulate(points);
    }

    IR_Item[] interpolateHRIR(float azm, float elv) {

        Log.d(TAG, "interpolateHRIR, azm:" + azm + ", elv:" + elv);

        List<Integer> triangles = mTriangles;
        List<Point> points = mPoints;

        int i = triangles.size() - 1;
        Point A, B, C;
        //X, T, invT, det, g1, g2, g3;
        while (true) {
            A = points.get(triangles.get(i));
            i--;
            B = points.get(triangles.get(i));
            i--;
            C = points.get(triangles.get(i));
            i--;

            float[] T = new float[]{A.azimuth - C.azimuth, A.elevation - C.elevation, B.azimuth - C.azimuth, B.elevation - C.elevation};
            float[] invT = new float[]{T[3], -T[1], -T[2], T[0]};

            float det = 1 / (T[0] * T[3] - T[1] * T[2]);

            for (int j = 0; j < invT.length; ++j)
                invT[j] *= det;

            float[] X = new float[]{azm - C.azimuth, elv - C.elevation};

            float g1 = invT[0] * X[0] + invT[2] * X[1];
            float g2 = invT[1] * X[0] + invT[3] * X[1];
            float g3 = 1 - g1 - g2;

            if (g1 >= 0 && g2 >= 0 && g3 >= 0) {

                float[] hrirL = new float[200];
                float[] hrirR = new float[200];

                Log.d(TAG, "g1:" + g1 + ", g2:" + g2 + ", g3:" + g3);

                Log.d(TAG, "A:" + A);
                Log.d(TAG, "B:" + B);
                Log.d(TAG, "C:" + C);

                IR_Item[] ir_items1 = getIrItem(A.azimuth, A.elevation);
                IR_Item[] ir_items2 = getIrItem(B.azimuth, B.elevation);
                IR_Item[] ir_items3 = getIrItem(C.azimuth, C.elevation);

                for (int j = 0; j < 200; j++) {
                    /*
                    hrirL[i] = g1 * hrir['L'][A[0]][A[1]][i] +
                            g2 * hrir['L'][B[0]][B[1]][i] +
                            g3 * hrir['L'][C[0]][C[1]][i];
                    */

                    hrirL[j] = g1 * ir_items1[0].data[j] +
                            g2 * ir_items2[0].data[j] +
                            g3 * ir_items3[0].data[j];

                    /*
                    hrirR[i] = g1 * hrir['R'][A[0]][A[1]][i] +
                            g2 * hrir['R'][B[0]][B[1]][i] +
                            g3 * hrir['R'][C[0]][C[1]][i];
                            */

                    hrirR[j] = g1 * ir_items1[1].data[j] +
                            g2 * ir_items2[1].data[j] +
                            g3 * ir_items3[1].data[j];
                }

                IR_Item[] retValue = new IR_Item[2];
                retValue[0] = new IR_Item();
                retValue[1] = new IR_Item();

                retValue[0].data = hrirL;
                retValue[0].size = 200;
                retValue[0].direction = LEFT;

                retValue[1].data = hrirR;
                retValue[1].size = 200;
                retValue[1].direction = RIGHT;

                retValue[0].azimuth = azm;
                retValue[0].elevation = elv;

                retValue[1].azimuth = azm;
                retValue[1].elevation = elv;

                return retValue;
            } else if (i < 2) {
                break;
            }
        }

        return null;
    }

    /*
        Params:
        x1 - axis that passes through the ears from left to right
        x2 - axis that passes "between the eyes" and points ahead
        x3 - axis that points "up"

        Returns point in interaural coordinates.
    */
    public static float[] cartesianToInteraural(float x1, float x2, float x3) {
        float r = (float) Math.sqrt(x1 * x1 + x2 * x2 + x3 * x3);
        float azm = rad2deg((float) Math.asin(x1 / r));
        float elv = rad2deg((float) Math.atan2(x3, x2));
        if (x2 < 0 && x3 < 0)
            elv += 360;
        return new float[]{r, azm, elv};
    }

    public static float[] interauralToCartesian(float r, float azm, float elv) {
        azm = deg2rad(azm);
        elv = deg2rad(elv);
        float x1 = (float) (r * Math.sin(azm));
        float x2 = (float) (r * Math.cos(azm) * Math.cos(elv));
        float x3 = (float) (r * Math.cos(azm) * Math.sin(elv));
        return new float[]{x1, x2, x3};
    }

    public static float deg2rad(float deg) {
        return (float) (deg * Math.PI / 180f);
    }

    public static float rad2deg(float rad) {
        return (float) (rad * 180 / Math.PI);
    }
}
