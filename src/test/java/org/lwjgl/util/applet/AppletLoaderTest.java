package org.lwjgl.util.applet;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lwjgl.util.applet.AppletLoader.JarInfo;
import org.lwjgl.util.applet.AppletLoader.JarProvider;

@RunWith(JMock.class)
public class AppletLoaderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	String tempDirectory = System.getProperty("java.io.tmpdir");

	// @Test
	// public void shouldNotExtractNativesWhenNoNewNativeJarWasDownloaded() throws Exception {
	//
	// AppletLoader appletLoader = new AppletLoader();
	//
	// appletLoader.state = AppletLoader.STATE_INIT;
	//
	// appletLoader.urlList = new URL[] { new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl.jar.pack.lzma"), new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl-natives.jar.pack.lzma") };
	// appletLoader.fileSizes = new int[] { 10, -2 };
	//
	// appletLoader.extractNatives(tempDirectory + "/lwjgltest/");
	//
	// assertEquals(AppletLoader.STATE_INIT, appletLoader.state);
	//
	// }

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

		final JarProvider jarProvider = mockery.mock(JarProvider.class);

		final AppletLoader appletLoader = new AppletLoader();
		appletLoader.setJarProvider(jarProvider);

		appletLoader.cacheEnabled = false;

		appletLoader.urlList = new URL[] { new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl.jar.pack.lzma") };

		mockery.checking(new Expectations() {
			{
				oneOf(jarProvider).getJarInfo(appletLoader.urlList[0]);
				will(returnValue(new JarInfo("lwjgl.jar.pack.lzma", 10, 1000L)));
			}
		});

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

		final JarProvider jarProvider = mockery.mock(JarProvider.class);

		final AppletLoader appletLoader = new AppletLoader() {

			@Override
			protected HashMap<String, Long> loadCache(File cacheBaseDirectory) throws Exception {
				return new HashMap<String, Long>();
			}

		};

		appletLoader.setJarProvider(jarProvider);

		appletLoader.cacheEnabled = true;

		appletLoader.urlList = new URL[] { new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl.jar.pack.lzma") };

		mockery.checking(new Expectations() {
			{
				oneOf(jarProvider).getJarInfo(appletLoader.urlList[0]);
				will(returnValue(new JarInfo("lwjgl.jar.pack.lzma", 10, 1000L)));
			}
		});

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

		final JarProvider jarProvider = mockery.mock(JarProvider.class);

		final AppletLoader appletLoader = new AppletLoader() {

			@Override
			protected HashMap<String, Long> loadCache(File cacheBaseDirectory) throws Exception {
				return new HashMap<String, Long>() {
					{
						put("lwjgl.jar.pack.lzma", 20L);
					}
				};
			}

		};

		appletLoader.setJarProvider(jarProvider);

		appletLoader.cacheEnabled = true;

		appletLoader.urlList = new URL[] { new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl.jar.pack.lzma") };

		mockery.checking(new Expectations() {
			{
				oneOf(jarProvider).getJarInfo(appletLoader.urlList[0]);
				will(returnValue(new JarInfo("lwjgl.jar.pack.lzma", 10, 100L)));
			}
		});

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

		final JarProvider jarProvider = mockery.mock(JarProvider.class);

		final AppletLoader appletLoader = new AppletLoader() {

			@Override
			protected HashMap<String, Long> loadCache(File cacheBaseDirectory) throws Exception {
				return new HashMap<String, Long>() {
					{
						put("lwjgl.jar.pack.lzma", 100L);
					}
				};
			}

		};

		appletLoader.setJarProvider(jarProvider);

		appletLoader.cacheEnabled = true;

		appletLoader.urlList = new URL[] { new URL("file:///" + tempDirectory + "/lwjgltest/lwjgl.jar.pack.lzma") };

		mockery.checking(new Expectations() {
			{
				oneOf(jarProvider).getJarInfo(appletLoader.urlList[0]);
				will(returnValue(new JarInfo("lwjgl.jar.pack.lzma", 10, 100L)));
			}
		});

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
