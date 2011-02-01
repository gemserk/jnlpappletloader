package org.lwjgl.util.applet;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FileInfoProviderRemoteImpl implements FileInfoProvider {
	
	private URL codeBase;
	
	public void setCodeBase(URL codebase) {
		this.codeBase = codebase;
	}
	
	public FileInfoProviderRemoteImpl() {
		
	}
	
	public FileInfoProviderRemoteImpl(URL codeBase) {
		this.codeBase = codeBase;
	}

	public FileInfo getFileInfo(URL url) {
		try {
			URLConnection connection = getUrlConnection(url);
			return new FileInfo(UrlUtils.getFileName(url), connection.getContentLength(), connection.getLastModified());
		} catch (Exception e) {
			throw new RuntimeException("failed to retrieve file info for " + url, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.lwjgl.util.applet.FileInfoProvider#getFileInfo(java.lang.String)
	 */
	public FileInfo getFileInfo(String file) {
		try {
			return getFileInfo(new URL(codeBase, file));
		} catch (MalformedURLException e) {
			throw new RuntimeException("failed to get file info for file: " + file, e);
		}
	}

	protected URLConnection getUrlConnection(URL url) throws Exception {
		URLConnection urlconnection = url.openConnection();
		urlconnection.setDefaultUseCaches(false);
		if (urlconnection instanceof HttpURLConnection)
			((HttpURLConnection) urlconnection).setRequestMethod("HEAD");
		
		// if pack200 enabled, if gzip enabled, etc
		urlconnection.addRequestProperty("Accept-Encoding", "pack200-gzip, gzip");
		
		return urlconnection;
	}

}