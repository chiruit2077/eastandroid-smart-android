package android.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.miscellaneous.Log;

/**
 * @author djrain
 * 
 */
public class AccelerometerSensorEx implements SensorEventListener {

	private static final float INFINITY = Float.NEGATIVE_INFINITY;
	private static final float X_SENSITIVITY = 20.0f * 0.5f;//감도의 최소값

	private float lx = INFINITY;
	private float ly = INFINITY;
	private float lz = INFINITY;
	private SensorEventListenerEx listener;
	private SensorManager mSensorManager;
	private Sensor mSccelerometerSensor;

	public static interface SensorEventListenerEx {
		public void onSensorChangedEx(float speed);
	}

	public void setOnSensorChangedEx(SensorEventListenerEx listener) {
		if (listener == null) {
			Log.l("해지");
			mSensorManager.unregisterListener(this, mSccelerometerSensor);
			this.listener = null;
		} else {
			Log.l("등록");
			mSensorManager.registerListener(this, mSccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
			this.listener = listener;
		}
	}

	public AccelerometerSensorEx(Context context) {
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			if (lx == INFINITY) {
				lx = event.values[0];
				ly = event.values[1];
				lz = event.values[2];
				return;
			}

			float G_Accelerometer = Math.abs(lx - event.values[0]) + Math.abs(ly - event.values[1]) + Math.abs(lz - event.values[2]);
			if (listener != null && G_Accelerometer > X_SENSITIVITY) {
				Log.l(G_Accelerometer, X_SENSITIVITY);
				listener.onSensorChangedEx(G_Accelerometer);
			}

			lx = event.values[0];
			ly = event.values[1];
			lz = event.values[2];

		}
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
