package android.util;

import android.net.Uri;
import android.os.Build;

public class SmsProviderInfo {

	public enum I {
		_id, address, date, body, type;
		public int o() {
			return ordinal();
		}
		public String s() {
			return name();
		}
	}

	public static class Info {
		public Uri CONTENT_URI = Uri.parse("content://sms");
		public String[] projection = new String[]{I._id.s(), I.address.s(), I.date.s(), I.body.s(), I.type.s()};
		public String where = "type = 1";
		public String order = "desc _id";
	}

	class Defaultnfo extends Info {
		Defaultnfo() {
			CONTENT_URI = Uri.parse("content://sms");
			projection = new String[]{I._id.s(), I.address.s(), I.date.s(), I.body.s(), I.type.s()};
			where = "type = 1";
			order = "date desc";
		}
	}

	class SecInfo extends Info {
		SecInfo() {
			CONTENT_URI = Uri.parse("content://com.sec.mms.provider/message");
			projection = new String[]{"RootID " + I._id.s(), "MDN1st " + I.address.s(), "RegTime " + I.date.s(), "Title " + I.body.s(), "Status " + I.type.s()};
			where = "type = 1101 or type = 1100";
			order = "date desc";
		}
	}

	public Info getInfo() {
		String model = Build.MODEL;
		int sdk = Build.VERSION.SDK_INT;
		if (model.contains("SHW-M250L") && sdk >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return new SecInfo();
		}
		return new Defaultnfo();
	}

	// 05-05 02:18:02.165: I/System.out(12924): 1983 {
	// 05-05 02:18:02.165: I/System.out(12924): RootID=2165
	// 05-05 02:18:02.165: I/System.out(12924): RegTime=1335944668000
	// 05-05 02:18:02.165: I/System.out(12924): MainType=0
	// 05-05 02:18:02.165: I/System.out(12924): SubType=0
	// 05-05 02:18:02.165: I/System.out(12924): DetailType=0
	// 05-05 02:18:02.165: I/System.out(12924): Status=1101
	// 05-05 02:18:02.165: I/System.out(12924): MDN1st=01093959350
	// 05-05 02:18:02.165: I/System.out(12924): MDN2nd=01093959350
	// 05-05 02:18:02.165: I/System.out(12924): Display=010-9395-9350
	// 05-05 02:18:02.165: I/System.out(12924): Chosung=010-9395-9350
	// 05-05 02:18:02.165: I/System.out(12924): Title=하나SK카드(8*5*)이*한님 04/24 21:21 할부/3개월 101,000원/승인/ 김스시
	// 05-05 02:18:02.165: I/System.out(12924): CallbackURL=
	// 05-05 02:18:02.165: I/System.out(12924): TID=0
	// 05-05 02:18:02.165: I/System.out(12924): CommonID=
	// 05-05 02:18:02.165: I/System.out(12924): AID=
	// 05-05 02:18:02.165: I/System.out(12924): AppSpecificData=
	// 05-05 02:18:02.165: I/System.out(12924): RawSmsPdu=<unprintable>
	// 05-05 02:18:02.165: I/System.out(12924): ReservedData1=null
	// 05-05 02:18:02.165: I/System.out(12924): MessageBody=
	// 05-05 02:18:02.165: I/System.out(12924): ReservedData2=null
	// 05-05 02:18:02.165: I/System.out(12924): }

}
