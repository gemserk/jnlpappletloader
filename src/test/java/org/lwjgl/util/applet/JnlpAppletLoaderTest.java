package org.lwjgl.util.applet;

import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.matchers.IsCollectionContaining;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class JnlpAppletLoaderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void testGetUrlsFromStringListCommaSeparated() {
		// String codebase = "http://localhost";
		String jarsParameter = "lwjgl-2.6.jar, jinput-2.6.jar, slick-2.6.jar";

		JarUtil jarUtil = new JarUtil();

		List<String> jars = jarUtil.getJars(jarsParameter);
		assertThat(jars, IsNull.notNullValue());
		assertThat(jars, IsCollectionContaining.hasItem("lwjgl-2.6.jar"));
		assertThat(jars, IsCollectionContaining.hasItem("jinput-2.6.jar"));
		assertThat(jars, IsCollectionContaining.hasItem("slick-2.6.jar"));
	}

	@Test
	public void testGetCodeBasedUrlForJarList() throws MalformedURLException {
		URL codebase = new URL("http://localhost");
		JarUtil jarUtil = new JarUtil();
		assertThat(new URL("http://localhost/lwjgl-2.6.jar"), IsEqual.equalTo(jarUtil.getCodeBasedUrl(codebase, "lwjgl-2.6.jar")));
	}

//	@Test
//	public void shouldFailWhenRequiredAppletParametersMissing() {
//
//		String mainClass = "";
//
//		Image logoImage;
//		Image progressBarImage;
//
//		List<URL> jarFiles = Lists.newArrayList();
//		List<URL> nativeFiles = Lists.newArrayList();
//
//		JnlpAppletLoader jnlpAppletLoader = new JnlpAppletLoader();
//
//		mockery.checking(new Expectations() {
//			{
//
//			}
//		});
//
//		jnlpAppletLoader.init();
//
//	}

}
