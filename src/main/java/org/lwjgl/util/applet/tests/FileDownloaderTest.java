package org.lwjgl.util.applet.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;

import org.lwjgl.util.applet.FileDownloader;

public class FileDownloaderTest {

	static String tempDirectory = System.getProperty("java.io.tmpdir");

	static String pack200GZipEncoding = "pack200-gzip";

	static String gzipEncoding = "gzip";

	static String jarExtension = ".jar";

	static String pack200Extension = ".jar.pack";

	public static void main(String[] args) throws Exception {
		
		URL codeBase = new URL("http://acoppes-laptop.local/prototipos/discoverthename-test/");

		 String jarName = "jinput-2.6.0-GEMSERK-natives-linux";
//		String jarName = "animation-0.0.2-SNAPSHOT";

		URL url = new URL(codeBase, jarName + jarExtension);

		URLConnection connection = url.openConnection();

		connection.addRequestProperty("Accept-Encoding", "pack200-gzip, gzip");
		connection.connect();

		String path = tempDirectory + File.separator + "jardownloadertest" + File.separator;

		File jarFile = new File(path + jarName + jarExtension);

		String contentEncoding = connection.getContentEncoding();

		System.out.println(contentEncoding);

		boolean isPack200Encoding = pack200GZipEncoding.equals(contentEncoding);
		boolean isGZipEncoding = gzipEncoding.equals(contentEncoding);
		
		String downloadExtension;
		
		if (isPack200Encoding) 
			downloadExtension = pack200Extension;
		else
			downloadExtension = jarExtension;

		String filePath = path + jarName + downloadExtension;

		FileDownloader fileDownloader = new FileDownloader();

		new File(path).mkdirs();

		File file = new File(filePath);

		if (!file.exists())
			file.createNewFile();

		InputStream inputStream = connection.getInputStream();
		
		if (isPack200Encoding || isGZipEncoding)
			inputStream = new GZIPInputStream(inputStream);

		fileDownloader.download(inputStream, new FileOutputStream(file));
		
		if (!isPack200Encoding)
			return;
		
		JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile));
		
		Pack200.newUnpacker().unpack(file, out);
		file.delete();
		
		out.close();

	}

}
