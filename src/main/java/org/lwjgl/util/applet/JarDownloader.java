package org.lwjgl.util.applet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;

public class JarDownloader {

	static String tempDirectory = System.getProperty("java.io.tmpdir");

	static String pack200GZipEncoding = "pack200-gzip";

	static String gzipEncoding = "gzip";

	static String jarExtension = ".jar";

	static String pack200Extension = ".jar.pack";

	private final String path;

	private final URL codeBase;

	private JarUtil jarUtils;

	public void setJarUtils(JarUtil jarUtils) {
		this.jarUtils = jarUtils;
	}

	public JarDownloader(URL codeBase, String path) {
		this.codeBase = codeBase;
		this.path = path;
	}

	public File download(FileInfo fileInfo) {
		try {
			return internalDownload(fileInfo);
		} catch (Exception e) {
			throw new RuntimeException("failed to download file " + fileInfo.getFileName(), e);
		}
	}

	private File internalDownload(FileInfo fileInfo) throws IOException, FileNotFoundException {
		URL url = jarUtils.getCodeBasedUrl(codeBase, fileInfo.getFileName());

		String jarName = fileInfo.getNameWithoutExtension();

		File jarFile = new File(path + jarName + jarExtension);

		URLConnection connection = url.openConnection();
		
		connection.addRequestProperty("Accept-Encoding", "pack200-gzip, gzip");
		connection.connect();

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
			return file;

		JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile));

		Pack200.newUnpacker().unpack(file, out);
		file.delete();

		out.close();

		return new File(path + fileInfo.getFileName());
	}

	// if pack 200 available, should try with file.jar.pack

	// if lzma available then should try first (file).lzma, where file is the result of the previous test

	// if gzip available then should try first (file).gz (if previous test fail)

	// Accept-Encoding -> "..."

}