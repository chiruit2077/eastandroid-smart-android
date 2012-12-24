/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Various utilities shared amongst the Launcher's classes.
 */
public final class Utilities {

    @SuppressWarnings("unused")
    private static final String TAG = "Launcher.Utilities";

    private static final Rect sOldBounds = new Rect();
    private static final Canvas sCanvas = new Canvas();

    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG));
    }

    public static void dip(Context context, RectF rc) {
        Matrix matrix = new Matrix();
        matrix.setScale(2f / 3f, 2f / 3f);
        float density = context.getResources().getDisplayMetrics().density;
        matrix.setScale(density, density);

        matrix.mapRect(rc);
    }

    public static void dip(Context context, float[] vecs) {
        Matrix matrix = new Matrix();
        matrix.setScale(2f / 3f, 2f / 3f);
        float density = context.getResources().getDisplayMetrics().density;
        matrix.setScale(density, density);

        matrix.mapVectors(vecs);
    }

    /**
     * Returns a bitmap suitable for the all apps view. The bitmap will be a power of two sized ARGB_8888 bitmap that can be used as a gl texture.
     */
    public static Bitmap Drawable2Bitmap(Context context, Drawable drawable) {
        synchronized (sCanvas) { // we share the statics :-(

            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0.2f);

            if (drawable instanceof BitmapDrawable) {
                // Ensure the bitmap has a density.
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
                    bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
                }
            }

            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();

            final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            final Canvas canvas = sCanvas;
            canvas.setBitmap(bitmap);

            sOldBounds.set(drawable.getBounds());
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            drawable.setBounds(sOldBounds);

            return bitmap;
        }
    }

    public static boolean UrlToBitmap(String url, Bitmap photo) {
        boolean result = false;
        if (url == null || url.length() < 1) {
            try {
                URL _url = new URL(url);
                URLConnection urlc = _url.openConnection();

                urlc.setConnectTimeout(6000);

                urlc.setUseCaches(false);
                if (urlc.getInputStream() != null) {
                    photo = BitmapFactory.decodeStream(urlc.getInputStream());
                    result = true;
                } else {
                    photo = null;
                }

                if (urlc.getInputStream() != null) {
                    urlc.getInputStream().close();
                    urlc = null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                photo = null;
            } catch (IOException e) {
                e.printStackTrace();
                photo = null;
            } catch (Exception e) {
                e.printStackTrace();
                photo = null;
            }
        }
        return result;
    }

}
