/**
 * 
 */
package org.lwjgl.util.applet;

import java.net.URL;

public class UrlUtils {

	protected static String getFileName(URL url) {
		return url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
	}

}