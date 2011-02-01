package org.lwjgl.util.jnlp.applet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class URLBuilderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	
	@Test
	public void shouldReturnUrlWithinContext() throws MalformedURLException {
		URLBuilder urlBuilder = new URLBuilder(new URL("http://localhost/"));
		URL url = urlBuilder.build("launch.jnlp");
		assertNotNull(url);
		assertThat(url, IsEqual.equalTo(new URL("http://localhost/launch.jnlp")));
	}

	@Test
	public void shouldReturnProperUrlWhenItIsAbsoluteFromHttp() throws MalformedURLException {
		URLBuilder urlBuilder = new URLBuilder(new URL("http://localhost/"));
		URL url = urlBuilder.build("http://acoppes/launch.jnlp");
		assertNotNull(url);
		assertThat(url, IsEqual.equalTo(new URL("http://acoppes/launch.jnlp")));
	}

	@Test
	public void shouldReturnProperUrlWhenItIsAbsoluteFromFileSystem() throws MalformedURLException {
		URLBuilder urlBuilder = new URLBuilder(new URL("http://localhost/"));
		URL url = urlBuilder.build("file:launch.jnlp");
		assertNotNull(url);
		assertThat(url, IsEqual.equalTo(new URL("file:launch.jnlp")));
	}
	
	@Test(expected=RuntimeException.class)
	public void testOpenUrlInputStreamFromFileNotFound() throws Exception {
		URL url = new URL("file:");
		URLBuilder urlBuilder = new URLBuilder();
		urlBuilder.open(url);
	}
	
	@Test
	public void testOpenUrlInputStreamFromFile() throws Exception {
		URLBuilder urlBuilder = new URLBuilder();
		InputStream is = urlBuilder.open(Thread.currentThread().getContextClassLoader().getResource("test-with-extensions.jnlp"));
		assertThat(is, IsNull.notNullValue());
		is.close();
	}
}
