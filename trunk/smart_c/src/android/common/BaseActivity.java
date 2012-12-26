package android.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.miscellaneous.E;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * @author djrain
 * 
 */
@SuppressWarnings("deprecation")
public class BaseActivity extends ActivityGroup {

	protected Context mContext;

	protected TimePickerDialog timePickerDialog;
	protected DatePickerDialog datePickerDialog;
	protected Dialog mDialogProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = BaseActivity.this;
		if (E.SCREEN)
			keepScreenOn();
	}
	@Override
	protected void onStop() {
		super.onStop();
		cancelProgress();
		clearDlgs();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		ArrayList<Integer> value = new ArrayList<Integer>();
		final ArrayList<View> views = mManagedTagView;
		for (View view : views) {
			String view_name = getResources().getResourceName(view.getId());
			final Object obj = view.getTag();
			if (obj instanceof Bundle) {
				Bundle bundle = (Bundle) obj;
				Set<String> keyset = bundle.keySet();
				for (String key : keyset) {
					outState.putString(view_name + key, bundle.getString(key));
				}
				value.add(view.getId());
			}
		}
		outState.putIntegerArrayList("managedViewIds", value);
		mManagedTagView.clear();
		// Log.l(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState == null)
			return;
		ArrayList<Integer> resids = savedInstanceState.getIntegerArrayList("managedViewIds");
		Set<String> keyset = savedInstanceState.keySet();

		for (Integer resid : resids) {
			String view_name = getResources().getResourceName(resid);
			for (String key : keyset) {
				Bundle bundle = null;
				if (key.startsWith(view_name)) {
					String value = savedInstanceState.getString(key);
					String bundleKey = key.substring(view_name.length());
					bundle = new Bundle();
					bundle.putString(bundleKey, value);
				}
				findViewById(resid).setTag(bundle);
			}
			mManagedTagView.add(findViewById(resid));
		}
	}
	public void keepScreenOn() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public void keepScreenOff() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	protected ArrayList<Dialog> mDialogs = new ArrayList<Dialog>();

	protected void clearDlgs() {
		final ArrayList<Dialog> dlgs = mDialogs;

		while (dlgs.size() > 0) {
			Dialog alertDialog = dlgs.remove(0);
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
				alertDialog = null;
			}
		}
	}

	protected void cancelProgress() {
		if (mDialogProgress != null) {
			mDialogProgress.cancel();
		}
	}

	public Dialog showProgress() {
		if (isFinishing())
			return null;
		mDialogProgress = ProgressDialog.show(mContext, "", "잠시만 기다려주세요.", true, true);
		return mDialogProgress;
	}

	public Dialog showDatePicker(OnDateSetListener onDateSetListener, int year, int monthOfYear, int dayOfMonth) {
		if (isFinishing())
			return null;
		if (datePickerDialog == null)
			datePickerDialog = new DatePickerDialog(mContext, onDateSetListener, year, monthOfYear, dayOfMonth);
		datePickerDialog.updateDate(year, monthOfYear, dayOfMonth);
		datePickerDialog.show();
		mDialogs.add(datePickerDialog);
		return datePickerDialog;
	}

	protected class OnDateClickListener implements OnClickListener, OnDateSetListener {

		int mResid = 0;
		long mMilliseconds;

		public OnDateClickListener(int resid, long milliseconds) {
			mResid = resid;
			mMilliseconds = milliseconds;
		}

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			Calendar cal = Calendar.getInstance();
			cal.set(year, monthOfYear, dayOfMonth);
			setText(mResid, new SimpleDateFormat("yyyy.MM.dd").format(new Date(cal.getTimeInMillis())));
		}

		@Override
		public void onClick(View v) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(mMilliseconds);
			showDatePicker(this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		}
	};

	public Dialog showTimePicker(OnTimeSetListener onTimeSetListener, int hourOfDay, int minutOfHour) {
		if (isFinishing())
			return null;
		if (timePickerDialog == null)
			timePickerDialog = new TimePickerDialog(mContext, onTimeSetListener, hourOfDay, minutOfHour, false);
		timePickerDialog.updateTime(hourOfDay, minutOfHour);
		timePickerDialog.show();
		mDialogs.add(timePickerDialog);
		return datePickerDialog;
	}

	public Dialog showDialogFinish(String message, String positiveButtonText) {
		if (isFinishing())
			return null;

		DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		};

		Dialog dlg = getDialog(null, null, message, null, positiveButtonText, positiveListener, null, null, null, null);

		dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});

		dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				finish();
			}
		});

		dlg.show();
		return dlg;
	}
	public Dialog showDialog(Object message, Object positiveButtonText, DialogInterface.OnClickListener positiveListener) {
		return showDialog(null, null, message, null, positiveButtonText, positiveListener, null, null, null, null);
	}
	public Dialog showDialog(Object message,//
			Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
			, Object negativeButtonText, DialogInterface.OnClickListener negativeListener) {
		return showDialog(null, null, message, null, positiveButtonText, positiveListener, null, null, negativeButtonText, negativeListener);
	}
	public Dialog showDialog(Object message,//
			Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
			, Object neutralButtonText, DialogInterface.OnClickListener neutralListener//
			, Object negativeButtonText, DialogInterface.OnClickListener negativeListener//
	) {
		return showDialog(null, null, message, null, positiveButtonText, positiveListener, neutralButtonText, neutralListener, negativeButtonText, negativeListener);
	}
	public Dialog showDialog(Object title, Object icon, Object message, View view//
			, Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
			, Object neutralButtonText, DialogInterface.OnClickListener neutralListener //
			, Object negativeButtonText, DialogInterface.OnClickListener negativeListener//
	) {
		if (isFinishing())
			return null;
		final Dialog dlg = getDialog(title, icon, message, view, positiveButtonText, positiveListener, neutralButtonText, neutralListener, negativeButtonText, negativeListener);
		dlg.show();
		return dlg;
	}

	public Dialog getDialog(Object title, Object icon, Object message, View view//
			, Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
			, Object neutralButtonText, DialogInterface.OnClickListener neutralListener //
			, Object negativeButtonText, DialogInterface.OnClickListener negativeListener//
	) {
		final Dialog dlg = getDialog(mContext, title, icon, message, view, positiveButtonText, positiveListener, neutralButtonText, neutralListener, negativeButtonText, negativeListener);
		mDialogs.add(dlg);
		return dlg;
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

	protected void setIntent(int resid, final Intent intent, final int requestCode) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(intent, requestCode);
			};
		});
	}

	protected void setIntent(int resid, final Intent intent) {
		setIntent(resid, intent, -1);
	}

	protected void setIntent(int resid, final Class<?> cls) {
		setIntent(resid, new Intent(mContext, cls), -1);
	}

	public Dialog showTDialog(View view) {
		if (isFinishing())
			return null;
		Dialog dlg = new Dialog(mContext);
		dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dlg.setContentView(view);
		mDialogs.add(dlg);
		dlg.show();
		return dlg;
	}

	public Dialog showList(int array_resid, DialogInterface.OnClickListener onItemclickedListener) {
		return showList(getResources().getTextArray(array_resid), onItemclickedListener);
	}

	public Dialog showList(CharSequence[] items, DialogInterface.OnClickListener onItemclickedListener) {
		if (isFinishing())
			return null;
		Dialog dlg = new AlertDialog.Builder(mContext).setItems(items, onItemclickedListener).create();
		dlg.show();
		mDialogs.add(dlg);
		return dlg;
	}

	public Dialog showList(ListAdapter adapter, DialogInterface.OnClickListener onItemclickedListener) {
		if (isFinishing())
			return null;

		Dialog dlg = new AlertDialog.Builder(mContext).setAdapter(adapter, onItemclickedListener)//
				.create();

		dlg.show();
		mDialogs.add(dlg);
		return dlg;
	}

	protected void toastL(String text, Object... args) {
		Toast.makeText(mContext, String.format(text, args), Toast.LENGTH_LONG).show();
	}

	public void toast(String text, Object... args) {
		Toast.makeText(mContext, String.format(text, args), Toast.LENGTH_SHORT).show();
	}

	protected void toast(int resid) {
		Toast.makeText(mContext, resid, Toast.LENGTH_SHORT).show();
	}

	protected void toastLC(String text, Object... args) {
		Toast toast = Toast.makeText(mContext, String.format(text, args), Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	protected void setCheck(int resid, boolean checked) {

		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof CompoundButton))
			throw new IllegalArgumentException("체크 불가능 컨트롤임");
		final CompoundButton checkbutton = (CompoundButton) v;
		checkbutton.setChecked(checked);

	}

	protected boolean isChecked(int resid) {

		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof CompoundButton))
			throw new IllegalArgumentException("체크 불가능 컨트롤임");

		final CompoundButton checkbutton = (CompoundButton) v;
		return checkbutton.isChecked();

	}

	protected void setRadioCheck(int resid, int checkeditemresid) {

		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof RadioGroup))
			throw new IllegalArgumentException("Radio 불가능 컨트롤임");

		final RadioGroup radioGroup = (RadioGroup) v;
		radioGroup.check(checkeditemresid);
	}

	protected int getCheckedRadioButtonId(int resid) {

		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof RadioGroup))
			throw new IllegalArgumentException("Radio 불가능 컨트롤임");

		final RadioGroup radioGroup = (RadioGroup) v;
		return radioGroup.getCheckedRadioButtonId();
	}

	protected <T> T getCheckedRadioButtonId(int resid, SparseArray<T> map) {
		return (T) map.get(getCheckedRadioButtonId(resid));
	}

	protected void setImageBitmap(int resid, Bitmap bitmap) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof ImageView))
			throw new IllegalArgumentException("ImageView 불가능 컨트롤임");

		final ImageView imageView = (ImageView) v;

		imageView.setImageBitmap(bitmap);
	}

	protected void setText(int resid, CharSequence text) {

		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof TextView))
			throw new IllegalArgumentException("텍스트 불가능 컨트롤임");

		final TextView tv = (TextView) v;

		tv.setText(text);

	}
	protected void setText(int resid, int resid_string) {
		setText(resid, getString(resid_string));
	}
	protected void setHintText(int resid, String text) {

		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof TextView))
			throw new IllegalArgumentException("텍스트 불가능 컨트롤임");

		final TextView tv = (TextView) v;

		tv.setHint(text);
	}

	protected void setTextColor(int resid, int color) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof TextView))
			throw new IllegalArgumentException("텍스트 불가능 컨트롤임");

		final TextView tv = (TextView) v;

		tv.setTextColor(color);
	}

	protected void setHintTextColor(int resid, int color) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof TextView))
			throw new IllegalArgumentException("텍스트 불가능 컨트롤임");

		final TextView tv = (TextView) v;

		tv.setHintTextColor(color);
	}

	protected String text(int resid) {

		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof TextView))
			throw new IllegalArgumentException("텍스트 불가능 컨트롤임");

		final TextView tv = (TextView) v;

		return tv.getText().toString();
	}

	protected String hint(int resid) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof TextView))
			throw new IllegalArgumentException("텍스트 불가능 컨트롤임");

		final TextView tv = (TextView) v;

		return tv.getHint().toString();
	}

	protected void addTextChangedListener(int resid, TextWatcher watcher) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof TextView))
			throw new IllegalArgumentException("텍스트 불가능 컨트롤임");

		final TextView tv = (TextView) v;

		tv.addTextChangedListener(watcher);
	}

	protected void setOnEditorActionListener(int resid_edittext, final int resid) {
		final View v = findViewById(resid_edittext);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof EditText))
			throw new IllegalArgumentException("애디트 불가능 컨트롤임");

		((EditText) v).setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				View vv = findViewById(resid);
				if (vv == null)
					throw new IllegalArgumentException("뭐야이건");

				switch (actionId) {
					case EditorInfo.IME_ACTION_DONE :
					case EditorInfo.IME_ACTION_GO :
					case EditorInfo.IME_ACTION_NONE :
					case EditorInfo.IME_ACTION_SEARCH :
					case EditorInfo.IME_ACTION_SEND :
					case EditorInfo.IME_ACTION_UNSPECIFIED :
						vv.performClick();
						return true;

					default :
						return false;
				}
			}
		});
	}

	protected long getLong(int resid) {
		try {
			return (long) Long.parseLong(text(resid));
		} catch (NumberFormatException e) {
		}
		return 0L;
	}

	protected void setLong(int resid, long l) {
		setText(resid, "" + l);
	}

	protected int getInt(int resid) {
		try {
			return (int) Integer.parseInt(text(resid));
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	protected void setInt(int resid, int l) {
		setText(resid, "" + l);
	}

	protected void setTag(int resid, Object tag) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");
		v.setTag(tag);
	}

	protected void setTag(int resid, int key, Object tag) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");
		v.setTag(key, tag);
	}

	protected Object getTag(int resid, int key) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");
		return v.getTag(key);
	}

	protected Object getTag(int resid) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");
		return v.getTag();
	}

	@SuppressWarnings("unchecked")
	protected <T> T getTag(int resid, T defaultValue) {
		try {
			return (T) getTag(resid);
		} catch (ClassCastException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	protected boolean getTagBoolean(int resid) {
		return (Boolean) getTag(resid);
	}

	protected String getTagString(int resid) {
		return (String) getTag(resid);
	}

	protected int getTagInt(int resid) {
		return (Integer) getTag(resid);
	}

	// Tag에 값을 번들에 보관하는 방법
	private ArrayList<View> mManagedTagView = new ArrayList<View>();

	protected void addManageTag(View v) {
		mManagedTagView.add(v);
	}

	protected void removeManageTag(View v) {
		mManagedTagView.remove(v);
	}

	protected String getTagBS(int resid, String key) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		Object obj = v.getTag();
		if (obj == null)
			return null;
		// throw new IllegalArgumentException("뭐야 또 이건");

		if (!(obj instanceof Bundle))
			return null;
		// throw new IllegalArgumentException("Bundle 불가능 Tag임");

		Bundle bundle = (Bundle) obj;
		if (!bundle.containsKey(key)) {
			return null;
			// throw new IllegalArgumentException("다 좋은대 Bundle에 없어!");
		}
		return bundle.getString(key);
	}

	protected boolean isEmpty(int resid) {

		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof TextView))
			throw new IllegalArgumentException("텍스트 불가능 컨트롤임");

		final TextView tv = (TextView) v;

		return tv.getText().toString().trim().length() <= 0;
	}

	protected int getVisibility(int resid) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");
		return v.getVisibility();
	}

	protected void setVisibility(int resid, int visibility) {
		final View v = findViewById(resid);
		if (v != null)
			v.setVisibility(visibility);
	}

	protected void setVisibility(int resid, boolean visibility) {
		setVisibility(resid, visibility ? View.VISIBLE : View.GONE);
	}

	protected void setVisibility_INVISIBLE(int resid, boolean visibility) {
		setVisibility(resid, visibility ? View.VISIBLE : View.INVISIBLE);
	}

	protected void startAnimation(int resid_view, int resid_anim) {
		final View v = findViewById(resid_view);
		if (v == null) {
			throw new IllegalArgumentException("뭐야이건");
		}
		if (!(v instanceof View))
			throw new IllegalArgumentException("View가아님");
		v.startAnimation(AnimationUtils.loadAnimation(mContext, resid_anim));
	}

	protected void setEnabled(int resid, boolean b) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");
		v.setEnabled(b);
	}

	protected void toggleGone(int resid) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");
		v.setVisibility(View.GONE - v.getVisibility());
	}

	protected void setOnClickListener(int resid, View.OnClickListener onClickListener) {
		final View v = findViewById(resid);
		if (v == null) {
			throw new IllegalArgumentException("뭐야이건");
		}
		if (!(v instanceof View))
			throw new IllegalArgumentException("View가아님");
		v.setOnClickListener(onClickListener);
	}

	protected void setOnLongClickListener(int resid, View.OnLongClickListener onLongClickListener) {
		final View v = findViewById(resid);
		if (v == null) {
			// Log.l("뭐야이건");
			return;
		}
		if (!(v instanceof View))
			throw new IllegalArgumentException("View가아님");
		v.setOnLongClickListener(onLongClickListener);
	}

	protected void setLongIntent(int resid, final Intent intent, final int requestCode) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		v.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				startActivityForResult(intent, requestCode);
				return true;
			};
		});
	}

	protected void setLongIntent(int resid, final Intent intent) {
		setLongIntent(resid, intent, -1);
	}

	protected void setLongIntent(int resid, final Class<?> cls) {
		setLongIntent(resid, new Intent(mContext, cls));
	}

	protected void setOnCheckedChangeListener(int resid, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
		final View v = findViewById(resid);
		if (!(v instanceof CompoundButton))
			throw new IllegalArgumentException("뭐야이건");
		((CompoundButton) v).setOnCheckedChangeListener(onCheckedChangeListener);
	}

	protected void setOnCheckedChangeListener(int resid, RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
		final View v = findViewById(resid);
		if (!(v instanceof RadioGroup))
			throw new IllegalArgumentException("뭐야이건");
		((RadioGroup) v).setOnCheckedChangeListener(onCheckedChangeListener);
	}

	protected <T> void setSpinner(int resid, List<T> objects) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof Spinner))
			throw new IllegalArgumentException("Spinner 불가능 컨트롤임");

		final Spinner spinner = (Spinner) v;

		ArrayAdapter<T> adapter = new ArrayAdapter<T>(mContext, android.R.layout.simple_spinner_item, android.R.id.text1, objects);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	protected void setOnItemSelectedListener(int resid, AdapterView.OnItemSelectedListener listener) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof Spinner))
			throw new IllegalArgumentException("Spinner 불가능 컨트롤임");

		final AdapterView<?> adapterView = (AdapterView<?>) v;

		adapterView.setOnItemSelectedListener(listener);
	}

	protected <T> void setSpinner(int resid, T[] object) {
		setSpinner(resid, Arrays.asList(object));
	}

	protected void setSpinner(int resid, CharSequence[] strings) {
		setSpinner(resid, Arrays.asList(strings));
	}

	protected void setSpinner(int resid, int textArrayResId) {
		CharSequence[] strings = mContext.getResources().getTextArray(textArrayResId);
		setSpinner(resid, Arrays.asList(strings));
	}

	public int getAdapterViewItemPosition(int resid) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof AdapterView<?>))
			throw new IllegalArgumentException("AdapterView 불가능 컨트롤임");

		final AdapterView<?> adapterView = (AdapterView<?>) v;

		return adapterView.getSelectedItemPosition();
	}

	public Object getAdapterViewSelectedItem(int resid) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof AdapterView<?>))
			throw new IllegalArgumentException("AdapterView 불가능 컨트롤임");

		final AdapterView<?> adapterView = (AdapterView<?>) v;

		return adapterView.getSelectedItem();
	}

	public void setAdapterViewSelection(int resid, int position) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof AdapterView<?>))
			throw new IllegalArgumentException("AdapterView 불가능 컨트롤임");

		final AdapterView<?> adapterView = (AdapterView<?>) v;

		adapterView.setSelection(position);
	}

	public void setAdapterViewSelection(int resid, String text) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");

		if (!(v instanceof AdapterView<?>))
			throw new IllegalArgumentException("AdapterView 불가능 컨트롤임");

		final AdapterView<?> adapterView = (AdapterView<?>) v;

		if (text != null) {
			for (int i = 0; i < adapterView.getCount(); i++) {
				if (text.equals(adapterView.getItemAtPosition(i).toString())) {
					adapterView.setSelection(i);
					break;
				}
			}
		}

	}

	protected void setSelected(int resid, boolean selected) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");
		v.setSelected(selected);
	}

	protected int getColor(int resid) {
		try {
			return getResources().getColor(resid);
		} catch (NotFoundException e) {
			return 0x00ffffff;
		}
	}

	/** xx,xxx원 */
	protected void setMoney_(int resid, long money) {
		try {
			setText(resid, String.format("%,d원", money));
		} catch (Exception e) {
		}
	}

	protected void setMoney_(int resid, String money) {
		long moneyL = 0;
		try {
			moneyL = Long.parseLong(money.replaceAll("[^0-9.-]", ""));
		} catch (Exception e) {
			moneyL = 0;
		}
		setMoney_(resid, moneyL);
	}

	protected void setMoney(int resid, long money) {
		try {
			setText(resid, String.format("%,d", money));
		} catch (Exception e) {
		}
	}

	protected void setMoney(int resid, String money) {
		long moneyL = 0;
		try {
			moneyL = Long.parseLong(money.replaceAll("[^0-9.-]", ""));
		} catch (Exception e) {
			moneyL = 0;
		}
		setMoney(resid, moneyL);
	}

	protected long getMoney(int resid) {
		try {
			return Long.parseLong(text(resid).replaceAll("[^0-9.-]", ""));
		} catch (Exception e) {
			return 0;
		}

	}

	protected void requestFocus(int resid) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("뭐야이건");
		v.requestFocus();
	}

	protected int getInt(Uri uri_item, String single_projection) {
		Cursor cursor = mContext.getContentResolver().query(uri_item, new String[]{single_projection}, null, null, null);
		if (cursor.getCount() != 1)
			return 0;
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		cursor.close();
		return result;
	}

	protected String getString(Uri uri_item, String single_projection) {
		Cursor cursor = mContext.getContentResolver().query(uri_item, new String[]{single_projection}, null, null, null);
		if (cursor.getCount() != 1)
			return "";
		cursor.moveToFirst();
		String result = cursor.getString(0);
		cursor.close();
		return result;
	}
}