/*
 * Copyright (C) 2007 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package android.view;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * An animation that rotates the view on the Y axis between two specified
 * angles. This animation also adds a translation on the Z axis (depth) to
 * improve the effect.
 */
public class Rotate3dAnimation extends Animation {
	private final float mFromDegrees;
	private final float mToDegrees;
	private final float mCenterX;
	private final float mCenterY;
	private final float mDepthZ;

	private final float mScale;

	private Camera mCamera = new Camera();

	// 360, 270, 0, 400, 0.0f, true
	public Rotate3dAnimation(float fromDegrees, float toDegrees, float centerX, float centerY, float depthZ) {
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mCenterX = centerX;
		mCenterY = centerY;
		mDepthZ = depthZ;

		final Camera camera = new Camera();
		camera.translate(0.0f, 0.0f, mDepthZ);
		Matrix matrix = new Matrix();
		camera.getMatrix(matrix);
		float[] values = new float[9];
		matrix.getValues(values);
		mScale = values[4];
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = mFromDegrees;
		float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

		final float centerX = mCenterX;
		final float centerY = mCenterY;

		final Camera camera = mCamera;
		final Matrix matrix = t.getMatrix();
		camera.save();

		camera.translate(0.0f, 0.0f, mDepthZ);
		camera.rotateY(degrees);
		camera.getMatrix(matrix);
		matrix.postScale(1 / mScale, 1 / mScale);

		camera.restore();

		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}
