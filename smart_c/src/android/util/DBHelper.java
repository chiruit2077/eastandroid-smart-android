package android.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author djrain
 * 
 */
public class DBHelper {

	public static long getLong(Context context, Uri uri) {
		long result = -1L;
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		if (cursor.getColumnCount() == 1 && cursor.getCount() == 1) {
			cursor.moveToFirst();
			result = cursor.getLong(0);
		}

		if (!cursor.isClosed())
			cursor.close();

		return result;
	}
}
