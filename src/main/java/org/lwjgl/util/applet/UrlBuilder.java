package org.lwjgl.util.applet;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Provides a way to create URLs inside a given context.
 * 
 * @author acoppes
 */
public class UrlBuilder {

	private final URL context;

	public UrlBuilder(URL context) {
		this.context = context;
	}

	/**
	 * Returns an URL using the context set.
	 * 
	 * @param url
	 *            a String with the path of the URL to build, could be relative or absolute, if absolute then context is not used.
	 * @return an URL which could be relative to context or absolute.
	 */
	public URL build(String url) {
		try {
			return new URL(context, url);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Failed to create url for " + url, e);
		}
	}

	/**
	 * Returns whether or not the url is absolute.
	 * 
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