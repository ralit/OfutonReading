package org.ralit.ofutonreading;

import java.io.File;
import java.util.Comparator;

class FileSort implements Comparator<File>{

	@Override
	public int compare(File src, File target) {
		int diff = src.getName().compareTo(target.getName());
		return diff;
	}

}
