package android.miscellaneous;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.miscellaneous.Log;

/**
 * @author djrain
 * 
 */
public class ProviderUtil {

	public static void _dumpProvider(Context context, Uri uri) {
		Log.e("_dumpProvider", "start: " + uri.toString());
		final ContentResolver resolver = context.getContentResolver();
		Cursor c = null;
		try {
			c = resolver.query(uri, null, null, null, null);
		} catch (Exception e) {
			Log.e("_dumpProvider", "resolver.query Exception");
			e.printStackTrace();
		}

		String[] columns;
		if (c != null) {
			try {
				columns = c.getColumnNames();
				String str = "";
				for (String string : columns) {
					str += string + ",";
				}
				Log.e("_dumpProvider:C", str);
			} catch (Exception e) {
				e.printStackTrace();
				c.close();
			}

			int count = c.getCount();
			Log.e("_dumpProvider:R", "" + count);
			if (c.getCount() > 0) {

				int countColumns = c.getColumnCount();
				while (c.moveToNext()) {
					String str = "";
					for (int i = 0; i < countColumns; i++) {
						try {
							str += c.getString(i) + ",";
						} catch (Exception e) {
							str += "BLOB,";
						}

					}
					Log.e("_dumpProvider:R", str);
				}
			}
		}
		Log.e("_dumpProvider", "end: =======================================================");
	}

	public static void _dumpCursor(Cursor c) {
		String[] columns;
		if (c != null) {
			try {
				columns = c.getColumnNames();
				String str = "";
				for (String string : columns) {
					str += string + ",";
				}
				Log.e("_dumpProvider:C", str);
			} catch (Exception e) {
				e.printStackTrace();
				c.close();
			}

			int count = c.getCount();
			Log.e("_dumpProvider:R", "" + count);
			if (c.getCount() > 0) {

				int countColumns = c.getColumnCount();
				while (c.moveToNext()) {
					String str = "";
					for (int i = 0; i < countColumns; i++) {
						try {
							str += c.getString(i) + ",";
						} catch (Exception e) {
							str += "BLOB,";
						}

					}
					Log.e("_dumpProvider:R", str);
				}
			}
		}
		Log.e("_dump Cursor", "end: =======================================================");
	}
}
