package org.lwjgl.util.applet;

import java.util.ArrayList;
import java.util.List;

public class CacheFilter {

	private Cache cache;

	public CacheFilter(Cache cache) {
		this.cache = cache;
	}

	public List<FileInfo> removeCachedFiles(List<FileInfo> files) {
		List<FileInfo> filteredFiles = new ArrayList<FileInfo>();

		for (int i = 0; i < files.size(); i++) {
			FileInfo file = files.get(i);
			if (cache.isAlreadyDownloaded(file))
				continue;
			filteredFiles.add(file);
		}

		return filteredFiles;
	}

}