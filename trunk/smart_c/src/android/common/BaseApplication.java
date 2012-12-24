package android.common;

import android.app.Application;
import android.content.Context;
import android.miscellaneous.E;
import android.widget.Toast;

/**
 * @author djrain
 * 
 */
public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		BaseP.c().init(getBaseContext());
		BaseV.INIT(getBaseContext());
		BaseFile.CREATE(getBaseContext().getPackageName());
		displayInfo();
	}
	@Override
	public void setTheme(int resid) {
		super.setTheme(resid);
	}

	public void displayInfo() {
		if (!E.LOG)
			return;

		Context baseContext = getBaseContext();
		float density = baseContext.getResources().getDisplayMetrics().density;
		int widthPixels = baseContext.getResources().getDisplayMetrics().widthPixels;
		int heightPixels = baseContext.getResources().getDisplayMetrics().heightPixels;
		String msg = baseContext.getPackageName() + "\nDENSITY : " + density + "\n"//
				+ "SCREEN : " + widthPixels + "X" + heightPixels;

		Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show();
	}

}
