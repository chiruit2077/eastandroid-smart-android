package android.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import android.miscellaneous.Log;
import android.net.Uri;

public class GCMSend {

	public static String JSON_DATA = "{\"registration_ids\" : [%s],\"data\" : {%s},}";
	public static String senderData(ArrayList<String> regIds, Map<String, String> map) {
		StringBuffer data_param = new StringBuffer();
		final Set<String> keys = map.keySet();
		for (String key : keys) {
			data_param.append("\"" + key.replace("\"", "\\\"") + "\":\"" + map.get(key).replace("\"", "\\\"") + "\",");
		}

		StringBuffer regids_param = new StringBuffer();
		for (String regId : regIds) {
			regids_param.append("\"" + Uri.encode(regId) + "\",");
		}

		Log.l(String.format(JSON_DATA, regids_param.toString(), data_param.toString()));
		return String.format(JSON_DATA, regids_param.toString(), data_param.toString());
	}

	public static void sender(final String API_KEY, ArrayList<String> regIds, Map<String, String> map) {
		sender(API_KEY, senderData(regIds, map));
	}
	public static void sender(final String API_KEY, final String data) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.l();
				try {
					byte[] postData = data.getBytes("UTF8");

					URL url = new URL("https://android.googleapis.com/gcm/send");
					HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
					httpURLConnection.setDoOutput(true);
					httpURLConnection.setUseCaches(false);
					httpURLConnection.setRequestMethod("POST");
					httpURLConnection.setRequestProperty("Content-Type", "application/json");
					// conn.setRequestProperty("Content-Type", "charset=UTF-8");
					httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
					httpURLConnection.setRequestProperty("Authorization", "key=" + API_KEY);

					OutputStream out = httpURLConnection.getOutputStream();
					out.write(postData);
					out.close();

					int responseCode = httpURLConnection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {
						Log.l("http ok : ", httpURLConnection.getResponseMessage(), responseCode);
					} else {
						Log.l("!http error : ", httpURLConnection.getResponseMessage(), responseCode);
					}

					httpURLConnection.getInputStream();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
