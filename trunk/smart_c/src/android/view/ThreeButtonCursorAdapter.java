package android.view;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;

public abstract class ThreeButtonCursorAdapter extends CursorAdapter {

	

	public ThreeButtonCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	public ThreeButtonCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}


}
