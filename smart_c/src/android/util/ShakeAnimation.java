package android.util;

import java.util.Random;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;

/**
 * @author djrain
 * 
 */
public class ShakeAnimation {

	private static Random rand = new Random(System.currentTimeMillis());

	public static void startShakeAnimation(View v) {

		float fromDegrees = 0;
		float toDegrees = 1;
		int pivotXType = Animation.RELATIVE_TO_SELF;
		int pivotYType = Animation.RELATIVE_TO_SELF;
		float pivotXValue = .5f;
		float pivotYValue = .5f;

//		Log.l(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
		RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
		animation.setDuration(500 + rand.nextInt(100));
		animation.setRepeatMode(Animation.RESTART);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setFillBefore(true);
		animation.setInterpolator(new CycleInterpolator(1f));
		v.startAnimation(animation);

	}

	public static Animation getShakeAnimation() {
		float fromDegrees = 0;
		float toDegrees = 1;
		int pivotXType = Animation.RELATIVE_TO_SELF;
		int pivotYType = Animation.RELATIVE_TO_SELF;
		float pivotXValue = .5f;
		float pivotYValue = .5f;

//		Log.l(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
		RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
		animation.setDuration(500 + rand.nextInt(100));
		animation.setRepeatMode(Animation.RESTART);
		animation.setFillEnabled(false);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setInterpolator(new CycleInterpolator(1f));
		return animation;
	}
}
