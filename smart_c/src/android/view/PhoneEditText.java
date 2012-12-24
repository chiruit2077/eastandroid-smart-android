package android.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.common.BaseC;
import android.content.Context;
import android.miscellaneous.Log;
import android.telephony.PhoneNumberUtils;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

/**
 * @author djrain
 * 
 */
public class PhoneEditText extends EditText {

	Context mContext;

	private boolean mIsEdit;

	public PhoneEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		create(context);
	}

	public PhoneEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context);
	}

	public PhoneEditText(Context context) {
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

		Pattern pattern = Pattern.compile(BaseC.regularExpressionPhoneNo);
		Matcher matcher = pattern.matcher(phno);
		if (matcher.matches()) {
			setText(PHONENUMBER_FORMATER(phno));
		} else {
			setText(phno);
		}
	}

	public String getPhoneNo() {
		try {
			return PhoneNumberUtils.stripSeparators(getText().toString());
		} catch (Exception e) {
			return getText().toString();
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

	// 0505 010 011 016 017 018 019 번호에 한하여 문자를 보낼수 있다
	public static String PHONENUMBER_FORMATER(String number) {
		String strPhoneNo = PhoneNumberUtils.stripSeparators(number);

		Pattern pattern = Pattern.compile(BaseC.regularExpressionPhoneNo);
		Matcher matcher = pattern.matcher(strPhoneNo);

		if (strPhoneNo == null || !matcher.matches())
			return "";

		return matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3);
	}

}