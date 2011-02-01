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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + contentLength;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + (int) (lastModified ^ (lastModified >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInfo other = (FileInfo) obj;
		if (contentLength != other.contentLength)
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (lastModified != other.lastModified)
			return false;
		return true;
	}
	
}