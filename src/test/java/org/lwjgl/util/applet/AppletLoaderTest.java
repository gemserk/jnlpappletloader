package org.lwjgl.util.applet;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class AppletLoaderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	String tempDirectory = System.getProperty("java.io.tmpdir");

	@Test
	public void shouldNotExtractNativesWhenNoNewNativeJarWasDownloaded() throws Exception {

		AppletLoader appletLoader = new AppletLoader();

		appletLoader.state = AppletLoader.STATE_INIT;

		appletLoader.urlList = new URL[] { new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl.jar.pack.lzma"), new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl-natives.jar.pack.lzma") };
		appletLoader.fileSizes = new int[] { 10, -2 };

		appletLoader.extractNatives(tempDirectory + "/lwjgltest/");

		assertEquals(AppletLoader.STATE_INIT, appletLoader.state);

	}

	@Test
	public void testGetJarNameFromUrl() throws Exception {

		AppletLoader appletLoader = new AppletLoader();

		assertEquals("lwjgl.jar", appletLoader.getJarName(new URL("file:///lwjgl.jar.pack.lzma")));
		assertEquals("lwjgl.jar", appletLoader.getJarName(new URL("file:///lwjgl.jar.pack")));
		assertEquals("lwjgl.jar", appletLoader.getJarName(new URL("file:///lwjgl.jar.pack.lzma")));
		assertEquals("lwjgl.jar", appletLoader.getJarName(new URL("file:///lwjgl.jar.lzma")));
		assertEquals("applet_util.jar", appletLoader.getJarName(new URL("file:///lwjgl/applet_util.jar")));

	}
	
	@Test
	public void testGetJarInfoWhenCacheDisabled() throws Exception {

		final URLConnection urlConnection = mockery.mock(URLConnection.class);

		AppletLoader appletLoader = new AppletLoader() {

			@Override
			protected URLConnection getUrlConnection(URL url) throws IOException, ProtocolException {
				return urlConnection;
			}

		};

		appletLoader.cacheEnabled = false;

		mockery.checking(new Expectations() {
			{
				oneOf(urlConnection).getContentLength();
				will(returnValue(10));
				oneOf(urlConnection).getLastModified();
				will(returnValue(1000L));
			}
		});

		appletLoader.urlList = new URL[] { new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl.jar.pack.lzma") };

		File cacheBaseDirectory = new File(tempDirectory + "/lwjgltest/");

		assertNull(appletLoader.fileSizes);
		assertNull(appletLoader.filesLastModified);
		assertEquals(0, appletLoader.totalSizeDownload);

		appletLoader.getJarsInfo(cacheBaseDirectory);

		assertNotNull(appletLoader.fileSizes);
		assertNotNull(appletLoader.filesLastModified);
		assertEquals(10, appletLoader.fileSizes[0]);
		assertEquals(1000L, (long) appletLoader.filesLastModified.get("lwjgl.jar.pack.lzma"));
		assertEquals(10, appletLoader.totalSizeDownload);
	}
	
	@Test
	public void testGetJarInfoWhenCacheEnabledWithFileNotInCache() throws Exception {

		final URLConnection urlConnection = mockery.mock(URLConnection.class);

		AppletLoader appletLoader = new AppletLoader() {

			@Override
			protected URLConnection getUrlConnection(URL url) throws IOException, ProtocolException {
				return urlConnection;
			}

			@Override
			protected HashMap<String, Long> loadCache(File cacheBaseDirectory) throws Exception {
				return new HashMap<String, Long>();
			}

		};

		appletLoader.cacheEnabled = true;

		mockery.checking(new Expectations() {
			{
				oneOf(urlConnection).getContentLength();
				will(returnValue(10));
				oneOf(urlConnection).getLastModified();
				will(returnValue(1000L));
			}
		});

		appletLoader.urlList = new URL[] { new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl.jar.pack.lzma") };

		File cacheBaseDirectory = new File(tempDirectory + "/lwjgltest/");

		assertNull(appletLoader.fileSizes);
		assertNull(appletLoader.filesLastModified);
		assertEquals(0, appletLoader.totalSizeDownload);

		appletLoader.getJarsInfo(cacheBaseDirectory);

		assertNotNull(appletLoader.fileSizes);
		assertNotNull(appletLoader.filesLastModified);
		assertEquals(10, appletLoader.fileSizes[0]);
		assertEquals(1000L, (long) appletLoader.filesLastModified.get("lwjgl.jar.pack.lzma"));
		assertEquals(10, appletLoader.totalSizeDownload);
	}
	
	@Test
	public void testGetJarInfoWhenCacheEnabledWithFileInCacheAndModified() throws Exception {

		final URLConnection urlConnection = mockery.mock(URLConnection.class);

		AppletLoader appletLoader = new AppletLoader() {

			@Override
			protected URLConnection getUrlConnection(URL url) throws IOException, ProtocolException {
				return urlConnection;
			}

			@Override
			protected HashMap<String, Long> loadCache(File cacheBaseDirectory) throws Exception {
				return new HashMap<String, Long>() {
					{
						put("lwjgl.jar.pack.lzma", 20L);
					}
				};
			}

		};

		appletLoader.cacheEnabled = true;

		mockery.checking(new Expectations() {
			{
				oneOf(urlConnection).getContentLength();
				will(returnValue(10));
				oneOf(urlConnection).getLastModified();
				will(returnValue(100L));
			}
		});

		appletLoader.urlList = new URL[] { new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl.jar.pack.lzma") };

		File cacheBaseDirectory = new File(tempDirectory + "/lwjgltest/");

		assertNull(appletLoader.fileSizes);
		assertNull(appletLoader.filesLastModified);
		assertEquals(0, appletLoader.totalSizeDownload);

		appletLoader.getJarsInfo(cacheBaseDirectory);

		assertNotNull(appletLoader.fileSizes);
		assertNotNull(appletLoader.filesLastModified);
		assertEquals(10, appletLoader.fileSizes[0]);
		assertEquals(100L, (long) appletLoader.filesLastModified.get("lwjgl.jar.pack.lzma"));
		assertEquals(10, appletLoader.totalSizeDownload);
	}
	
	@Test
	public void testGetJarInfoWhenCacheEnabledWithFileInCacheAndNotModified() throws Exception {

		final URLConnection urlConnection = mockery.mock(URLConnection.class);

		AppletLoader appletLoader = new AppletLoader() {

			@Override
			protected URLConnection getUrlConnection(URL url) throws IOException, ProtocolException {
				return urlConnection;
			}

			@Override
			protected HashMap<String, Long> loadCache(File cacheBaseDirectory) throws Exception {
				return new HashMap<String, Long>() {
					{
						put("lwjgl.jar.pack.lzma", 100L);
					}
				};
			}

		};

		appletLoader.cacheEnabled = true;

		mockery.checking(new Expectations() {
			{
				oneOf(urlConnection).getContentLength();
				will(returnValue(10));
				oneOf(urlConnection).getLastModified();
				will(returnValue(100L));
			}
		});

		appletLoader.urlList = new URL[] { new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl.jar.pack.lzma") };

		File cacheBaseDirectory = new File(tempDirectory + "/lwjgltest/");

		assertNull(appletLoader.fileSizes);
		assertNull(appletLoader.filesLastModified);
		assertEquals(0, appletLoader.totalSizeDownload);

		appletLoader.getJarsInfo(cacheBaseDirectory);

		assertNotNull(appletLoader.fileSizes);
		assertNotNull(appletLoader.filesLastModified);
		assertEquals(-2, appletLoader.fileSizes[0]);
		assertEquals(100L, (long) appletLoader.filesLastModified.get("lwjgl.jar.pack.lzma"));
		assertEquals(0, appletLoader.totalSizeDownload);
	}

}
