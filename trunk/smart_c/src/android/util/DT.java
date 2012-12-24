package android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author djrain
 * 
 */
public class DT {

	public static final long TIMEGAP = +32400000L;

	public static long today() {
		return ST(System.currentTimeMillis());
	}

	public static String yyyymmddNow() {
		return DT.format(System.currentTimeMillis(), DT.yyyymmdd);
	}

	public static String yyyymmddhhmmssNow() {
		return DT.format(System.currentTimeMillis(), DT.yyyymmddhhmmss);
	}

	public static String yyyymmdd(long milliseconds) {
		return DT.format(milliseconds, DT.yyyymmdd);
	}

	public static long ST(long milliseconds) {
		return ((milliseconds + TIMEGAP) / 86400000L * 86400000L) - TIMEGAP;
	}

	public static long stripTime(long milliseconds) {
		return ST(milliseconds);
	}

	public static long SD(long milliseconds) {
		milliseconds = ST(milliseconds);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(milliseconds);
		c.set(Calendar.DATE, 1);
		return c.getTimeInMillis();
	}

	public static long stripDay(long milliseconds) {
		return SD(milliseconds);
	}

	public static void BookMonth(int bookStartday, long milliseconds, long[] range) {

		// Log.da(milliseconds);

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milliseconds);
		int sd = bookStartday;
		cal.set(Calendar.DATE, sd);
		long s = DT.ST(cal.getTimeInMillis());
		cal.add(Calendar.MONTH, (s <= milliseconds ? +1 : -1));
		long e = DT.ST(cal.getTimeInMillis());

		range[0] = Math.min(s, e);
		range[1] = Math.max(s, e);

		// Log.da(range[0], range[1]);
	}

	public static final String FORMAT_BOOKMONTH = "%%04d. %%02d . %02d ~ %%02d . %02d";
	public static final String FORMAT_BOOKYEAR = "%%04d";
	public static final String FORMAT_BOOKDATE = "%%04d. %%02d ";

	public static String BookFormat(int bookStartday, long milliseconds, String foramt) {
		long[] range = new long[2];
		DT.BookMonth(bookStartday, milliseconds, range);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(range[0]);
		int sy = cal.get(Calendar.YEAR);
		int sm = cal.get(Calendar.MONTH);
		int sd = cal.get(Calendar.DATE);

		cal.setTimeInMillis(range[1]);
		cal.add(Calendar.DATE, -1);
		int em = cal.get(Calendar.MONTH);
		int ed = cal.get(Calendar.DATE);

		String format = String.format(foramt, sd, ed);
		String text = String.format(format, sy, sm + 1, em + 1);
		return text;
	}

	public static long move(long milliseconds, int field, int distance) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(milliseconds);
		c.add(field, distance);
		return c.getTimeInMillis();
	}

	public static String format(long milliseconds, SimpleDateFormat to) {
		try {
			return to.format(new Date(milliseconds));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String format(String date, SimpleDateFormat from, SimpleDateFormat to) {
		try {
			return to.format(from.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static long parse(String date, SimpleDateFormat from) {
		try {
			return from.parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 달의 몇번째 주?
	public static int weekofmonth(long milliseconds) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(milliseconds);
		return c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
	}

	public static final SimpleDateFormat yyyymm = new SimpleDateFormat("yyyyMM");

	public static final SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat yyyy__ = new SimpleDateFormat("yyyy년");

	public static final SimpleDateFormat weekofmonth = new SimpleDateFormat("W");

	public static final SimpleDateFormat mmdd__ = new SimpleDateFormat("MM월dd일");
	public static final SimpleDateFormat mmdd_2 = new SimpleDateFormat("MM.dd");

	public static final SimpleDateFormat mm__ = new SimpleDateFormat("MM월");
	public static final SimpleDateFormat m__ = new SimpleDateFormat("M월");

	public static final SimpleDateFormat dd = new SimpleDateFormat("dd");
	public static final SimpleDateFormat dd__ = new SimpleDateFormat("dd일");

	public static final SimpleDateFormat emdhs = new SimpleDateFormat("(E)M/d HH:mm");

	public static final SimpleDateFormat mde = new SimpleDateFormat("M/d(E)");
	public static final SimpleDateFormat dayofweek = new SimpleDateFormat("E");
	public static final SimpleDateFormat dayofweek__ = new SimpleDateFormat("E요일");

	public static final SimpleDateFormat yyyy_mmdd = new SimpleDateFormat("yyyy\nMM.dd");
	public static final SimpleDateFormat yyyy__mmdd = new SimpleDateFormat("yyyy.\nMM.dd");

	public static final SimpleDateFormat yyyymmdd__ = new SimpleDateFormat("yyyy년 MM월 dd일");
	public static final SimpleDateFormat yyyymmddE__ = new SimpleDateFormat("yyyy년 MM월 dd일 E요일");

	public static final SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat yyyymmdd_ = new SimpleDateFormat("yyyy/MM/dd");
	public static final SimpleDateFormat yyyymmdd_1 = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat yyyymmdd_2 = new SimpleDateFormat("yyyy.MM.dd");

	public static final SimpleDateFormat yyyymmddhhmmss = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat yyyymmddhhmmss_ = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static final SimpleDateFormat yyyymmddhhmmss_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat yyyymmddhhmmss_2 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	public static final SimpleDateFormat yyyymmddhhmmss_3 = new SimpleDateFormat("yyyy.MM.dd a hh:mm");

	public static final SimpleDateFormat mmddhhmmss__ = new SimpleDateFormat("MM월dd일 HH:mm");

	public static final SimpleDateFormat ahhmm = new SimpleDateFormat("a hh:mm");
	public static final SimpleDateFormat hhmmss = new SimpleDateFormat("HHmmss");
	public static final SimpleDateFormat mmss = new SimpleDateFormat("mm:ss");

	public static int length(long milliseconds_first, long mILLISECONDS, int year) {

		return 0;
	}
}
