package android.util;

import java.util.ArrayList;
import java.util.Collections;

import android.miscellaneous.Log;

/**
 * @FileName OrderHelper.java
 * @Date 2012. 4. 13.
 * @Author Finger
 * @E_Mail
 * @COPYRIGHT 2008 FINGER,INC. ALL RIGHTS RESERVED.
 * @DESCRIPTION 리스트의 순서 변경 데이터 관리
 */
/**
 * @author djrain
 * 
 */
public class OrderHelper {

	public static interface OrderInterface {
		void fullOrder(ArrayList<Integer> src, ArrayList<Integer> des, ArrayList<Integer> recheck);

		int getCount();
	}

	public static void fullUpOrding(SparseBooleanArray sba, OrderInterface adapter) {
		// check
		ArrayList<Integer> positions = filterChecked(sba);

		ArrayList<Integer> src = new ArrayList<Integer>();
		ArrayList<Integer> des = new ArrayList<Integer>();
		ArrayList<Integer> recheck = new ArrayList<Integer>();
		ordering(positions, src, des, recheck);

		adapter.fullOrder(src, des, recheck);
	}

	public static void fullDownOrding(SparseBooleanArray sba, OrderInterface adapter) {
		// check
		ArrayList<Integer> positions = filterChecked(sba);

		positions = rpositions(positions, adapter.getCount());
		Collections.reverse(positions);

		ArrayList<Integer> src = new ArrayList<Integer>();
		ArrayList<Integer> des = new ArrayList<Integer>();
		ArrayList<Integer> recheck = new ArrayList<Integer>();
		ordering(positions, src, des, recheck);

		src = rpositions(src, adapter.getCount());
		des = rpositions(des, adapter.getCount());
		recheck = rpositions(recheck, adapter.getCount());

		adapter.fullOrder(src, des, recheck);
	}

	private static void ordering(ArrayList<Integer> positions, ArrayList<Integer> src, ArrayList<Integer> des, ArrayList<Integer> recheck) {
		// 초기패스 위치
		ArrayList<Integer> pass = new ArrayList<Integer>();
		int N = positions.size();
		for (int i = 0; i < N; i++) {
			if (positions.get(i) != i)
				break;
			pass.add(positions.get(i));
		}
		Log.l(pass);

		// 이동항목에서 제외
		positions.removeAll(pass);
		Log.l(positions);

		// 원본

		for (Integer position : positions) {
			if (src.size() == 0 || src.get(src.size() - 1) < position - 1)
				src.add(position - 1);
			src.add(position);
		}
		Log.l(src);

		// 결과

		des.addAll(src);

		recheck.addAll(pass);
		for (Integer position : positions) {
			int s = Collections.binarySearch(des, position);
			Collections.swap(des, s - 1, s);
			recheck.add(position - 1);
		}
		Log.l(des);
		// checked
		Log.l(recheck);

	}

	public static ArrayList<Integer> rpositions(ArrayList<Integer> positions, int N) {
		ArrayList<Integer> rpositions = new ArrayList<Integer>();

		for (Integer position : positions) {
			rpositions.add(N - 1 - position);
		}

		return rpositions;
	}

	public static ArrayList<Integer> filterChecked(final SparseBooleanArray sba) {
		// check
		int N = sba.size();
		ArrayList<Integer> positions = new ArrayList<Integer>();
		for (int i = 0; i < N; i++) {
			if (sba.valueAt(i)) {
				positions.add(sba.keyAt(i));
			}
		}
		Log.l(positions);
		return positions;
	}
}