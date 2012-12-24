package android.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.common.BaseC;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.miscellaneous.Assert;
import android.miscellaneous.Log;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class SmsReceiverEditText extends EditText {

	private Context mContext;

	public static interface OnSmsReceiverEditTextListener {
		public void onSmsReceiverEditText(SmsReceiverEditText smsReceiverEditText, String authno);
	}

	private OnSmsReceiverEditTextListener listener;
	SmsBroadcastReceiver receiver;

	private class SmsBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
//			Log.l("sms: >>>>>");
//			Object tpdus[] = (Object[]) intent.getExtras().get("pdus");
//			for (Object pdu : tpdus) {
//				SmsMessage part = SmsMessage.createFromPdu((byte[]) pdu);
//				Log.l("sms:", part.getDisplayMessageBody());
//			}

			Bundle data = intent.getExtras();
			if (data != null) {
				Object pdus[] = (Object[]) data.get("pdus");
				String authNo = "";

				for (Object pdu : pdus) {
					SmsMessage part = SmsMessage.createFromPdu((byte[]) pdu);
					String message = part.getDisplayMessageBody();
					authNo = getAuthNo(message);
					// Log.l(authNo, message);
					if (authNo.matches(BaseC.regularExpressionAuthNo)) {
						setAuth(authNo);
					}
					if (listener != null && authNo != null && authNo.length() > 0)
						listener.onSmsReceiverEditText(SmsReceiverEditText.this, authNo);
				}
			}
		}
	}

	public void setOnSmsReceiverEditTextListener(OnSmsReceiverEditTextListener listener) {
		this.listener = listener;
	}

	public SmsReceiverEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		create(context);
	}

	public SmsReceiverEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context);
	}

	public SmsReceiverEditText(Context context) {
		super(context);
		create(context);
	}

	private void create(Context context) {
		mContext = context;
		setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		setSingleLine();
		setSelectAllOnFocus(true);
	}

	// set
	// 인증 문자열 넣기
	public void setAuth(String authno) {
		Assert.T(authno.matches(BaseC.regularExpressionAuthNo));
		setText(authno);
	}

	// get
	public String getAuth() {
		return getText().toString();
	}

	// event

	// 인증번호의 형식 문자열인지 ?
	private boolean isMatch(String authno) {
		Assert.T(authno != null);
		return authno.matches(BaseC.regularExpressionAuthNo);
	}

	// 문자열 안에 가져오기 없으면 ""
	private String getAuthNo(String message) {
		try {
			Pattern pattern = Pattern.compile(BaseC.regularExpressionMessageinAuthNo, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(message);
			if (matcher.matches())
				return matcher.group(1);
		} catch (Exception e) {
			Log.l(e.getMessage());
		}
		return "";
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		String authno = text.toString();
		if (isMatch(authno)) {
			unregisterReceiver();
		}
	}

	public void registerReceiver() {
		// Log.l("sms 등록");
		receiver = new SmsBroadcastReceiver();
		mContext.registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
	}

	public void unregisterReceiver() {
		if (receiver != null) {
			// Log.l("sms 해지");
			mContext.unregisterReceiver(receiver);
			receiver = null;
		} else {
			// Log.l("sms 이미 해지");
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		unregisterReceiver();
		super.onDetachedFromWindow();
	}
}
