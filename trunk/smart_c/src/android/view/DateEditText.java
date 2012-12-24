package android.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DT;
import android.widget.EditText;

public class DateEditText extends EditText {
	private String mFormat = "yyyyMMdd";
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(mFormat);

	public void setFormat(String format) {
		long milliseconds = getDate();
		mFormat = format;
		mSimpleDateFormat.applyPattern(format);
		setDate(milliseconds);
	}

	public DateEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DateEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DateEditText(Context context) {
		super(context);
	}

	public void setDate(long milliseconds) {
		setText(DT.format(milliseconds, mSimpleDateFormat));
	}
	public long getDate() {
		return DT.parse(getText().toString(), mSimpleDateFormat);
	}

	public boolean isValid() {
		try {
			String date_string = getText().toString();
			Date date = mSimpleDateFormat.parse(date_string);
			if (date_string.equals(mSimpleDateFormat.format(date))) {
				return true;
			}
		} catch (ParseException e) {
			return false;
		}
		return false;
	}

	public long getTimeMillis() throws ParseException {
		return mSimpleDateFormat.parse(getText().toString()).getTime();
	}

	public String getDateFormat(String format) throws ParseException {
		return DT.format(getTimeMillis(), new SimpleDateFormat(format));
	}
}
