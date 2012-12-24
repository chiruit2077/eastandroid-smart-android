package android.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;

public class ResourceProvider extends ContentProvider {
	//	public static final String VND = "vnd.hanabank.application/file";
	public static final String AUTHORITY = "com.hanabank.skwallet";
	public static final String URI = "content://" + AUTHORITY;

	@Override
	public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
		android.miscellaneous.Log.l("AssetFileDescriptor", "Uri: " + uri);
		android.miscellaneous.Log.l("AssetFileDescriptor", "mode: " + mode);
		try {
			return getContext().getAssets().openFd(uri.getLastPathSegment());
		} catch (IOException e) {
			throw new FileNotFoundException(e.getMessage());
		}
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		URI_MATCHER.addURI(AUTHORITY, "*", 101);
	}

	@Override
	public String getType(Uri uri) {
		return null;
		//		if (URI_MATCHER.match(uri) == UriMatcher.NO_MATCH)
		//			return null;
		//		return VND;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}

	@Override
	public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}

	@Override
	public int delete(Uri uri, String s, String[] as) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}

	@Override
	public Cursor query(Uri uri, String[] as, String s, String[] as1, String s1) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}

}