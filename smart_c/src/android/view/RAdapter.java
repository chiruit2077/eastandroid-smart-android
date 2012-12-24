package android.view;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

/**
 * @author djrain
 * 
 * @param <T>
 */
public abstract class RAdapter<T> extends BaseAdapter {
	LayoutInflater inflater;
	final List<T> arrays;
	private int resid;

	public RAdapter(Context mContext, int resid, final List<T> arrays) {
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.arrays = arrays;
		this.resid = resid;
	}

	@Override
	public int getCount() {
		if (arrays == null)
			return 0;
		return arrays.size();
	}

	@Override
	public Object getItem(int position) {
		if (arrays != null)
			return arrays.get(position);

		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(resid, parent, false);
		}

		getView(arrays.get(position), convertView);

		return convertView;
	}

	public abstract void getView(T object, View convertView);
}