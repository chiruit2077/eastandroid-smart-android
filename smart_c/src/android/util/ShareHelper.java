package android.util;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * @author djrain
 * 
 */
public class ShareHelper {

	/**
	 * <pre>
	 * 이메일 전송 호출 기능
	 * </pre>
	 * 
	 * @param context
	 * @param emailTo
	 *            수신자
	 * @param emailCC
	 *            참조자
	 * @param subject
	 *            제목
	 * @param body
	 *            내용
	 * @param filePaths
	 *            첨부파일
	 */
	public static Intent getIntentMailShare(String[] to, String[] cc, String subject, String body, String[] filePaths, String[] bcc) {

		final Intent intent = new Intent();
		intent.setType("text/plain");
		if (to != null)
			intent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
		if (cc != null)
			intent.putExtra(android.content.Intent.EXTRA_CC, cc);
		if (bcc != null)
			intent.putExtra(android.content.Intent.EXTRA_BCC, bcc);
		if (subject != null)
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		if (body != null)
			intent.putExtra(Intent.EXTRA_TEXT, body);

		if (filePaths != null) {
			ArrayList<Uri> uris = new ArrayList<Uri>();
			for (String file : filePaths) {
				if (file.startsWith("file://")) {
					uris.add(Uri.parse(file));
				} else {
					File f = new File(file);
					uris.add(Uri.fromFile(f));
				}
			}
			intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			intent.setAction(Intent.ACTION_SEND_MULTIPLE);
		} else {
			intent.setAction(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("mailto:"));
		}
		return intent;
	}

	public static Intent getIntentTextShare(String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		return intent;
	}

	public static Intent getIntentImageShare(Context context, String pathfile) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		Uri uri = Uri.fromFile(context.getFileStreamPath(pathfile));
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		return intent;
	}

	public static void shareTo(Context context, String title, Intent intent) {

		intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"pluto@eastandroid.com"});//

		context.startActivity(Intent.createChooser(intent, title));
	}
	public static void shareTo(Context context, Intent intent) {
		shareTo(context, "Share to...", intent);
	}

}
