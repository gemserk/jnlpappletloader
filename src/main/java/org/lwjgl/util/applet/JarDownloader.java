package org.lwjgl.util.applet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

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

	public static class Capabilities {

		private boolean pack200Enabled;

		private boolean gZipEnabled;

		private String acceptEncoding;

		public Capabilities(boolean pack200Enabled, boolean gZipEnabled) {
			this.pack200Enabled = pack200Enabled;
			this.gZipEnabled = gZipEnabled;
		}

		public boolean isPack200Enabled() {
			return pack200Enabled;
		}

		public void setPack200Enabled(boolean pack200Enabled) {
			this.pack200Enabled = pack200Enabled;
		}

		public boolean isGZipEnabled() {
			return gZipEnabled;
		}

		public void setGZipEnabled(boolean gZipEnabled) {
			this.gZipEnabled = gZipEnabled;
		}

		public String getAcceptEncoding() {
			return acceptEncoding;
		}

		public void setAcceptEncoding(String acceptEncoding) {
			this.acceptEncoding = acceptEncoding;
		}

	}

	public static class UrlConnectionBuilder {

		public URLConnection openConnection(URL url, String acceptedEncoding) {
			try {
				URLConnection connection = url.openConnection();
				connection.addRequestProperty("Accept-Encoding", acceptedEncoding);
				connection.connect();
				return connection;
			} catch (IOException e) {
				throw new RuntimeException("failed to establish connection with " + url, e);
			}
		}

	}

	public static class FileOutputStreamBuilder {

		public OutputStream getFileOutputStream(File file) {
			try {
				return new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("failed to get output stream for file " + file, e);
			}
		}

	}

	Capabilities capabilities = new Capabilities(true, true);

	UrlConnectionBuilder urlConnectionBuilder = new UrlConnectionBuilder();

	FileUtils fileUtils = new FileUtils();

	FileOutputStreamBuilder fileOutputStreamBuilder = new FileOutputStreamBuilder();

	public void setFileDownloader(FileUtils fileUtils) {
		this.fileUtils = fileUtils;
	}

	public void setUrlConnectionBuilder(UrlConnectionBuilder urlConnectionBuilder) {
		this.urlConnectionBuilder = urlConnectionBuilder;
	}

	public void setFileOutputStreamBuilder(FileOutputStreamBuilder fileOutputStreamBuilder) {
		this.fileOutputStreamBuilder = fileOutputStreamBuilder;
	}

	public void setCapabilities(Capabilities capabilities) {
		this.capabilities = capabilities;
	}

	private File internalDownload(FileInfo fileInfo) throws IOException, FileNotFoundException {
		URL url = jarUtils.getCodeBasedUrl(codeBase, fileInfo.getFileName());

		String jarName = fileInfo.getNameWithoutExtension();

		File jarFile = new File(path + jarName + jarExtension);

		String acceptEncoding = getAcceptEncoding(capabilities);

		System.out.println("Accept-Encoding: " + acceptEncoding);

		URLConnection connection = urlConnectionBuilder.openConnection(url, acceptEncoding);

		String contentEncoding = connection.getContentEncoding();

		System.out.println("Content-Encoding: " + contentEncoding);

		boolean isPack200Encoding = pack200GZipEncoding.equals(contentEncoding);
		boolean isGZipEncoding = gzipEncoding.equals(contentEncoding);

		String downloadExtension;

		if (isPack200Encoding)
			downloadExtension = pack200Extension;
		else
			downloadExtension = jarExtension;

		String filePath = path + jarName + downloadExtension;

		new File(path).mkdirs();

		File file = new File(filePath);

		if (!file.exists())
			file.createNewFile();

		InputStream inputStream = connection.getInputStream();

		if (isPack200Encoding || isGZipEncoding)
			fileUtils.unzip(inputStream, fileOutputStreamBuilder.getFileOutputStream(file));
		else
			fileUtils.copy(inputStream, fileOutputStreamBuilder.getFileOutputStream(file));

		if (!isPack200Encoding)
			return file;

		JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile));

		Pack200.newUnpacker().unpack(file, out);
		file.delete();

		out.close();

		return new File(path + fileInfo.getFileName());
	}

	private String getAcceptEncoding(Capabilities capabilities) {
		StringBuilder acceptEncodingStringBuilder = new StringBuilder("");

		if (capabilities.isPack200Enabled())
			appendAcceptedEncoding(acceptEncodingStringBuilder, "pack200-gzip");

		if (capabilities.isGZipEnabled())
			appendAcceptedEncoding(acceptEncodingStringBuilder, "gzip");

		return acceptEncodingStringBuilder.toString();
	}

	private void appendAcceptedEncoding(StringBuilder sb, String encoding) {
		if (sb.length() != 0)
			sb.append(", ");
		sb.append(encoding);
	}

	// if pack 200 available, should try with file.jar.pack

	// if lzma available then should try first (file).lzma, where file is the result of the previous test

	// if gzip available then should try first (file).gz (if previous test fail)

	// Accept-Encoding -> "..."

}