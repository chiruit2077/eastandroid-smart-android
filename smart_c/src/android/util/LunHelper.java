package android.util;

import java.util.Calendar;

import android.miscellaneous.Log;

public class LunHelper {
	public static class LunDate {
		int year;
		int month;
		int day;
		int leapMonth;

		public LunDate(int year, int month, int day, int leapMonth) {
			this.year = year;
			this.month = month;
			this.day = day;
			this.leapMonth = leapMonth;
		}
		
		@Override
		public boolean equals(Object o) {
			if( !(o instanceof LunDate) )
				return false;
			final LunDate lun = (LunDate) o;
			
			return lun.year == this.year &&  lun.month == this.month &&  lun.day == this.day &&  lun.leapMonth == this.leapMonth; 
		}
		
		@Override
		public String toString() {
			Log.l(leapMonth == 1 ? "(윤)" : "", year, month, day);
			return (leapMonth == 1 ? "(윤)" : "") + year+"-"+ month+"-"+ day;
		}
	}

	// 음력 데이터 (평달 - 작은달 :1, 큰달:2 )
	// (윤달이 있는 달 - 평달이 작고 윤달도 작으면 :3 , 평달이 작고 윤달이 크면 : 4)
	// (윤달이 있는 달 - 평달이 크고 윤달이 작으면 :5, 평달과 윤달이 모두 크면 : 6)
	private static int mLunarTable[][] = {
			{2, 1, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2},
			{1, 2, 1, 1, 2, 1, 2, 5, 2, 2, 1, 2},
			{1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 1},   /* 1901 */
			{2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2},
			{1, 2, 1, 2, 3, 2, 1, 1, 2, 2, 1, 2},
			{2, 2, 1, 2, 1, 1, 2, 1, 1, 2, 2, 1},
			{2, 2, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2},
			{1, 2, 2, 4, 1, 2, 1, 2, 1, 2, 1, 2},
			{1, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2, 1},
			{2, 1, 1, 2, 2, 1, 2, 1, 2, 2, 1, 2},
			{1, 5, 1, 2, 1, 2, 1, 2, 2, 2, 1, 2},
			{1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 1},
			{2, 1, 2, 1, 1, 5, 1, 2, 2, 1, 2, 2},   /* 1911 */
			{2, 1, 2, 1, 1, 2, 1, 1, 2, 2, 1, 2},
			{2, 2, 1, 2, 1, 1, 2, 1, 1, 2, 1, 2},
			{2, 2, 1, 2, 5, 1, 2, 1, 2, 1, 1, 2},
			{2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2},
			{1, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2, 1},
			{2, 3, 2, 1, 2, 2, 1, 2, 2, 1, 2, 1},
			{2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 1, 2},
			{1, 2, 1, 1, 2, 1, 5, 2, 2, 1, 2, 2},
			{1, 2, 1, 1, 2, 1, 1, 2, 2, 1, 2, 2},
			{2, 1, 2, 1, 1, 2, 1, 1, 2, 1, 2, 2},   /* 1921 */
			{2, 1, 2, 2, 3, 2, 1, 1, 2, 1, 2, 2},
			{1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 1, 2},
			{2, 1, 2, 1, 2, 2, 1, 2, 1, 2, 1, 1},
			{2, 1, 2, 5, 2, 1, 2, 2, 1, 2, 1, 2},
			{1, 1, 2, 1, 2, 1, 2, 2, 1, 2, 2, 1},
			{2, 1, 1, 2, 1, 2, 1, 2, 2, 1, 2, 2},
			{1, 5, 1, 2, 1, 1, 2, 2, 1, 2, 2, 2},
			{1, 2, 1, 1, 2, 1, 1, 2, 1, 2, 2, 2},
			{1, 2, 2, 1, 1, 5, 1, 2, 1, 2, 2, 1},
			{2, 2, 2, 1, 1, 2, 1, 1, 2, 1, 2, 1},   /* 1931 */
			{2, 2, 2, 1, 2, 1, 2, 1, 1, 2, 1, 2},
			{1, 2, 2, 1, 6, 1, 2, 1, 2, 1, 1, 2},
			{1, 2, 1, 2, 2, 1, 2, 2, 1, 2, 1, 2},
			{1, 1, 2, 1, 2, 1, 2, 2, 1, 2, 2, 1},
			{2, 1, 4, 1, 2, 1, 2, 1, 2, 2, 2, 1},
			{2, 1, 1, 2, 1, 1, 2, 1, 2, 2, 2, 1},
			{2, 2, 1, 1, 2, 1, 4, 1, 2, 2, 1, 2},
			{2, 2, 1, 1, 2, 1, 1, 2, 1, 2, 1, 2},
			{2, 2, 1, 2, 1, 2, 1, 1, 2, 1, 2, 1},
			{2, 2, 1, 2, 2, 4, 1, 1, 2, 1, 2, 1},   /* 1941 */
			{2, 1, 2, 2, 1, 2, 2, 1, 2, 1, 1, 2},
			{1, 2, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2},
			{1, 1, 2, 4, 1, 2, 1, 2, 2, 1, 2, 2},
			{1, 1, 2, 1, 1, 2, 1, 2, 2, 2, 1, 2},
			{2, 1, 1, 2, 1, 1, 2, 1, 2, 2, 1, 2},
			{2, 5, 1, 2, 1, 1, 2, 1, 2, 1, 2, 2},
			{2, 1, 2, 1, 2, 1, 1, 2, 1, 2, 1, 2},
			{2, 2, 1, 2, 1, 2, 3, 2, 1, 2, 1, 2},
			{2, 1, 2, 2, 1, 2, 1, 1, 2, 1, 2, 1},
			{2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2},   /* 1951 */
			{1, 2, 1, 2, 4, 2, 1, 2, 1, 2, 1, 2},
			{1, 2, 1, 1, 2, 2, 1, 2, 2, 1, 2, 2},
			{1, 1, 2, 1, 1, 2, 1, 2, 2, 1, 2, 2},
			{2, 1, 4, 1, 1, 2, 1, 2, 1, 2, 2, 2},
			{1, 2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 2},
			{2, 1, 2, 1, 2, 1, 1, 5, 2, 1, 2, 2},
			{1, 2, 2, 1, 2, 1, 1, 2, 1, 2, 1, 2},
			{1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1},
			{2, 1, 2, 1, 2, 5, 2, 1, 2, 1, 2, 1},
			{2, 1, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2},   /* 1961 */
			{1, 2, 1, 1, 2, 1, 2, 2, 1, 2, 2, 1},
			{2, 1, 2, 3, 2, 1, 2, 1, 2, 2, 2, 1},
			{2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2},
			{1, 2, 1, 2, 1, 1, 2, 1, 1, 2, 2, 2},
			{1, 2, 5, 2, 1, 1, 2, 1, 1, 2, 2, 1},
			{2, 2, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2},
			{1, 2, 2, 1, 2, 1, 5, 2, 1, 2, 1, 2},
			{1, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2, 1},
			{2, 1, 1, 2, 2, 1, 2, 1, 2, 2, 1, 2},
			{1, 2, 1, 1, 5, 2, 1, 2, 2, 2, 1, 2},   /* 1971 */
			{1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 1},
			{2, 1, 2, 1, 1, 2, 1, 1, 2, 2, 2, 1},
			{2, 2, 1, 5, 1, 2, 1, 1, 2, 2, 1, 2},
			{2, 2, 1, 2, 1, 1, 2, 1, 1, 2, 1, 2},
			{2, 2, 1, 2, 1, 2, 1, 5, 2, 1, 1, 2},
			{2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 1},
			{2, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2, 1},
			{2, 1, 1, 2, 1, 6, 1, 2, 2, 1, 2, 1},
			{2, 1, 1, 2, 1, 2, 1, 2, 2, 1, 2, 2},
			{1, 2, 1, 1, 2, 1, 1, 2, 2, 1, 2, 2},   /* 1981 */
			{2, 1, 2, 3, 2, 1, 1, 2, 2, 1, 2, 2},
			{2, 1, 2, 1, 1, 2, 1, 1, 2, 1, 2, 2},
			{2, 1, 2, 2, 1, 1, 2, 1, 1, 5, 2, 2},
			{1, 2, 2, 1, 2, 1, 2, 1, 1, 2, 1, 2},
			{1, 2, 2, 1, 2, 2, 1, 2, 1, 2, 1, 1},
			{2, 1, 2, 2, 1, 5, 2, 2, 1, 2, 1, 2},
			{1, 1, 2, 1, 2, 1, 2, 2, 1, 2, 2, 1},
			{2, 1, 1, 2, 1, 2, 1, 2, 2, 1, 2, 2},
			{1, 2, 1, 1, 5, 1, 2, 2, 1, 2, 2, 2},
			{1, 2, 1, 1, 2, 1, 1, 2, 1, 2, 2, 2},   /* 1991 */
			{1, 2, 2, 1, 1, 2, 1, 1, 2, 1, 2, 2},
			{1, 2, 5, 2, 1, 2, 1, 1, 2, 1, 2, 1},
			{2, 2, 2, 1, 2, 1, 2, 1, 1, 2, 1, 2},
			{1, 2, 2, 1, 2, 2, 1, 5, 2, 1, 1, 2},
			{1, 2, 1, 2, 2, 1, 2, 1, 2, 2, 1, 2},
			{1, 1, 2, 1, 2, 1, 2, 2, 1, 2, 2, 1},
			{2, 1, 1, 2, 3, 2, 2, 1, 2, 2, 2, 1},
			{2, 1, 1, 2, 1, 1, 2, 1, 2, 2, 2, 1},
			{2, 2, 1, 1, 2, 1, 1, 2, 1, 2, 2, 1},
			{2, 2, 2, 3, 2, 1, 1, 2, 1, 2, 1, 2},   /* 2001 */
			{2, 2, 1, 2, 1, 2, 1, 1, 2, 1, 2, 1},
			{2, 2, 1, 2, 2, 1, 2, 1, 1, 2, 1, 2},
			{1, 5, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2},
			{1, 2, 1, 2, 1, 2, 2, 1, 2, 2, 1, 1},
			{2, 1, 2, 1, 2, 1, 5, 2, 2, 1, 2, 2},
			{1, 1, 2, 1, 1, 2, 1, 2, 2, 2, 1, 2},
			{2, 1, 1, 2, 1, 1, 2, 1, 2, 2, 1, 2},
			{2, 2, 1, 1, 5, 1, 2, 1, 2, 1, 2, 2},
			{2, 1, 2, 1, 2, 1, 1, 2, 1, 2, 1, 2},
			{2, 1, 2, 2, 1, 2, 1, 1, 2, 1, 2, 1},   /* 2011 */
			{2, 1, 6, 2, 1, 2, 1, 1, 2, 1, 2, 1},
			{2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2},
			{1, 2, 1, 2, 1, 2, 1, 2, 5, 2, 1, 2},
			{1, 2, 1, 1, 2, 1, 2, 2, 2, 1, 2, 1},
			{2, 1, 2, 1, 1, 2, 1, 2, 2, 1, 2, 2},
			{2, 1, 1, 2, 3, 2, 1, 2, 1, 2, 2, 2},
			{1, 2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 2},
			{2, 1, 2, 1, 2, 1, 1, 2, 1, 2, 1, 2},
			{2, 1, 2, 5, 2, 1, 1, 2, 1, 2, 1, 2},
			{1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1},   /* 2021 */
			{2, 1, 2, 1, 2, 2, 1, 2, 1, 2, 1, 2},
			{1, 5, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2},
			{1, 2, 1, 1, 2, 1, 2, 2, 1, 2, 2, 1},
			{2, 1, 2, 1, 1, 5, 2, 1, 2, 2, 2, 1},
			{2, 1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2},
			{1, 2, 1, 2, 1, 1, 2, 1, 1, 2, 2, 2},
			{1, 2, 2, 1, 5, 1, 2, 1, 1, 2, 2, 1},
			{2, 2, 1, 2, 2, 1, 1, 2, 1, 1, 2, 2},
			{1, 2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1},
			{2, 1, 5, 2, 1, 2, 2, 1, 2, 1, 2, 1},   /* 2031 */
			{2, 1, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2},
			{1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 5, 2},
			{1, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 1},
			{2, 1, 2, 1, 1, 2, 1, 1, 2, 2, 1, 2},
			{2, 2, 1, 2, 1, 4, 1, 1, 2, 2, 1, 2},
			{2, 2, 1, 2, 1, 1, 2, 1, 1, 2, 1, 2},
			{2, 2, 1, 2, 1, 2, 1, 2, 1, 1, 2, 1},
			{2, 2, 1, 2, 5, 2, 1, 2, 1, 2, 1, 1},
			{2, 1, 2, 2, 1, 2, 2, 1, 2, 1, 2, 1},
			{2, 1, 1, 2, 1, 2, 2, 1, 2, 2, 1, 2},   /* 2041 */
			{1, 5, 1, 2, 1, 2, 1, 2, 2, 2, 1, 2},
			{1, 2, 1, 1, 2, 1, 1, 2, 2, 1, 2, 2}
		};

	public static LunDate sol2lun(long milliseconds) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(milliseconds);
		return sol2lun(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE));
	}

	/*
	 * 양력 <. 음력 변환 함수 type : 1 - 양력 . 음력 2 - 음력 . 양력 leapmonth : 0 - 평달 1 - 윤달
	 * (type = 2 일때만 유효)
	 */
	public static LunDate sol2lun(int year, int month, int day) {
		int solYear, solMonth, solDay;
		int lunYear, lunMonth, lunDay;
		int lunLeapMonth;
		int lunMonthDay;
		int lunIndex;

		int solMonthDay[] = { 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

		/* range check */
		if (year < 1900 || year > 2040) {
			return new LunDate(0, 0, 0, 0);
		}

		/* 속도 개선을 위해 기준 일자를 여러개로 한다 */
		if (year >= 2000) {
			/* 기준일자 양력 2000년 1월 1일 (음력 1999년 11월 25일) */
			solYear = 2000;
			solMonth = 1;
			solDay = 1;
			lunYear = 1999;
			lunMonth = 11;
			lunDay = 25;
			lunLeapMonth = 0;

			solMonthDay[1] = 29; /* 2000 년 2월 28일 */
			lunMonthDay = 30; /* 1999년 11월 */
		} else if (year >= 1970) {
			/* 기준일자 양력 1970년 1월 1일 (음력 1969년 11월 24일) */
			solYear = 1970;
			solMonth = 1;
			solDay = 1;
			lunYear = 1969;
			lunMonth = 11;
			lunDay = 24;
			lunLeapMonth = 0;

			solMonthDay[1] = 28; /* 1970 년 2월 28일 */
			lunMonthDay = 30; /* 1969년 11월 */
		} else if (year >= 1940) {
			/* 기준일자 양력 1940년 1월 1일 (음력 1939년 11월 22일) */
			solYear = 1940;
			solMonth = 1;
			solDay = 1;
			lunYear = 1939;
			lunMonth = 11;
			lunDay = 22;
			lunLeapMonth = 0;

			solMonthDay[1] = 29; /* 1940 년 2월 28일 */
			lunMonthDay = 29; /* 1939년 11월 */
		} else {
			/* 기준일자 양력 1900년 1월 1일 (음력 1899년 12월 1일) */
			solYear = 1900;
			solMonth = 1;
			solDay = 1;
			lunYear = 1899;
			lunMonth = 12;
			lunDay = 1;
			lunLeapMonth = 0;

			solMonthDay[1] = 28; /* 1900 년 2월 28일 */
			lunMonthDay = 30; /* 1899년 12월 */
		}

		lunIndex = lunYear - 1899;

		while (true) {

			if (year == solYear && month == solMonth && day == solDay) {
				return new LunDate(lunYear, lunMonth, lunDay, lunLeapMonth);
			}

			/* add a day of solar calendar */
			if (solMonth == 12 && solDay == 31) {
				solYear++;
				solMonth = 1;
				solDay = 1;

				/* set monthDay of Feb */
				if (solYear % 400 == 0)
					solMonthDay[1] = 29;
				else if (solYear % 100 == 0)
					solMonthDay[1] = 28;
				else if (solYear % 4 == 0)
					solMonthDay[1] = 29;
				else
					solMonthDay[1] = 28;

			} else if (solMonthDay[solMonth - 1] == solDay) {
				solMonth++;
				solDay = 1;
			} else
				solDay++;

			/* add a day of lunar calendar */
			if (lunMonth == 12 && ((mLunarTable[lunIndex][lunMonth - 1] == 1 && lunDay == 29) || (mLunarTable[lunIndex][lunMonth - 1] == 2 && lunDay == 30))) {
				lunYear++;
				lunMonth = 1;
				lunDay = 1;

				if (lunYear > 2043) {
					return new LunDate(0, 0, 0, 0);

				}

				lunIndex = lunYear - 1899;

				if (mLunarTable[lunIndex][lunMonth - 1] == 1)
					lunMonthDay = 29;
				else if (mLunarTable[lunIndex][lunMonth - 1] == 2)
					lunMonthDay = 30;
			} else if (lunDay == lunMonthDay) {
				if (mLunarTable[lunIndex][lunMonth - 1] >= 3 && lunLeapMonth == 0) {
					lunDay = 1;
					lunLeapMonth = 1;
				} else {
					lunMonth++;
					lunDay = 1;
					lunLeapMonth = 0;
				}

				if (mLunarTable[lunIndex][lunMonth - 1] == 1)
					lunMonthDay = 29;
				else if (mLunarTable[lunIndex][lunMonth - 1] == 2)
					lunMonthDay = 30;
				else if (mLunarTable[lunIndex][lunMonth - 1] == 3)
					lunMonthDay = 29;
				else if (mLunarTable[lunIndex][lunMonth - 1] == 4 && lunLeapMonth == 0)
					lunMonthDay = 29;
				else if (mLunarTable[lunIndex][lunMonth - 1] == 4 && lunLeapMonth == 1)
					lunMonthDay = 30;
				else if (mLunarTable[lunIndex][lunMonth - 1] == 5 && lunLeapMonth == 0)
					lunMonthDay = 30;
				else if (mLunarTable[lunIndex][lunMonth - 1] == 5 && lunLeapMonth == 1)
					lunMonthDay = 29;
				else if (mLunarTable[lunIndex][lunMonth - 1] == 6)
					lunMonthDay = 30;
			} else {
				lunDay++;
			}
		}
	}
}
