package org.lwjgl.util.applet;

import java.io.File;
import java.net.URL;

public class JarDownloader {

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
		
		URL url = jarUtils.getCodeBasedUrl(codeBase, fileInfo.fileName);
		
		return new File(path + fileInfo.fileName);
	}
	
	// if pack 200 available, should try with file.jar.pack

	// if lzma available then should try first (file).lzma, where file is the result of the previous test
	
	// if gzip available then should try first (file).gz (if previous test fail)
	
	// Accept-Encoding -> "..."
	
}