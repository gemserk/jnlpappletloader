package org.lwjgl.util.applet;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class JarDownloaderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	String tempDirectory = System.getProperty("java.io.tmpdir");

	@Test
	public void shouldDownloadFile() {
		
	}
	
	// @Test
	// public void shouldDownloadFile() throws MalformedURLException {
	//
	// final URL codeBase = new URL("http://localhost");
	// String path = tempDirectory + File.separator + "jarDownloaderTest" + File.separator;
	//		
	// final JarUtil jarUtils = mockery.mock(JarUtil.class);
	//
	// JarDownloader jarDownloader = new JarDownloader(codeBase, path);
	// jarDownloader.setJarUtils(jarUtils);
	//		
	// FileInfo fileInfo = new FileInfo("lwjgl.jar", 100, 100L);
	//
	// mockery.checking(new Expectations() {
	// {
	// oneOf(jarUtils).getCodeBasedUrl(codeBase, "lwjgl.jar");
	// will(returnValue(new URL("http://localhost/lwjgl.jar")));
	// }
	// });
	//
	// File downloadedFile = jarDownloader.download(fileInfo);
	//		
	// assertThat(downloadedFile, IsNull.notNullValue());
	// assertThat(downloadedFile.getAbsolutePath(), IsEqual.equalTo(path + "lwjgl.jar"));
	//		
	// }

}
