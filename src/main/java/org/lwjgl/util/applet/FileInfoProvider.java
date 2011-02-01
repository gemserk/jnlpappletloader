package org.lwjgl.util.applet;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class FileInfoProvider {

	public FileInfo getFileInfo(URL url) {
		try {
			URLConnection urlconnection = getUrlConnection(url);
			return new FileInfo(UrlUtils.getFileName(url), urlconnection.getContentLength(), urlconnection.getLastModified());
		} catch (Exception e) {
			throw new RuntimeException("failed to retrieve file info for " + url, e);
		}
	}

	protected URLConnection getUrlConnection(URL url) throws Exception {
		URLConnection urlconnection = url.openConnection();
		urlconnection.setDefaultUseCaches(false);
		if (urlconnection instanceof HttpURLConnection)
			((HttpURLConnection) urlconnection).setRequestMethod("HEAD");
		return urlconnection;
	}

}