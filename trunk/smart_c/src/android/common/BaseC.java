package android.common;

public interface BaseC {
	int LENGTH_resRegNo = 13;

	String regularExpressionEmail = "^([\\w-\\.]+)@((?:[\\w]+\\.)+[a-zA-Z]{2,4})$";
	String regularExpressionPhoneNo = "^(0(?:505|70|10|11|16|17|18|19))(\\d{3}|\\d{4})(\\d{4})$";
	String regularExpressionAuthNo = "\\d{5}";
	String regularExpressionMessageinAuthNo = ".*\\[(\\d{5})\\].*";
	String regularExpressionVFilename = "\\\\/|[&{}?=/\\\\: .<>*|\\]\\[\\\"\\']";
	String CHAR_KSC5601 = "KSC5601";
	String CHAR_UTF8 = "UTF-8";

}
