package android.miscellaneous;

/**
 * @author djrain
 * 
 */
public class Assert {
	public static void T(boolean bool) {
		if (!bool) {
			new Exception("왜왔니?").printStackTrace();
		}
	}

	public static void F() {
		new Exception("또왔니?").printStackTrace();
	}
}
