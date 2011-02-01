package org.lwjgl.util.applet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Cache implements Serializable {

	private static final long serialVersionUID = 1527888906179597800L;

	private Map<String, FileInfo> cachedFiles = new HashMap<String, FileInfo>();

	public boolean isAlreadyDownloaded(FileInfo file) {
		FileInfo cachedEntry = cachedFiles.get(file.getFileName());
		if (cachedEntry == null)
			return false;
		return file.getLastModified() == cachedEntry.getLastModified();
	}

	public void add(FileInfo fileInfo) {
		cachedFiles.put(fileInfo.getFileName(), fileInfo);
	}

	public static Cache load(File file) {
		try {
			ObjectInputStream dis = new ObjectInputStream(new FileInputStream(file));
			Cache cache = (Cache) dis.readObject();
			dis.close();
			return cache;
		} catch (FileNotFoundException e) {
			// logger cache file not found
			return new Cache();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void save(Cache cache, File file) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(cache);
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
