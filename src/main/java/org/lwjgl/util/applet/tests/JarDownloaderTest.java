package org.lwjgl.util.applet.tests;

import java.io.File;
import java.net.URL;

import org.lwjgl.util.applet.FileInfo;
import org.lwjgl.util.applet.JarDownloader;
import org.lwjgl.util.applet.JarUtil;

public class JarDownloaderTest {

	static String tempDirectory = System.getProperty("java.io.tmpdir");

	public static void main(String[] args) throws Exception {
		
		URL codeBase = new URL("http://acoppes-laptop.local/prototipos/discoverthename-test/");
		
		String downloadPath = tempDirectory + File.separator + "jardownloadertest" + File.separator;

		JarDownloader jarDownloader = new JarDownloader(codeBase, downloadPath);
		jarDownloader.setJarUtils(new JarUtil());
		
		jarDownloader.download(new FileInfo("lwjgl-2.6.0-GEMSERK.jar", 0, 0));
		jarDownloader.download(new FileInfo("slick-274.jar", 0, 0));
		
	}

}

