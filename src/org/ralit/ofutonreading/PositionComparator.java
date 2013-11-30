package org.ralit.ofutonreading;

import java.util.ArrayList;
import java.util.Comparator;

public class PositionComparator implements Comparator<ArrayList<Integer>> {

	@Override
	public int compare(ArrayList<Integer> lhs, ArrayList<Integer> rhs) {
		return lhs.get(1) - rhs.get(1);
	}

}
