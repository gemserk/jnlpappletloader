package org.lwjgl.util.applet;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lwjgl.util.applet.JarDownloader.FileOutputStreamBuilder;
import org.lwjgl.util.applet.JarDownloader.UrlConnectionBuilder;

@RunWith(JMock.class)
public class JarDownloaderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	String tempDirectory = System.getProperty("java.io.tmpdir");

	@Test
	public void shouldDownloadFile() throws IOException {

		final String path = tempDirectory + File.separator + "jarDownloaderTest" + File.separator;

		JarUtil jarUtils = new JarUtil();

		final UrlConnectionBuilder urlConnectionBuilder = mockery.mock(UrlConnectionBuilder.class);
		final URLConnection urlConnection = mockery.mock(URLConnection.class);

		final InputStream inputStream = mockery.mock(InputStream.class);
		final OutputStream outputStream = mockery.mock(OutputStream.class);

		final FileDownloader fileDownloader = mockery.mock(FileDownloader.class);

		final FileOutputStreamBuilder fileOutputStreamBuilder = mockery.mock(FileOutputStreamBuilder.class);

		JarDownloader jarDownloader = new JarDownloader(new URL("file:"), path);
		jarDownloader.setJarUtils(jarUtils);
		jarDownloader.setUrlConnectionBuilder(urlConnectionBuilder);
		jarDownloader.setFileDownloader(fileDownloader);
		jarDownloader.setFileOutputStreamBuilder(fileOutputStreamBuilder);

		// expect

		mockery.checking(new Expectations() {
			{
				oneOf(urlConnectionBuilder).openConnection(new URL("file:lwjgl.jar"), "pack200-gzip, gzip");
				will(returnValue(urlConnection));

				oneOf(urlConnection).getContentEncoding();
				will(returnValue("null"));

				oneOf(urlConnection).getInputStream();
				will(returnValue(inputStream));

				oneOf(fileOutputStreamBuilder).getFileOutputStream(new File(path + File.separator + "lwjgl.jar"));
				will(returnValue(outputStream));

				oneOf(fileDownloader).download(inputStream, outputStream);

			}
		});

		File downloadedFile = jarDownloader.download(new FileInfo("lwjgl.jar", 100, 100L));

		assertThat(downloadedFile, IsNull.notNullValue());
		assertThat(downloadedFile.getAbsolutePath(), IsEqual.equalTo(path + "lwjgl.jar"));

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
