package android.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.common.BaseC;
import android.content.Context;
import android.miscellaneous.Log;
import android.telephony.PhoneNumberUtils;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;

/**
 * @author djrain
 * 
 */
public class PhoneAutoCompleteEditText extends AutoCompleteTextView {

	Context mContext;

	private boolean mIsEdit;

	public PhoneAutoCompleteEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		create(context);
	}

	public PhoneAutoCompleteEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context);
	}

	public PhoneAutoCompleteEditText(Context context) {
		super(context);
		create(context);
	}

	private void create(Context context) {
		mContext = context;
		setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		setSingleLine();
		setSelectAllOnFocus(true);

	}

	public void setPhoneNo(String phno) {
		setText(phno);
	}

	public String getPhoneNo() {

		Pattern pattern = Pattern.compile(BaseC.regularExpressionPhoneNo);
		String str = PhoneNumberUtils.stripSeparators(getText().toString());
		Matcher matcher = pattern.matcher(str);

		if (matcher.matches()) {
			str = matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3);
			Log.l(str);
			return str;
		} else {
			Log.l(str);
			return str;
		}
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		if (mIsEdit)
			return;
		Log.l();
		mIsEdit = true;

		int lastSelectionEnd = getText().length() - getSelectionEnd();

		try {
			setPhoneNo(getPhoneNo());
		} catch (Exception e) {
			setPhoneNo("");
		}

		try {
			int selection = getText().length() - lastSelectionEnd;
			if (selection < 0)
				selection = 0;
			if (selection > getText().length())
				selection = getText().length();
			setSelection(selection);
		} catch (IndexOutOfBoundsException e) {
			setSelection(getText().length());
		}

		super.onTextChanged(text, start, lengthBefore, lengthAfter);

		mIsEdit = false;
	}

}