package android.common;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author djrain
 * 
 */
public class BaseCommand {

	public static void setIntent(final View view, final Intent intent) {
		if (view == null)
			throw new IllegalArgumentException("뭐야이건");

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view.getContext().startActivity(intent);
			};
		});
	}

	public static void setIntent(View view, final Class<?> cls) {
		setIntent(view, new Intent(view.getContext(), cls));
	}

	public static Dialog getDialog(Context mContext, Object title, Object icon, Object message, View view//
			, Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
			, Object neutralButtonText, DialogInterface.OnClickListener neutralListener //
			, Object negativeButtonText, DialogInterface.OnClickListener negativeListener//
	) {

		final Builder builder = new AlertDialog.Builder(mContext);
		if (title != null)
			builder.setTitle(title instanceof String ? (String) title : mContext.getResources().getString((Integer) title));
		if (message != null)
			builder.setMessage(message instanceof String ? (String) message : mContext.getResources().getString((Integer) message));
		if (icon != null)
			builder.setIcon(icon instanceof Drawable ? (Drawable) icon : mContext.getResources().getDrawable((Integer) icon));
		if (view != null)
			builder.setView(view);
		if (positiveButtonText != null)
			builder.setPositiveButton(positiveButtonText instanceof String ? (String) positiveButtonText : mContext.getResources().getString((Integer) positiveButtonText), positiveListener);
		if (negativeButtonText != null)
			builder.setNegativeButton(negativeButtonText instanceof String ? (String) negativeButtonText : mContext.getResources().getString((Integer) negativeButtonText), negativeListener);
		if (neutralButtonText != null)
			builder.setNeutralButton(neutralButtonText instanceof String ? (String) neutralButtonText : mContext.getResources().getString((Integer) neutralButtonText), neutralListener);

		final AlertDialog dlg = builder.create();
		return dlg;
	}

	public static Dialog getDialog(Context mContext, Object message//
			, Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
	) {
		return getDialog(mContext, null, null, message, null, positiveButtonText, positiveListener, null, null, null, null);

	}
	public static Dialog getDialog(Context mContext, Object message//
			, Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
			, Object negativeButtonText, DialogInterface.OnClickListener negativeListener//
	) {
		return getDialog(mContext, null, null, message, null, positiveButtonText, positiveListener, null, null, negativeButtonText, negativeListener);
	}

	public static void keepScreenOn(Window window) {
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public static void keepScreenOff(Window window) {
		window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

}