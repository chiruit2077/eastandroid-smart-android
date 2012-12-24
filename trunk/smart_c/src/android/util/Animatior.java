package android.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * @author djrain
 * 
 */
public class Animatior implements Callback {

	private Handler mHandler;
	private Map<String, Holder> mHolders = new HashMap<String, Holder>();
	public static class Holder {
		public String name;
		public float from;
		public float to;
		public float current;
		public Holder set(String name, float from, float to) {
			this.name = name;
			this.from = from;
			this.to = to;
			return this;
		}
		public void reset() {
			this.from = 0f;
			this.to = 0f;
			this.current = 0f;

		}
		public void applyTransformation(float interpolatedTime) {
			current = from + ((to - from) * interpolatedTime);
		}
	}

	private static final int EVENT_ID_UPDATE = 100;
	private static final long FRAME_DELAYED = 1000 / 30;

	public static final int INFINITE = -1;
	public static final int RESTART = 1;
	public static final int REVERSE = 2;

	boolean mEnded = false;
	boolean mStarted = false;
	boolean mCycleFlip = false;

	boolean mFillBefore = true;
	boolean mFillAfter = false;
	boolean mFillEnabled = false;

	long mStartTime = -1;
	long mStartOffset;
	long mDuration;
	int mRepeatCount = 0;
	int mRepeated = 0;
	int mRepeatMode = RESTART;

	private Interpolator mInterpolator = new LinearInterpolator();

	private OnAnimatorListener mListener;

	private boolean mMore = true;

	// constructor
	public Animatior() {
		mHandler = new Handler(this);
		ensureInterpolator();
	}

	public Animatior set(Holder holder) {
		mHolders.put(holder.name, holder);
		return this;
	}
	public float getHolderCurrnet(String key) {
		return mHolders.get(key).current;
	}

	public Holder getHolder(String key) {
		return mHolders.get(key);
	}
	public Map<String, Holder> getHolders() {
		return mHolders;
	}

	public void setCycleFlip(boolean cycleFlip) {
		mCycleFlip = cycleFlip;
	}

	public static interface OnAnimatorListener {
		void onAnimationStart(Animatior animator);

		void onAnimationUpdate(Animatior animator);

		void onAnimationEnd(Animatior animator);

		void onAnimationRepeat(Animatior animatior);
	}

	// func
	public void reset() {
		mHandler.removeMessages(EVENT_ID_UPDATE);
		mCycleFlip = false;
		mRepeated = 0;
		mMore = true;

		mStartTime = INFINITE;
		mStarted = false;
		mEnded = false;
		final Set<String> keys = mHolders.keySet();
		for (String key : keys)
			mHolders.get(key).reset();

	}
	// 종료로 이동 
	public void cancel() {
		mHandler.removeMessages(EVENT_ID_UPDATE);
		if (mStarted && !mEnded) {
			fireAnimationEnd();
			mEnded = true;
		}
		// Make sure we move the animation to the end
		mStartTime = Long.MIN_VALUE;
		mMore = false;
	}
	public void pause() {
		mHandler.removeMessages(EVENT_ID_UPDATE);
	}
	public void resume() {
		mHandler.sendEmptyMessage(EVENT_ID_UPDATE);
	}

	public void start() {
		mStartTime = INFINITE;
		mStarted = false;
		mEnded = false;
		mHandler.removeMessages(EVENT_ID_UPDATE);
		getTransformation(AnimationUtils.currentAnimationTimeMillis());
	}

	// getter setter

	public void setInterpolator(Context context, int resID) {
		setInterpolator(AnimationUtils.loadInterpolator(context, resID));
	}

	public void setInterpolator(Interpolator i) {
		mInterpolator = i;
	}

	public void setStartOffset(long startOffset) {
		mStartOffset = startOffset;
	}

	public void setDuration(long durationMillis) {
		if (durationMillis < 0) {
			throw new IllegalArgumentException("Animation duration cannot be negative");
		}
		mDuration = durationMillis;
	}
	public void setStartTime(long startTimeMillis) {
		mStartTime = startTimeMillis;
		mStarted = mEnded = false;
		mCycleFlip = false;
		mRepeated = 0;
		mMore = true;
	}

	public void setRepeatMode(int repeatMode) {
		mRepeatMode = repeatMode;
	}

	public void setRepeatCount(int repeatCount) {
		if (repeatCount < 0) {
			repeatCount = INFINITE;
		}
		mRepeatCount = repeatCount;
	}

	public boolean isFillEnabled() {
		return mFillEnabled;
	}

	public void setFillEnabled(boolean fillEnabled) {
		mFillEnabled = fillEnabled;
	}

	public void setFillBefore(boolean fillBefore) {
		mFillBefore = fillBefore;
	}

	public void setFillAfter(boolean fillAfter) {
		mFillAfter = fillAfter;
	}

	public Interpolator getInterpolator() {
		return mInterpolator;
	}

	public long getStartTime() {
		return mStartTime;
	}

	public long getDuration() {
		return mDuration;
	}

	public long getStartOffset() {
		return mStartOffset;
	}

	public int getRepeatMode() {
		return mRepeatMode;
	}

	public int getRepeatCount() {
		return mRepeatCount;
	}

	public boolean getFillBefore() {
		return mFillBefore;
	}

	public boolean getFillAfter() {
		return mFillAfter;
	}

	public boolean hasStarted() {
		return mStarted;
	}

	public boolean hasEnded() {
		return mEnded;
	}

	// Listener
	public void setOnAnimatorListener(OnAnimatorListener listener) {
		mListener = listener;
	}

	protected void ensureInterpolator() {
		if (mInterpolator == null) {
			mInterpolator = new AccelerateDecelerateInterpolator();
		}
	}

	public long computeDurationHint() {
		return (getStartOffset() + getDuration()) * (getRepeatCount() + 1);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////

	// protected

	private void applyTransformation(float interpolatedTime) {
		final Set<String> keys = mHolders.keySet();
		for (String key : keys)
			mHolders.get(key).applyTransformation(interpolatedTime);
	}

	public boolean getTransformation(long currentTime) {
		if (mStartTime == -1) {
			mStartTime = currentTime;
		}

		final long startOffset = getStartOffset();
		final long duration = mDuration;
		float normalizedTime;
		if (duration != 0) {
			normalizedTime = ((float) (currentTime - (mStartTime + startOffset))) / (float) duration;
		} else {
			// time is a step-change with a zero duration
			normalizedTime = currentTime < mStartTime ? 0.0f : 1.0f;
		}

		final boolean expired = normalizedTime >= 1.0f;
		mMore = !expired;

//		if (!mFillEnabled)
		normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);

		if ((normalizedTime >= 0.0f || mFillBefore) && (normalizedTime <= 1.0f || mFillAfter)) {
			if (!mStarted) {
				fireAnimationStart();
				mStarted = true;
			}

//			if (mFillEnabled)
			normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);

			if (mCycleFlip) {
				normalizedTime = 1.0f - normalizedTime;
			}

			final float interpolatedTime = mInterpolator.getInterpolation(normalizedTime);
			applyTransformation(interpolatedTime);
			fireAnimationUpdate();
		}

		if (expired) {
			if (mRepeatCount == mRepeated) {
				if (!mEnded) {
					mEnded = true;
					fireAnimationEnd();
				}
			} else {
				if (mRepeatCount > 0) {
					mRepeated++;
				}

				if (mRepeatMode == REVERSE) {
					mCycleFlip = !mCycleFlip;
				}

				mStartTime = -1;
				mMore = true;

				fireAnimationRepeat();
			}
		}

		if (mMore) {
			mHandler.sendEmptyMessageDelayed(EVENT_ID_UPDATE, FRAME_DELAYED);
		}

		return mMore;
	}

	private void fireAnimationUpdate() {
		if (mListener != null) {
			mListener.onAnimationUpdate(this);
		}
	}
	private void fireAnimationRepeat() {
		if (mListener != null) {
			mListener.onAnimationRepeat(this);
		}
	}

	private void fireAnimationStart() {
		if (mListener != null) {
			mListener.onAnimationStart(this);
		}
	}

	private void fireAnimationEnd() {
		if (mListener != null) {
			mListener.onAnimationEnd(this);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == EVENT_ID_UPDATE) {
			getTransformation(AnimationUtils.currentAnimationTimeMillis());
		}
		return true;
	}

}
