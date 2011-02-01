/**
 * 
 */
package org.lwjgl.util.applet;

import java.util.ArrayList;
import java.util.List;

public class CacheFilter {

	Cache cache;

	public CacheFilter(Cache cache) {
		this.cache = cache;
	}

	public List<FileInfo> filterCachedFiles(List<FileInfo> files) {
		List<FileInfo> filteredFiles = new ArrayList<FileInfo>();

		for (int i = 0; i < files.size(); i++) {
			FileInfo file = files.get(i);
			if (!cache.cached(file))
				filteredFiles.add(file);
		}

		return filteredFiles;
	}

}