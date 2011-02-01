package org.lwjgl.util.applet;

import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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

	String tempDirectory = System.getProperty("java.io.tmpdir");

	class JarUtils {

		public List<String> getJars(String jarList) {
			ArrayList<String> jars = new ArrayList<String>();
			StringTokenizer stringTokenizer = new StringTokenizer(jarList, ", ");
			while (stringTokenizer.hasMoreElements())
				jars.add(stringTokenizer.nextToken());
			return jars;
		}

		public URL getCodebasedUrl(URL codebase, String jar) {
			try {
				return new URL(codebase, jar);
			} catch (MalformedURLException e) {
				throw new RuntimeException("failed to get codebased url for " + jar, e);
			}
		}

	}

	@Test
	public void testGetUrlsFromStringListCommaSeparated() {
		// String codebase = "http://localhost";
		String jarsParameter = "lwjgl-2.6.jar, jinput-2.6.jar, slick-2.6.jar";

		JarUtils jarUtils = new JarUtils();

		List<String> jars = jarUtils.getJars(jarsParameter);
		assertThat(jars, IsNull.notNullValue());
		assertThat(jars, IsCollectionContaining.hasItem("lwjgl-2.6.jar"));
		assertThat(jars, IsCollectionContaining.hasItem("jinput-2.6.jar"));
		assertThat(jars, IsCollectionContaining.hasItem("slick-2.6.jar"));
	}

	@Test
	public void testGetCodeBasedUrlForJarList() throws MalformedURLException {
		URL codebase = new URL("http://localhost");
		JarUtils jarUtils = new JarUtils();
		assertThat(new URL("http://localhost/lwjgl-2.6.jar"), IsEqual.equalTo(jarUtils.getCodebasedUrl(codebase, "lwjgl-2.6.jar")));
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
