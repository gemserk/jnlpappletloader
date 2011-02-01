package org.lwjgl.util.applet;

public class FileInfo {

	int contentLength;

	long lastModified;

	String fileName;

	public FileInfo(String fileName, int contentLength, long lastModified) {
		super();
		this.fileName = fileName;
		this.contentLength = contentLength;
		this.lastModified = lastModified;
	}
	
}