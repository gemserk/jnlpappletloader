package org.lwjgl.util.applet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class JarUtil {

	public List<String> getJars(String jarList) {
		ArrayList<String> jars = new ArrayList<String>();
		StringTokenizer stringTokenizer = new StringTokenizer(jarList, ", ");
		while (stringTokenizer.hasMoreElements())
			jars.add(stringTokenizer.nextToken());
		return jars;
	}

	public URL getCodeBasedUrl(URL codeBase, String jar) {
		try {
			return new URL(codeBase, jar);
		} catch (MalformedURLException e) {
			throw new RuntimeException("failed to get codebased url for " + jar, e);
		}
	}

	public List<URL> getUrls(URL codeBase, String jarList) {
		List<String> jars = getJars(jarList);
		List<URL> urls = new ArrayList<URL>();
		for (int i = 0; i < jars.size(); i++) {
			urls.add(getCodeBasedUrl(codeBase, jars.get(i)));
		}
		return urls;
	}

	public List<URL> convertToUrls(URL codebase, List<String> files) {
		List<URL> urls = new ArrayList<URL>();
		for (String file : files)
			urls.add(getCodeBasedUrl(codebase, file));
		return urls;
	}

}