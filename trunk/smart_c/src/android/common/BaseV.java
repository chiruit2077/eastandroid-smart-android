package android.common;

import android.content.Context;

/**
 * @author djrain
 * 
 */
public class BaseV {
	public static float DENSITY = 1f;

	public static void INIT(Context applicationContext) {
		BaseV.DENSITY = applicationContext.getResources().getDisplayMetrics().density;
	}

	public static float px(float dp) {
		return dp * BaseV.DENSITY;
	}

}
