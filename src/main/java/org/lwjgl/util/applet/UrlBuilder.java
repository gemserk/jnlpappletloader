package org.lwjgl.util.applet;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlBuilder {

	private final URL context;

	public UrlBuilder(URL context) {
		this.context = context;
	}

	public URL build(String url) {
		try {
			return new URL(context, url);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Failed to create url for " + url, e);
		}
	}

	/**
	 * Returns whether or not the url is absolute.
	 * @param urlString
	 * @return
	 */
	public boolean isAbsolute(String urlString) {
		try {
			URL url = new URL(new URL("file:"), urlString);
			return urlString.equals(url.toString()); 
		} catch (MalformedURLException e) {
			return false;
		}
	}

}