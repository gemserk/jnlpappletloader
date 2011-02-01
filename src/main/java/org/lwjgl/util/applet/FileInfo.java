package org.lwjgl.util.applet;

public class FileInfo {

	int contentLength;

	long lastModified;

	private String fileName;

	public FileInfo(String fileName, int contentLength, long lastModified) {
		super();
		this.setFileName(fileName);
		this.contentLength = contentLength;
		this.lastModified = lastModified;
	}
	
	public String getNameWithoutExtension() {
		return getFileName().substring(0, getFileName().indexOf('.'));
	}

	void setFileName(String fileName) {
		this.fileName = fileName;
	}

	String getFileName() {
		return fileName;
	}
	
}