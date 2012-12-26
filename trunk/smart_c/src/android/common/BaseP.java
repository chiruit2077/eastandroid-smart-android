package android.common;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author djrain
 * 
 */
public class BaseP {

	private static final String DEFVALUE_STRING = "";
	private static final int DEFVALUE_INT = -1;
	private static final long DEFVALUE_LONG = -1L;
	private static final boolean DEFVALUE_BOOLEAN = false;
	public static BaseP INSTANCE = new BaseP();

	public static BaseP c() {
		return INSTANCE;
	}

	protected SharedPreferences mPreferences;

	public void init(Context context) {
		mPreferences = getDefaultSharedPreferences(context);
	}

	public static SharedPreferences getDefaultSharedPreferences(Context context) {
		return context.getSharedPreferences(getDefaultSharedPreferencesName(context), getDefaultSharedPreferencesMode());
	}

	private static String getDefaultSharedPreferencesName(Context context) {
		return context.getPackageName() + "_preferences";
	}

	private static int getDefaultSharedPreferencesMode() {
		return Context.MODE_PRIVATE;
	}

	public SharedPreferences get() {
		return mPreferences;
	}

	public Map<String, ?> getAll() {
		return mPreferences.getAll();
	}

	public boolean contain(String key) {
		return mPreferences.contains(key);
	}

	public void remove(String key) {
		mPreferences.edit().remove(key).commit();
	}

	public void set(String key, long value) {
		mPreferences.edit().putLong(key, value).commit();
	}

	public void set(String key, int value) {
		mPreferences.edit().putInt(key, value).commit();
	}

	public void set(String key, String value) {
		mPreferences.edit().putString(key, value).commit();
	}

	public void set(String key, boolean value) {
		mPreferences.edit().putBoolean(key, value).commit();
	}

	public long getLong(String key) {
		return mPreferences.getLong(key, DEFVALUE_LONG);
	}

	public int getInt(String key) {
		return mPreferences.getInt(key, DEFVALUE_INT);
	}

	public String getString(String key) {
		return mPreferences.getString(key, DEFVALUE_STRING);
	}

	public boolean getBoolean(String key) {
		return mPreferences.getBoolean(key, DEFVALUE_BOOLEAN);
	}

	public long getLong(String key, long defValue) {
		return mPreferences.getLong(key, defValue);
	}

	public int getInt(String key, int defValue) {
		return mPreferences.getInt(key, defValue);
	}

	public String getString(String key, String defValue) {
		return mPreferences.getString(key, defValue);
	}

	public boolean getBoolean(String key, boolean defValue) {
		return mPreferences.getBoolean(key, defValue);
	}

}
