package android.common;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.DT;

/**
 * <pre>
 * 기능 : ssp관련 파일IO Util
 * 
 *  
 *  file     => xxxxxxxxxx.xxx              filename and ext
 *  filename => xxxxxxxxxxx                 has no ext
 *  pathfile => /xxxx/xxxxx/xxxxxxxxx.xxx   absolute path + filename + ext
 *  ext      => .xxx                        note : has dot(".")
 * </pre>
 */

/**
 * @author djrain
 * 
 *         <pre>
 * &lt;uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * </pre>
 */
public class BaseFile {
	protected static String ROOT = "._";

	public static void CREATE(String packagename) {
		ROOT = packagename;
		makePath(getRoot());
	}

	public static String getRoot() {
		return Environment.getExternalStorageDirectory() + "/" + ROOT;
	}

	protected static String makePath(final String path) {
		File fileDir = new File(path);
		if (!fileDir.isDirectory())
			fileDir.mkdirs();

		return fileDir.getAbsolutePath();
	}

	public static String getVFilename(String filename) {
		return filename.replaceAll(BaseC.regularExpressionVFilename, "_");
	}

	public static File getVPathfile(String filename) {
		return new File(getRoot(), getVFilename(filename));
	}
	public static String getVFilename(String filename, String ext) {
		return filename.replaceAll(BaseC.regularExpressionVFilename, "_");
	}

	public static File getVPathfile(String filename, String ext) {
		return new File(getRoot(), getVFilename(filename) + "." + ext);
	}

	public static boolean exists(String string) {
		String filename = getVFilename(string);
		return new File(getRoot(), filename).exists();
	}
	public static boolean exists(Uri uri) {
		try {
			return new File(URI.create(uri.toString())).exists();
		} catch (Exception e) {
		}
		return false;
	}

	public static long length(Uri uri) {
		try {
			return new File(URI.create(uri.toString())).length();
		} catch (Exception e) {
		}
		return 0;
	}

	public static boolean delete(String filename) {
		return new File(getRoot(), getVFilename(filename)).delete();
	}
	public static boolean delete(Uri uri) {
		try {
			return new File(URI.create(uri.toString())).delete();
		} catch (Exception e) {
		}
		return false;
	}

	public static Uri getTempUri(String prefix, String suffix) {
		return Uri.fromFile(getTempFile(prefix, suffix));
	}
	public static File getTempTime(String prefix, String suffix) {
		return getTempFile(prefix + "_" + DT.yyyymmddhhmmssNow() + "_", suffix);
	}
	public static File getTempFile(String prefix, String suffix) {
		return getTempFile(prefix, suffix, getRoot());
	}
	public static File getTempFile(String prefix, String suffix, String path) {
		try {
			return File.createTempFile(prefix, suffix, new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Uri getUri(String name) {
		if (name == null)
			return null;
		return Uri.fromFile(new File(getRoot(), name));
	}

	public static File[] getfiles(final String ext) {
		return new File(getRoot()).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return !pathname.isDirectory() && pathname.getName().endsWith("." + ext);
			}
		});
	}

	public static File[] getExtfiles(final String ext) {
		return new File(getRoot() + "/" + ext).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return !pathname.isDirectory() && pathname.getName().endsWith("." + ext);
			}
		});
	}

	public static void saveBitmap(Context context, Uri saveUri, Bitmap bitmap) {
		if (saveUri == null || bitmap == null)
			return;

		try {
			OutputStream outputStream = context.getContentResolver().openOutputStream(saveUri);
			if (outputStream != null)
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
		} catch (IOException ex) {
		}

	}

	public static void copy(File source, File target) throws IOException {
		FileChannel inChannel = new FileInputStream(source).getChannel();
		FileChannel outChannel = new FileOutputStream(target, false).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	public static void copy(String source, String target) throws IOException {
		copy(new File(source), new File(target));
	}

	public static void copyDB(SQLiteDatabase source, String target) throws IOException {
		copy(new File(source.getPath()), new File(target));
	}

	/**
	 * @param pathfile
	 * @param obj
	 * <br>
	 *            not used just type<br>
	 *            must null
	 * @return T obj
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T load(String pathfile, T obj) {
		if (obj != null)
			throw new IllegalArgumentException("obj must null");

//		Log.l(pathfile, obj);
		try {
			FileInputStream fis = new FileInputStream(pathfile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			return (T) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static <T extends Serializable> boolean save(String pathfile, T obj) {
//		Log.l(obj, pathfile);

		try {
			FileOutputStream fos = new FileOutputStream(pathfile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getExt(String pathfile) {
		String filename = new File(pathfile).getName();
		return filename.substring(filename.lastIndexOf(".") + ".".length());
	}
}
