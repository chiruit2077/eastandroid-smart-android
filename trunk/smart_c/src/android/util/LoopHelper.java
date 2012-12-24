package android.util;

import java.util.Calendar;

import android.miscellaneous.Log;

public class LoopHelper {
	public static enum REPEAT_CODE {
		NONE, DAY, WEEKDAYS, WEEKEND, WEEK, WEEK2, MONTH_OF_WEEK, MONTH, MONTHEND, YEAR;

		public static CharSequence[] DISPLAY = new String[]{("없음"), ("매일"), ("주중(월~금)"), ("주말(토~일)"), ("매주"), ("격주"), ("매월"), ("매월"), ("월말"), ("매년")};

		public int o() {
			return ordinal();
		}

		@Override
		public String toString() {
			return (String) DISPLAY[this.ordinal()];
		}

		public String toString(long milliseconds) {

			switch (this) {
				case WEEK :
					return toString() + "(" + DT.format(milliseconds, DT.dayofweek__) + " 마다)";
				case MONTH_OF_WEEK :
					int i = DT.weekofmonth(milliseconds);
					return toString() + "(" + (i >= 5 ? "마지막 " : i + "번째 ") + DT.format(milliseconds, DT.dayofweek__) + " 마다)";
				case WEEK2 :
					return toString() + "(2주 " + DT.format(milliseconds, DT.dayofweek__) + ")";
				case MONTH :
					return toString() + "(" + DT.format(milliseconds, DT.dd__) + " 마다)";
				case YEAR :
					return toString() + "(" + DT.format(milliseconds, DT.mmdd__) + " 마다)";
			}

			return toString();
		}

	};

	public static class Builder {
		private long mStartDay = 0;
		private long mEndDay = 0;
		private long mDDay = 0;
		private REPEAT_CODE mLoop = REPEAT_CODE.NONE;
		private Calendar c = Calendar.getInstance();
		private long mEndDDay = Long.MAX_VALUE;

		public void setRange(long startDay, long endDay) {

			mStartDay = startDay;
			mEndDay = endDay;

		}

		public void setRangeMoth(long month) {
			c.setTimeInMillis(month);
			c.set(Calendar.MILLISECOND, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.DATE, 1);
			long s = c.getTimeInMillis();

			c.setTimeInMillis(month);
			c.set(Calendar.MILLISECOND, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.MONTH, 1);
			long e = c.getTimeInMillis();

			Log.da(s, e);
			setRange(s, e);
		}

		public void setRangeDay(long day) {
			long s = day / 86400000L * 86400000L;
			long e = ((day / 86400000L) + 1) * 86400000L;
			Log.da(s, e);
			setRange(s, e);
		}

		public void setDDay(long milliseconds) {
			mDDay = milliseconds;
		}

		public void setEndDDay(long milliseconds) {
			mEndDDay = milliseconds;
		}

		public void setREPEAT_CODE(REPEAT_CODE looptype) {
			mLoop = looptype;
		}

		public LoopHelper create() {
			return new LoopHelper(mStartDay, mEndDay, mDDay, mEndDDay, mLoop);
		}

		public void setDay(long milliseconds) {
			mStartDay = milliseconds / 86400000L * 86400000L;
			mEndDay = ((milliseconds / 86400000L) + 1) * 86400000L;
		}
	}

	private static int week_of_month;

	private static int day_of_week;

	long mStartDay = 0;
	long mEndDay = 0;
	long mDDay = 0;
	long mEndDDay = Long.MAX_VALUE;
	REPEAT_CODE mLoop = REPEAT_CODE.NONE;

	private LoopHelper(long start, long end, long dday, long endDDay, REPEAT_CODE loop) {
		this.mStartDay = start;
		this.mEndDay = end;
		this.mDDay = dday;
		this.mLoop = loop;
		this.mEndDDay = endDDay;

		setStart();
	}

	Calendar c = Calendar.getInstance();

	public boolean hasNext() {
		return mMilliseconds < Math.min(mEndDay, mEndDDay);
	}

	private void setStart() {
		switch (mLoop) {
			case MONTH :
				mMilliseconds = firstMonth(mDDay, mStartDay);
				break;
			case WEEK :
				mMilliseconds = firstEveryweek(mDDay, mStartDay);
				break;
			case WEEK2 :
				mMilliseconds = firstEvery2week(mDDay, mStartDay);
				break;
			case YEAR :
				mMilliseconds = firstYear(mDDay, mStartDay);
				break;
			case DAY :
				mMilliseconds = firstDay(mDDay, mStartDay);
				break;
			case MONTH_OF_WEEK :
				mMilliseconds = firstMonthOfWeek(mDDay, mStartDay);
				break;
			case WEEKEND :
				mMilliseconds = firstWeekend(mDDay, mStartDay);
				break;
			case WEEKDAYS :
				mMilliseconds = firstWeekdays(mDDay, mStartDay);
				break;
			case MONTHEND :
				mMilliseconds = firstMonthend(mDDay, mStartDay);
				break;
			default :

				break;
		}
	}

	long mMilliseconds;

	public long getNext() {
		long milliseconds = mMilliseconds;
		switch (mLoop) {
			case MONTH :
				c.setTimeInMillis(mMilliseconds);
				c.add(Calendar.MONTH, 1);
				mMilliseconds = c.getTimeInMillis();
				break;
			case YEAR :
				c.setTimeInMillis(mMilliseconds);
				c.add(Calendar.YEAR, 1);
				mMilliseconds = c.getTimeInMillis();
				break;
			case DAY :
				mMilliseconds += 86400000L;
				break;
			case WEEK :
				mMilliseconds += 7L * 86400000L;
				break;
			case WEEK2 :
				mMilliseconds += 14L * 86400000L;
				break;
			case WEEKEND :
				mMilliseconds = firstWeekend(mMilliseconds + 86400000L);
				break;
			case WEEKDAYS :
				mMilliseconds = firstWeekdays(mMilliseconds + 86400000L);
				break;
			case MONTHEND :
				mMilliseconds = firstMonthend(mMilliseconds + 86400000L);
				break;
			case MONTH_OF_WEEK :
				c.setTimeInMillis(mMilliseconds);
				c.add(Calendar.MONTH, 1);
				mMilliseconds = dayOfWeekInMonth(c.getTimeInMillis(), day_of_week, week_of_month);
				break;
			default :

				break;
		}
		return milliseconds;
	}

	public static long firstMonthOfWeek(long dday, long startday) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(dday);
		week_of_month = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
		day_of_week = c.get(Calendar.DAY_OF_WEEK);

		// Log.da(c.getTimeInMillis());

		return dayOfWeekInMonth(Math.max(dday, startday), day_of_week, week_of_month);
	}

	// public static long dayOfWeekInMonth(long dday, int week_of_month) {
	// Calendar c = Calendar.getInstance();
	// int dayofweek = c.get(Calendar.DAY_OF_WEEK);
	// return dayOfWeekInMonth(dday, dayofweek, week_of_month);
	// }

	public static long dayOfWeekInMonth(long dday, int dayofweek, int week_of_month) {
		Calendar c = Calendar.getInstance();

		c.setTimeInMillis(dday);
		int m = c.get(Calendar.MONTH);

		c.set(Calendar.DAY_OF_WEEK, dayofweek);
		c.set(Calendar.DAY_OF_WEEK_IN_MONTH, week_of_month);
		int mc = c.get(Calendar.MONTH);

		if (m != mc) {
			c.set(Calendar.MONTH, mc - 1);
			c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 4);
		}

		// Log.da(c.getTimeInMillis());

		return c.getTimeInMillis();
	}

	// // 달력시작하는 요일을 주첫날로 설정하고오기
	// public static void setFirstDayOfWeek(final Calendar c, long dday) {
	// // 달력셑팅
	// c.setTimeInMillis(dday);
	// int m = c.get(Calendar.MONTH);
	// int y = c.get(Calendar.YEAR);
	// int d = c.get(Calendar.DATE);
	// c.set(y, m, 1);
	// c.setFirstDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
	// // 원래 날짜로 이동
	// c.set(Calendar.DATE, d);
	// }

	public static long firstMonthend(long dday, long startday) {
		return firstMonthend(Math.max(dday, startday));
	}

	public static long firstMonthend(long startday) {
		Calendar c = Calendar.getInstance();
		int y, m;
		c.setTimeInMillis(startday);
		m = c.get(Calendar.MONTH);
		y = c.get(Calendar.YEAR);
		c.set(y, m + 1, 1);
		c.add(Calendar.DATE, -1);
		return c.getTimeInMillis();
	}

	public static long firstWeekdays(long dday, long startday) {
		return firstWeekdays(Math.max(dday, startday));
	}

	private static long firstWeekdays(long startday) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(startday);

		while (c.get(Calendar.DAY_OF_WEEK) % 6 == 1)
			c.add(Calendar.DATE, 1);

		return c.getTimeInMillis();
	}

	public static long firstWeekend(long dday, long startday) {
		return firstWeekend(Math.max(dday, startday));
	}

	public static long firstWeekend(long startday) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(startday);

		while (c.get(Calendar.DAY_OF_WEEK) % 6 != 1)
			c.add(Calendar.DATE, 1);

		return c.getTimeInMillis();
	}

	public static long firstDay(long dday, long startday) {
		return firstDay(Math.max(dday, startday));
	}

	public static long firstDay(long startday) {
		return ((startday + TIMEGAP) / 86400000L * 86400000L) - TIMEGAP;
	}

	public static long firstEveryweek(long dday, long startday) {
		final long week = 86400000L * 7;
		long dwg = ((dday + TIMEGAP) / 86400000L * 86400000L) % week;

		long sd = ((Math.max(dday, startday) + TIMEGAP) / 86400000L * 86400000L);
		long swg = sd % week;
		return sd + (dwg - swg >= 0 ? dwg - swg : week + (dwg - swg)) - TIMEGAP;
	}

	// d2wg d-day's 2 week gap
	// s2wg startday's 2 week gap
	// sd start day
	public static long firstEvery2week(long dday, long startday) {
		final long WEEK2 = 86400000L * 14L;
		long d2wg = ((dday + TIMEGAP) / 86400000L * 86400000L) % WEEK2;

		long sd = ((Math.max(dday, startday) + TIMEGAP) / 86400000L * 86400000L);
		long s2wg = sd % WEEK2;
		return sd + (d2wg - s2wg >= 0 ? d2wg - s2wg : WEEK2 + (d2wg - s2wg)) - TIMEGAP;
	}

	public static long firstMonth(long dday, long startday) {
		Calendar c = Calendar.getInstance();
		int y, m, d;
		c.setTimeInMillis(dday);
		d = c.get(Calendar.DATE);

		c.setTimeInMillis(Math.max(dday, startday));
		m = c.get(Calendar.MONTH);
		y = c.get(Calendar.YEAR);

		c.set(y, m, d);
		long s = c.getTimeInMillis();
		if (s < startday)
			c.add(Calendar.MONTH, 1);
		return c.getTimeInMillis();
	}

	public static long firstYear(long dday, long startday) {
		Calendar c = Calendar.getInstance();
		int y, m, d;
		c.setTimeInMillis(dday);
		m = c.get(Calendar.MONTH);
		d = c.get(Calendar.DATE);

		c.setTimeInMillis(Math.max(dday, startday));
		y = c.get(Calendar.YEAR);

		c.set(y, m, d);
		long s = c.getTimeInMillis();
		if (s < startday) {
			c.add(Calendar.YEAR, 1);
		}
		if (m != c.get(Calendar.MONTH)) {
			return c.getTimeInMillis() - 86400000L;
		}
		return c.getTimeInMillis();
	}

	public static final long TIMEGAP = +32400000L;

}
