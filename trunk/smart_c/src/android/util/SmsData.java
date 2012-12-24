package android.util;

import android.database.Cursor;
import android.telephony.SmsMessage;

public class SmsData {

	public int _id;
	public String address;
	public long date;
	public String body;
	public int type;

	public SmsData set(SmsMessage sms) {
		address = sms.getOriginatingAddress();
		body = sms.getDisplayMessageBody();
		date = sms.getTimestampMillis();
		return this;
	}

	public SmsData set(Cursor cursor) {
		_id = cursor.getInt(SmsProviderInfo.I._id.o());
		address = cursor.getString(SmsProviderInfo.I.address.o());
		date = cursor.getLong(SmsProviderInfo.I.date.o());
		body = cursor.getString(SmsProviderInfo.I.body.o());
		type = cursor.getInt(SmsProviderInfo.I.type.o());

		return this;
	}

	@Override
	public String toString() {
		return body + "\n" + DT.format(date, DT.yyyymmddhhmmss);
	}

	// public static void sendSms(Context context, String sender, String body) {
	// byte[] pdu = null;
	// byte[] scBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD("0000000000");
	// byte[] senderBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD(sender);
	// int lsmcs = scBytes.length;
	// byte[] dateBytes = new byte[7];
	// Calendar calendar = new GregorianCalendar();
	// dateBytes[0] = reverseByte((byte) (calendar.get(Calendar.YEAR)));
	// dateBytes[1] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));
	// dateBytes[2] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));
	// dateBytes[3] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));
	// dateBytes[4] = reverseByte((byte) (calendar.get(Calendar.MINUTE)));
	// dateBytes[5] = reverseByte((byte) (calendar.get(Calendar.SECOND)));
	// dateBytes[6] = reverseByte((byte) ((calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / (60 * 1000 * 15)));
	// try {
	// ByteArrayOutputStream bo = new ByteArrayOutputStream();
	// bo.write(lsmcs);
	// bo.write(scBytes);
	// bo.write(0x04);
	// bo.write((byte) sender.length());
	// bo.write(senderBytes);
	// bo.write(0x00);
	// bo.write(0x01); // encoding: 0 for default 7bit
	// // bo.write(0x00); // encoding: 0 for default 7bit
	// bo.write(dateBytes);
	// try {
	// // byte[] bodybytes = GsmAlphabet.stringToGsm7BitPacked(body);
	// byte[] bodybytes = body.getBytes("euc-kr");
	// bo.write(bodybytes);
	// } catch (Exception e) {
	// }
	//
	// pdu = bo.toByteArray();
	// } catch (IOException e) {
	// }
	//
	// Intent intent = new Intent();
	// // intent.setClassName("com.android.mms", "com.android.mms.transaction.SmsReceiverService");
	// intent.setAction("android.provider.Telephony.SMS_RECEIVED");
	// intent.putExtra("pdus", new Object[]{pdu});
	// context.sendBroadcast(intent);
	// }
	// private static byte reverseByte(byte b) {
	// return (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);
	// }

}
