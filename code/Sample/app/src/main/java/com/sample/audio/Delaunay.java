package com.sample.audio;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Delaunay {

    private final static String TAG = "Delaunay";

    private final static float EPSILON = 1.0f / 1048576.0f;

    private IR.Point[] supertriangle(List<IR.Point> vertices) {
        float xmin = Float.POSITIVE_INFINITY,
                ymin = Float.POSITIVE_INFINITY,
                xmax = Float.NEGATIVE_INFINITY,
                ymax = Float.NEGATIVE_INFINITY,
                dx, dy, dmax, xmid, ymid;

        for (int i = 0; i < vertices.size(); i++) {

            if (vertices.get(i).azimuth < xmin) {
                xmin = vertices.get(i).azimuth;
            }

            if (vertices.get(i).azimuth > xmax) {
                xmax = vertices.get(i).azimuth;
            }

            if (vertices.get(i).elevation < ymin) {
                ymin = vertices.get(i).elevation;
            }

            if (vertices.get(i).elevation > ymax) {
                ymax = vertices.get(i).elevation;
            }

            //if (vertices[i][0] < xmin) xmin = vertices[i][0];
            //if (vertices[i][0] > xmax) xmax = vertices[i][0];
            //if (vertices[i][1] < ymin) ymin = vertices[i][1];
            //if (vertices[i][1] > ymax) ymax = vertices[i][1];
        }

        dx = xmax - xmin;
        dy = ymax - ymin;

        dmax = Math.max(dx, dy);

        xmid = xmin + dx * 0.5F;
        ymid = ymin + dy * 0.5F;

        IR.Point p1 = new IR.Point(xmid - 20 * dmax, ymid - dmax);
        IR.Point p2 = new IR.Point(xmid, ymid + 20 * dmax);
        IR.Point p3 = new IR.Point(xmid + 20 * dmax, ymid - dmax);

        Log.d(TAG, "p1:" + p1);
        Log.d(TAG, "p2:" + p2);
        Log.d(TAG, "p3:" + p3);

        return new IR.Point[]{p1, p2, p3};

//        return new float [] {}
//        [xmid - 20 * dmax, ymid - dmax],
//        [xmid, ymid + 20 * dmax],
//        [xmid + 20 * dmax, ymid - dmax]
//        ];
    }

    //外接圆
    public static class Circumcircle {
        public int i;
        public int j;
        public int k;
        public float x;
        public float y;
        public float r;

        @Override
        public String toString() {
            return "i: " + i + ", j: " + j + ", k: " + k + "x: " + x + ", y: " + y + "r: " + r;
        }
    }

    Circumcircle circumcircle(List<IR.Point> vertices, int i, int j, int k) {
        float x1 = vertices.get(i).azimuth,
                y1 = vertices.get(i).elevation,
                x2 = vertices.get(j).azimuth,
                y2 = vertices.get(j).elevation,

                x3 = vertices.get(k).azimuth,
                y3 = vertices.get(k).elevation,

                fabsy1y2 = Math.abs(y1 - y2),
                fabsy2y3 = Math.abs(y2 - y3),

                xc, yc, m1, m2, mx1, mx2, my1, my2, dx, dy;

		/* Check for coincident points */
        if (fabsy1y2 < EPSILON && fabsy2y3 < EPSILON) {
            //throw new Error("Eek! Coincident points!");
            Log.e(TAG, "Eek! Coincident points!");
            return null;
        }

        if (fabsy1y2 < EPSILON) {
            m2 = -((x3 - x2) / (y3 - y2));
            mx2 = (x2 + x3) / 2.0f;
            my2 = (y2 + y3) / 2.0f;
            xc = (x2 + x1) / 2.0f;
            yc = m2 * (xc - mx2) + my2;
        } else if (fabsy2y3 < EPSILON) {
            m1 = -((x2 - x1) / (y2 - y1));
            mx1 = (x1 + x2) / 2.0f;
            my1 = (y1 + y2) / 2.0f;
            xc = (x3 + x2) / 2.0f;
            yc = m1 * (xc - mx1) + my1;
        } else {
            m1 = -((x2 - x1) / (y2 - y1));
            m2 = -((x3 - x2) / (y3 - y2));
            mx1 = (x1 + x2) / 2.0f;
            mx2 = (x2 + x3) / 2.0f;
            my1 = (y1 + y2) / 2.0f;
            my2 = (y2 + y3) / 2.0f;
            xc = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
            yc = (fabsy1y2 > fabsy2y3) ?
                    m1 * (xc - mx1) + my1 :
                    m2 * (xc - mx2) + my2;
        }

        dx = x2 - xc;
        dy = y2 - yc;

        Circumcircle circumcircle = new Circumcircle();
        circumcircle.i = i;
        circumcircle.j = j;
        circumcircle.k = k;
        circumcircle.x = xc;
        circumcircle.y = yc;
        circumcircle.r = dx * dx + dy * dy;


        Log.d(TAG, "circumcircle:" + circumcircle);

        return circumcircle;
        //return new float [] { i,  j,  k,  xc,  yc, dx * dx + dy * dy };
    }


    boolean dedup(List<Integer> edges) {

        boolean found = false;

        int i, j, a, b, m, n;

        for (j = edges.size(); j >= 2; ) {
            b = edges.get(--j);
            a = edges.get(--j);

            for (i = j; i >= 2; ) {
                n = edges.get(--i);
                m = edges.get(--i);

                if ((a == m && b == n) || (a == n && b == m)) {
                    //edges.splice(j, 2);
                    edges.remove(j + 1);
                    edges.remove(j);

                    edges.remove(i + 1);
                    edges.remove(i);
                    //edges.splice(i, 2);

                    found = true;
                    break;
                }
            }

            if (found) {
                break;
            }
        }

        return found;
    }

    List<Integer> triangulate(List<IR.Point> points) {
        int n = points.size();
        if (n < 3) {
            return null;
        }

        final List<IR.Point> vertices = new ArrayList<>(points);

        //[0, n-1]
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }

        //sort 根据azimuth值大小降序排列
        //稳定排序，与JS的排序结果不完全一样，因为azimuth是分段的，每个azimuth有52个相同的点
        Arrays.sort(indices, new Comparator<Integer>() {
            public int compare(Integer i, Integer j) {
                //return o2.compareTo(o1);
                float ret = (vertices.get(j).azimuth - vertices.get(i).azimuth);
                if (ret < 0) return -1;
                if (ret == 0) return 0;
                return 1;
                //return vertices[j][0] - vertices[i][0];
            }
        });

        //
        IR.Point[] st = supertriangle(vertices);
        vertices.add(st[0]);
        vertices.add(st[1]);
        vertices.add(st[2]);

        List<Circumcircle> open = new ArrayList<>();
        open.add(circumcircle(vertices, n, n + 1, n + 2));

        List<Circumcircle> closed = new ArrayList<>();
        List<Integer> edges = new ArrayList<>();

        float dx, dy;
        int i, j;

        for (i = indices.length - 1; i >= 0; i--, edges.clear()) {
            int c = indices[i];

            /* For each open triangle, check to see if the current point is
                 * inside it's circumcircle. If it is, remove the triangle and add
				 * it's edges to an edge list. */

            for (j = open.size() - 1; j >= 0; j--) {

                /* If this point is to the right of this triangle's circumcircle,
					 * then this triangle should never get checked again. Remove it
					 * from the open list, add it to the closed list, and skip. */

                dx = vertices.get(c).azimuth - open.get(j).x;
                if (dx > 0f && dx * dx > open.get(j).r) {
                    closed.add(open.get(j));
                    open.remove(j);
                    continue;
                }

                /* If we're outside the circumcircle, skip this triangle. */
                dy = vertices.get(c).elevation - open.get(j).y;
                if (dx * dx + dy * dy - open.get(j).r > EPSILON) {
                    continue;
                }

                /* Remove the triangle and add it's edges to the edge list. */
                edges.add(open.get(j).i);
                edges.add(open.get(j).j);

                edges.add(open.get(j).j);
                edges.add(open.get(j).k);

                edges.add(open.get(j).k);
                edges.add(open.get(j).i);

                open.remove(j);
            }

            /* Remove any doubled edges. */
            while (true) {
                boolean ret = dedup(edges);
                if (!ret) break;
            }

            /* Add a new triangle for each edge. */
            for (j = edges.size(); j >= 2; ) {
                int b = edges.get(--j);
                int a = edges.get(--j);
                Circumcircle circumcircle = circumcircle(vertices, a, b, c);
                if (circumcircle != null) {
                    open.add(circumcircle);
                }
            }
        }

        /* Copy any remaining open triangles to the closed list, and then
			 * remove any triangles that share a vertex with the supertriangle,
			 * building a list of triplets that represent triangles. */
        for (i = open.size() - 1; i >= 0; i--) {
            closed.add(open.get(i));
        }
        open.clear();

        List<Integer> retValue = new ArrayList<>();

        for (i = closed.size() - 1; i >= 0; i--) {
            if (closed.get(i).i < n && closed.get(i).j < n && closed.get(i).k < n) {
                // open.add(closed[i].i, closed[i].j, closed[i].k);
                retValue.add(closed.get(i).i);
                retValue.add(closed.get(i).j);
                retValue.add(closed.get(i).k);
            }
        }

        /* Yay, we're done! */
        return retValue;
    }
}
