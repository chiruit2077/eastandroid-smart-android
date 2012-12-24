package android.util;

/**
 * @author djrain
 * 
 */
public class Money {

	public static String own(long money) {
		return String.format("%,d", money);
	}

	public static String own(String money) {
		return own(parseMoney(money));
	}

	public static String own_(long money) {
		return String.format("%,d원", money);
	}

	public static long parseMoney(String text) {
		try {
			return Long.parseLong(text.replaceAll("[^0-9.-]", ""));
		} catch (Exception e) {
			return 0L;
		}
	}
}
