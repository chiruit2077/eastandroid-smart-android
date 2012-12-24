package android.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.common.BaseFile;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.ShareHelper;
import android.util.SparseBooleanArray;
import android.util.UT;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class List2Items extends ListView implements OnItemLongClickListener {
	private static final int MAINF2ITEMS_EDIT = 0;
	private static final int MAINF2ITEMS_NEW = 1;
	private static final String POSITION = "position";
	private static final String KEY = "key";
	private static final String VALUE = "value";
	private static final String SCHEME = "http";
	private static final String AUTHORITY = ".eastandroid.com";

	private ListView mList;
	private BaseAdapter mAdapter;
	private ArrayList<Data> mListItem = new ArrayList<Data>();
	private LayoutInflater mInflater;
	private String mLastSaved;

	private Context mContext;
	private Activity activity;

	public List2Items(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public List2Items(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public List2Items(Context context) {
		super(context);
		mContext = context;

	}

	public static class Data implements Serializable {
		private static final long serialVersionUID = -6740621815511406382L;
		public String key;
		public String value;

		public Uri getEditUri() {
			return new Uri.Builder()//
					.scheme(SCHEME)//
					.authority(getClass().getSimpleName() + AUTHORITY)//
					.appendQueryParameter(KEY, key)//
					.appendQueryParameter(VALUE, value)//
					.build();

		}
		public Data set(String key, String value) {
			this.key = key;
			this.value = value;
			return this;
		}
	}

	@Override
	protected void onFinishInflate() {

		super.onFinishInflate();
		mInflater = LayoutInflater.from(mContext);
		mList = this;
		mAdapter = new Data2ItemAdapter();
		mList.setAdapter(mAdapter);
		mList.setOnItemLongClickListener(this);

		load(mLastSaved);

	}
	protected void load(String pathfile) {
		try {
			ArrayList<Data> target = null;
			target = BaseFile.load(pathfile, target);
			if (target != null) {
				mListItem = target;
				reload();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void reload() {
		if (mAdapter.isEmpty())
			mAdapter.notifyDataSetInvalidated();
		else
			mAdapter.notifyDataSetChanged();

		save(mLastSaved);
	}

	protected void save(String pathfile) {
		BaseFile.save(pathfile, mListItem);
	}

	void shareto() {
		try {
			SparseBooleanArray positions = mList.getCheckedItemPositions();
			ArrayList<Data> selected = new ArrayList<Data>();
			for (int i = 0; i < positions.size(); i++) {
				int position = positions.keyAt(i);
				if (positions.get(position)) {
					selected.add(mListItem.get(position));
				}
			}
			if (selected.size() <= 0) {
				selected = mListItem;
			}

			StringBuilder sb = new StringBuilder();
			for (Data target : selected) {
				sb.append(target.getEditUri().toString());
				sb.append("\r\n");
				sb.append("\r\n");
			}

			Intent intent = ShareHelper.getIntentTextShare(sb.toString());
			ShareHelper.shareTo(mContext, intent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Data target = (Data) parent.getItemAtPosition(position);
		Intent intent = new Intent(Intent.ACTION_EDIT, target.getEditUri());
		intent.putExtra(POSITION, position);
		activity.startActivityForResult(intent, MAINF2ITEMS_EDIT);
		return true;
	}
	public void delete() {
		SparseBooleanArray positions = mList.getCheckedItemPositions();
		Set<Object> remove = new HashSet<Object>();
		for (int i = 0; i < positions.size(); i++) {
			int position = positions.keyAt(i);
			if (positions.get(position)) {
				remove.add(mListItem.get(position));
			}
		}
		mList.clearChoices();
		mListItem.removeAll(remove);
		reload();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		String key = data.getStringExtra(KEY);
		String value = data.getStringExtra(VALUE);

		switch (requestCode) {
			case MAINF2ITEMS_NEW :
				mListItem.add(new Data().set(key, value));
				reload();
			case MAINF2ITEMS_EDIT :
				int position = data.getIntExtra(POSITION, -1);
				if (position == -1)
					return;
				mListItem.get(position).set(key, value);
				reload();
				break;
			default :
				break;
		}
	}

	public class Data2ItemAdapter extends BaseAdapter {
		public class Holder {
			public TextView key;
			public TextView value;

			public View set(View v) {
				key = (TextView) v.findViewById(android.R.id.text1);
				value = (TextView) v.findViewById(android.R.id.text2);
				v.setTag(this);
				return v;
			}
		}

		@Override
		public int getCount() {
			return mListItem.size();
		}

		@Override
		public Object getItem(int position) {
			return mListItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				if (UT.isOverHoneycomb()) {
					convertView = new Holder().set(mInflater.inflate(android.R.layout.simple_list_item_activated_2, parent, false));
				} else {
					convertView = new Holder().set(mInflater.inflate(com.smart_c.R.layout.smc_simple_list_item_2_checked, parent, false));
				}
			}
			try {
				final Holder h = (Holder) convertView.getTag();
				final Data d = mListItem.get(position);

				h.key.setText(d.key);
				h.value.setText(d.value);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}
}
