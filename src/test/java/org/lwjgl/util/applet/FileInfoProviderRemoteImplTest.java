package org.lwjgl.util.applet;

import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
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

@RunWith(JMock.class)
public class FileInfoProviderRemoteImplTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void shouldGetJarInfoFromUrl() throws MalformedURLException {

		final URLConnection urlConnection = mockery.mock(URLConnection.class);

		mockery.checking(new Expectations() {
			{
				oneOf(urlConnection).getContentLength();
				will(returnValue(100));
				oneOf(urlConnection).getLastModified();
				will(returnValue(1000L));
			}
		});

		FileInfoProviderRemoteImpl fileInfoProviderRemoteImpl = new FileInfoProviderRemoteImpl() {

			@Override
			protected URLConnection getUrlConnection(URL url) throws Exception {
				return urlConnection;
			}

		};

		FileInfo info = fileInfoProviderRemoteImpl.getFileInfo(new URL("http://localhost/lwjgl.jar"));

		assertThat(info, IsNull.notNullValue());
		assertThat(info.contentLength, IsEqual.equalTo(100));
		assertThat(info.lastModified, IsEqual.equalTo(1000L));
		assertThat(info.fileName, IsEqual.equalTo("lwjgl.jar"));
	}
	
	@Test
	public void shouldGetJarInfoFromString() throws MalformedURLException {

		final URLConnection urlConnection = mockery.mock(URLConnection.class);

		mockery.checking(new Expectations() {
			{
				oneOf(urlConnection).getContentLength();
				will(returnValue(100));
				oneOf(urlConnection).getLastModified();
				will(returnValue(1000L));
			}
		});

		FileInfoProviderRemoteImpl fileInfoProviderRemoteImpl = new FileInfoProviderRemoteImpl() {

			@Override
			protected URLConnection getUrlConnection(URL url) throws Exception {
				assertThat(url, IsEqual.equalTo(new URL("http://localhost/lwjgl.jar")));
				return urlConnection;
			}

		};
		fileInfoProviderRemoteImpl.setCodeBase(new URL("http://localhost/"));

		FileInfo info = fileInfoProviderRemoteImpl.getFileInfo("lwjgl.jar");

		assertThat(info, IsNull.notNullValue());
		assertThat(info.contentLength, IsEqual.equalTo(100));
		assertThat(info.lastModified, IsEqual.equalTo(1000L));
		assertThat(info.fileName, IsEqual.equalTo("lwjgl.jar"));
	}

}
