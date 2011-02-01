package org.lwjgl.util.jnlpappletloader;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.hamcrest.core.IsEqual;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class UrlBuilderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void shouldReturnUrlWithinContext() throws MalformedURLException {
		UrlBuilder urlBuilder = new UrlBuilder(new URL("http://localhost/"));
		URL url = urlBuilder.build("launch.jnlp");
		assertNotNull(url);
		assertThat(url, IsEqual.equalTo(new URL("http://localhost/launch.jnlp")));
	}

	@Test
	public void shouldReturnProperUrlWhenItIsAbsoluteFromHttp() throws MalformedURLException {
		UrlBuilder urlBuilder = new UrlBuilder(new URL("http://localhost/"));
		URL url = urlBuilder.build("http://acoppes/launch.jnlp");
		assertNotNull(url);
		assertThat(url, IsEqual.equalTo(new URL("http://acoppes/launch.jnlp")));
	}

	@Test
	public void shouldReturnProperUrlWhenItIsAbsoluteFromFileSystem() throws MalformedURLException {
		UrlBuilder urlBuilder = new UrlBuilder(new URL("http://localhost/"));
		URL url = urlBuilder.build("file:launch.jnlp");
		assertNotNull(url);
		assertThat(url, IsEqual.equalTo(new URL("file:launch.jnlp")));
	}
	
	@Test
	public void testIsAbsoluteUrl() throws MalformedURLException {
		UrlBuilder urlBuilder = new UrlBuilder(new URL("http://localhost/"));
		assertEquals(false, urlBuilder.isAbsolute("launch.jnlp"));
		assertEquals(true, urlBuilder.isAbsolute("http://someplace/launch.jnlp"));
		assertEquals(true, urlBuilder.isAbsolute("file:launch.jnlp"));
	}
	
}
