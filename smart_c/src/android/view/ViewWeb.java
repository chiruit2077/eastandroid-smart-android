package android.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.common.BaseCommand;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.miscellaneous.E;
import android.miscellaneous.Log;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.util.AttributeSet;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author djrain
 * 
 */
public class ViewWeb extends WebView {

	private OnLinkClickListener onLinkClickListener;

	public static interface OnLinkClickListener {
		boolean onLinkClicked(String url);

		boolean onLoading(boolean isIng);
	}

	public void viewSource() {
		loadUrl("javascript:webapp.viewSource(document.documentElement.outerHTML);");
	}

	public void setOnLinkClickListener(OnLinkClickListener onLinkClickListener) {
		this.onLinkClickListener = onLinkClickListener;
	}

	private Context mContext;
	// private int mW;
	public int mH;

	public ViewWeb(Context context) {
		super(context);
		mContext = context;
		create();
	}

	public ViewWeb(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		create();
	}

	public ViewWeb(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		create();
	}

	@Override
	protected void onSizeChanged(int w, int h, int ow, int oh) {
		super.onSizeChanged(w, h, ow, oh);
		mH = h;
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void create() {

		WebSettings webSettings = getSettings();
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setDomStorageEnabled(true);

		webSettings.setSavePassword(false);
		webSettings.setGeolocationEnabled(true);
		webSettings.setAllowFileAccess(false);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setSupportZoom(false);
		webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		webSettings.setAppCacheEnabled(false);

		// setBackgroundColor(Color.WHITE);

		setVerticalScrollBarEnabled(false);

		setWebViewClient(new HanaWebViewClient());
		setWebChromeClient(new HanaWebChromeClient());

		addJavascriptInterface(new ViewWebBridge(), "webapp");
		// ATM 찾기 부분 로딩 다이알로그 인터페이스
		addJavascriptInterface(new ViewWebBridge(), "loadingDialogJSInterface");

	}

	public class HanaWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.confirm();
				}
			};

			if (mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
				BaseCommand.getDialog(mContext, message, "확인", positiveListener).show();
			} else {
				Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
				result.confirm();
			}

			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
			Log.l();
			DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.confirm();
				}
			};
			DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.cancel();
				}
			};

			if (mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
				BaseCommand.getDialog(mContext, message, "확인", positiveListener, "취소", negativeListener).show();
			} else {
				Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_LONG).show();
				result.cancel();
			}
			return true;
		}
	}

	private class HanaWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (E.NETLOG)
				Log.l(Uri.parse(url));

			if (url.startsWith("tel:")) {
				mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
				return true;
			} else {
				Uri uri = Uri.parse(url);
				if (uri != null)
					view.loadUrl(url);
				return true;
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (onLinkClickListener != null)
				onLinkClickListener.onLoading(false);
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (onLinkClickListener != null)
				onLinkClickListener.onLoading(true);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Log.l("WebView " + view + ", int " + errorCode + ", String " + description + ", String " + failingUrl + "");
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

	}

	@SuppressWarnings("unused")
	private class ViewWebBridge {
		Handler handler = new Handler();

		public void viewSource(final String source) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					TextView tv = new TextView(mContext);
					tv.setTextColor(Color.RED);
					ScrollView sv = new ScrollView(mContext);
					sv.addView(tv);
					ViewWeb.this.addView(sv);
					tv.setText(source);
				}
			});
		}

		public void showPopup(final String msg) {
			handler.post(new Runnable() {
				public void run() {
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
		public void show() {
			Log.l();
		}

		public boolean isShowing() {
			return false;
		}

		public void dismiss() {
			Log.l();

		}
		public boolean startActivity(final String action, final String data) {
			try {
				mContext.startActivity(new Intent(action, Uri.parse(data)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}

}
