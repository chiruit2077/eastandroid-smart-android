package android.miscellaneous;

import java.io.UnsupportedEncodingException;

/**
 * @mix djrain
 * 
 */
public class HexUtil {
	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	public static String h2s(byte[] bytearray) throws UnsupportedEncodingException {
		if (bytearray == null)
			throw new NullPointerException();
		return asHex(bytearray);
	}

	public static byte[] s2h(String string_hex_format) {
		byte[] bytes = new byte[string_hex_format.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(string_hex_format.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}

	private static String asHex(byte[] buf) {
		char[] chars = new char[2 * buf.length];
		for (int i = 0; i < buf.length; ++i) {
			chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
		}
		return new String(chars);
	}

}