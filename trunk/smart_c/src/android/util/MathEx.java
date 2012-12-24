package android.util;

import java.util.Arrays;
import java.util.List;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * @author djrain
 * 
 */
public class MathEx {
	private static float[] src = new float[2];
	private static float[] dst = new float[2];
	private static Matrix matrix = new Matrix();

	public static float atan3(float sx, float sy, float ex, float ey, float cx, float cy) {
		matrix.setRotate((float) -Math.toDegrees(Math.atan2(sy - cy, sx - cx)), cx, cy);
		src[0] = ex;
		src[1] = ey;
		matrix.mapPoints(dst, src);
		double offestDegrees = Math.toDegrees(Math.atan2(dst[1] - cy, dst[0] - cx));
		return (float) offestDegrees;
	}

	public static float atan3(PointF s, PointF e, PointF c) {
		return atan3(s.x, s.y, e.x, e.y, c.x, c.y);
	}

	public static float atan2(float sx, float sy, float cx, float cy) {
		return (float) Math.toDegrees(Math.atan2(sy - cy, sx - cx));
	}

	public static float atan2(PointF s, PointF c) {
		return atan2(s.x, s.y, c.x, c.y);
	}

	public static float atan3sum(List<PointF> pts, PointF c) {
		float offestDegreesSum = 0;
		for (int i = 1; i < pts.size(); i++)
			offestDegreesSum += atan3(pts.get(i - 1), pts.get(i), c);
		return offestDegreesSum;
	}

	public static float atan3sum(PointF[] pts, PointF c) {
		return atan3sum(Arrays.asList(pts), c);
	}

	public static boolean get3PointCircle(PointF[] p, PointF c) {

		float dy1, dy2, d, d2, yi;
		PointF p1 = new PointF((p[0].x + p[1].x) / 2, (p[0].y + p[1].y) / 2);
		PointF p2 = new PointF((p[0].x + p[2].x) / 2, (p[0].y + p[2].y) / 2);

		dy1 = p[0].y - p[1].y;
		dy2 = p[0].y - p[2].y;

		if (dy1 != 0) {
			d = (p[1].x - p[0].x) / dy1;
			yi = p1.y - d * p1.x;
			if (dy2 != 0) {
				d2 = (p[2].x - p[0].x) / dy2;
				if (d != d2)
					c.x = (yi - (p2.y - d2 * p2.x)) / (d2 - d);
				else
					return false;
			} else if (p[2].x - p[0].x == 0)
				return false;
			else
				c.x = p2.x;
		} else if (dy2 != 0 && p[1].x - p[0].x != 0) {
			d = (p[2].x - p[0].x) / dy2;
			yi = p2.y - d * p2.x;
			c.x = p1.x;
		} else
			return false;
		c.y = d * c.x + yi;
		// float r = (float) Math.sqrt((p[0].x - c.x) * (p[0].x - c.x) + (p[0].y - c.y) * (p[0].y - c.y));
		return true;
	}

	public static long sum(long[] numbers) {
		long sum = 0;
		for (long number : numbers) {
			sum = sum + number;
		}
		return sum;
	}
}
