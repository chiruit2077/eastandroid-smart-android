package android.util;

import java.util.ArrayList;

public class TreeHelper {

	private TreeHelper mParent;
	private ArrayList<TreeHelper> mChilds = new ArrayList<TreeHelper>();

	public TreeHelper getRoot() {
		return isRoot() ? this : mParent.getRoot();
	}

	public int getChildCount() {
		return mChilds.size();
	}

	public ArrayList<TreeHelper> getChilds() {
		return mChilds;
	}

	public void addChild(TreeHelper object) {
		if (!allowChild())
			return;
		mChilds.add(object);
	}

	public void addChild(int index, TreeHelper object) {
		if (!allowChild())
			return;
		mChilds.add(index, object);
	}

	public boolean removeChild(TreeHelper object) {
		return mChilds.remove(object);
	}

	public TreeHelper removeChildAt(int index) {
		return mChilds.remove(index);
	}

	public TreeHelper childAt(int index) {
		return mChilds.get(index);
	}

	public boolean isRoot() {
		return mParent == null;
	}

	public TreeHelper getParent() {
		return mParent;
	}

	public void setParent(TreeHelper parent) {
		mParent = parent;
	}

	public boolean allowChild() {
		return true;
	}

}