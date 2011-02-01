package org.lwjgl.util.applet;

import java.util.Map;

public class Cache {
	
	Map<String, FileInfo> cachedFiles;
	
	public Cache(Map<String, FileInfo> cachedFiles) {
		this.cachedFiles = cachedFiles;
	}

	public boolean isAlreadyDownloaded(FileInfo file) {
		FileInfo cachedEntry = cachedFiles.get(file.fileName);
		return file.equals(cachedEntry);
	}

}
