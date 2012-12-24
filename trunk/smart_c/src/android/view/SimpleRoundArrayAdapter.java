package android.view;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class SimpleRoundArrayAdapter<T> extends ArrayAdapter<T> {
	private int c;
	private int t;
	private int b;
	private int s;

	public SimpleRoundArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public SimpleRoundArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public SimpleRoundArrayAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public SimpleRoundArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
		super(context, textViewResourceId, objects);
	}

	public SimpleRoundArrayAdapter(Context context, int textViewResourceId, T[] objects) {
		super(context, textViewResourceId, objects);
	}

	public SimpleRoundArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public void setCTBS(int c, int t, int b, int s) {
		this.c = c;
		this.t = t;
		this.b = b;
		this.s = s;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);

		if (getCount() > 1 && position != 0 && getCount() - 1 != position)
			convertView.setBackgroundResource(c);
		else if (getCount() > 1 && position == 0)
			convertView.setBackgroundResource(t);
		else if (getCount() > 1 && getCount() - 1 == position)
			convertView.setBackgroundResource(b);
		else if (getCount() == 1)
			convertView.setBackgroundResource(s);

		return convertView;
	}

}
