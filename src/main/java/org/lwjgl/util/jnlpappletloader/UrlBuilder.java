/**
 * 
 */
package org.lwjgl.util.jnlpappletloader;

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

}