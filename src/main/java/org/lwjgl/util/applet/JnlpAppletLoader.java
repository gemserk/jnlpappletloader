package org.lwjgl.util.applet;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JnlpAppletLoader extends Applet implements AppletStub {

	private static final long serialVersionUID = -2459790398016588477L;

	FileInfoProvider fileInfoProvider;

	JarUtil jarUtil = new JarUtil();

	JarDownloader jarDownloader;

	@Override
	public void init() {

		URL codeBase = internalGetCodeBase();

		fileInfoProvider = new FileInfoProviderRemoteImpl(codeBase);

		JarUtil jarUtils = new JarUtil();

		String tempFolder = System.getProperty("java.io.tmpdir") + File.separator + "lwjgltmp" + File.separator;

		jarDownloader = new JarDownloader(codeBase, tempFolder);
		jarDownloader.setJarUtils(jarUtils);

		// parseParameters

		// get jars info?

		// check cache and remove already downloader jars from jars to download?

		AppletParametersUtil appletParametersUtil = new AppletParametersUtil(this);

		final AppletParameters appletParameters = new AppletParametersProxy(appletParametersUtil).getAppletParameters();

		// List<URL> urls = convertToUrls(getCodeBase(), appletParameters.getJars());
		//		
		// for (URL url : urls)
		// System.out.println(url);

		List<FileInfo> jarFiles = new ArrayList<FileInfo>();

		jarFiles.addAll(getFilesInfo(appletParameters.getJars()));

		List<FileInfo> nativeFiles = new ArrayList<FileInfo>();

		List<FileInfo> files = new ArrayList<FileInfo>();

		files.addAll(jarFiles);
		files.addAll(nativeFiles);

		Cache cache = new Cache(new HashMap<String, FileInfo>());
		CacheFilter cacheFilter = new CacheFilter(cache);

		List<FileInfo> newFiles = cacheFilter.removeCachedFiles(files);

		for (FileInfo fileInfo : newFiles) {
			// update progress based on file info content length?
			System.out.println("Downloading " + fileInfo.getFileName() + " [" + fileInfo.getContentLength() + "]" + "...");
			jarDownloader.download(fileInfo);
		}

		// download jars

		// extract jars

		// validate jars?

		// update cache?

		// another stuff

		// switch applet

		URL[] urls = getLocalJarUrls(tempFolder, files);

		ClassLoader classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());

		switchApplet(classLoader, appletParameters);
	}

	private URL[] getLocalJarUrls(String tempFolder, List<FileInfo> files) {
		try {
			URL[] urls = new URL[files.size()];
			for (int i = 0; i < files.size(); i++)
				urls[i] = new URL("file:" + tempFolder + files.get(i).getFileName());
			return urls;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private URL internalGetCodeBase() {
		String codeBase = getParameter("al_codebase");
		if (codeBase == null)
			return this.getCodeBase();
		try {
			return new URL(codeBase);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<FileInfo> getFilesInfo(List<String> urls) {
		List<FileInfo> files = new ArrayList<FileInfo>();
		for (int i = 0; i < urls.size(); i++)
			files.add(fileInfoProvider.getFileInfo(urls.get(i)));
		return files;
	}

	private void switchApplet(final ClassLoader classLoader, final AppletParameters appletParameters) {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					switchApplet(classLoader, appletParameters.getMain());
					repaint();
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * replace the current applet with the lwjgl applet using AppletStub and initialise and start it
	 * 
	 * @param appletClassName
	 *            the applet class name to be loaded.
	 */
	@SuppressWarnings("unchecked")
	protected void switchApplet(ClassLoader classLoader, String appletClassName) {
		try {
			Class appletClass = classLoader.loadClass(appletClassName);
			Applet applet = (Applet) appletClass.newInstance();

			applet.setStub(this);
			applet.setSize(getWidth(), getHeight());

			setLayout(new BorderLayout());
			add(applet);
			validate();

			applet.init();

			applet.start();
		} catch (Exception e) {
			throw new RuntimeException("switch applet failed", e);
		}
	}

	@Override
	public void appletResize(int width, int height) {
		resize(width, height);
	}

}