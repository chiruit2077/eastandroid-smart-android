package android.util;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;

public class GPS {
	public static Location getLastKnownLocation(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT); // 정확도
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT); // 전원 소비량
		criteria.setAltitudeRequired(false); // 고도, 높이 값을 얻어 올지를 결정
		criteria.setBearingRequired(true);// provider 기본 정보(방위, 방향)
		criteria.setSpeedRequired(false); // 속도
		criteria.setCostAllowed(true); // 위치 정보를 얻어 오는데 들어가는 금전적 비용
		String provider = locationManager.getBestProvider(criteria, true);
		return getLastKnownLocation(context, provider);
	}
	public static Location getLastKnownLocation(Context context, String provider) {
		if (provider == null)
			return null;

		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.getLastKnownLocation(provider);
	}

	public static boolean isProviderEnabled(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	//android.permission.WRITE_SETTINGS
	public static void LOCATION_PROVIDERS_ALLOWED(Context context) {
		Settings.Secure.putString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, "network,gps");
	}

}
