package org.lwjgl.util.applet;

import static org.junit.Assert.assertThat;

import java.applet.Applet;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.matchers.IsCollectionContaining;
import org.junit.runner.RunWith;

import com.google.inject.internal.Lists;

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

	public static class AppletParameters {

		String main;

		String jars;

		String logo;

		String progessbar;

		public void setMain(String main) {
			this.main = main;
		}

		public String getMain() {
			return main;
		}

		public String getJars() {
			return jars;
		}

		public void setJars(String jars) {
			this.jars = jars;
		}

		public String getLogo() {
			return logo;
		}

		public void setLogo(String logo) {
			this.logo = logo;
		}

		public String getProgessbar() {
			return progessbar;
		}

		public void setProgessbar(String progessbar) {
			this.progessbar = progessbar;
		}

	}

	public static class AppletParametersUtil {

		private final Applet applet;

		public AppletParametersUtil(Applet applet) {
			this.applet = applet;
		}

		public String getRequiredParameter(String parameterName) {
			String parameterValue = applet.getParameter(parameterName);
			if (parameterValue == null)
				throw new MissingRequiredParameter(parameterName);
			return parameterValue;
		}

		protected boolean getBooleanParameter(String name, boolean defaultValue) {
			String parameter = applet.getParameter(name);
			if (parameter != null)
				return Boolean.parseBoolean(parameter);
			return defaultValue;
		}

	}

	@Test(expected = MissingRequiredParameter.class)
	public void shouldFailWhenMissingParameter() {
		final String parameterName = "parameterName";
		final Applet applet = mockery.mock(Applet.class);
		AppletParametersUtil appletParametersUtil = new AppletParametersUtil(applet);
		mockery.checking(new Expectations() {
			{
				oneOf(applet).getParameter(parameterName);
				will(returnValue(null));
			}
		});
		appletParametersUtil.getRequiredParameter(parameterName);
	}

	public static class AppletParametersProxy {

		private AppletParametersUtil appletParametersUtil;

		public AppletParametersProxy(AppletParametersUtil appletParametersUtil) {
			this.appletParametersUtil = appletParametersUtil;
		}

		public AppletParameters getAppletParameters() {
			AppletParameters appletParameters = new AppletParameters();
			appletParameters.setMain(appletParametersUtil.getRequiredParameter("al_main"));
			appletParameters.setJars(appletParametersUtil.getRequiredParameter("al_jars"));
			appletParameters.setLogo(appletParametersUtil.getRequiredParameter("al_logo"));
			appletParameters.setProgessbar(appletParametersUtil.getRequiredParameter("al_progressbar"));
			return appletParameters;
		}
	}

	@Test
	public void shouldGetAppletParametersFromApplet() {
		final AppletParametersUtil appletParametersUtil = mockery.mock(AppletParametersUtil.class);

		AppletParametersProxy appletParametersProxy = new AppletParametersProxy(appletParametersUtil);
		mockery.checking(new Expectations() {
			{
				oneOf(appletParametersUtil).getRequiredParameter("al_main");
				will(returnValue("MAIN"));
				oneOf(appletParametersUtil).getRequiredParameter("al_jars");
				will(returnValue("JARS"));
				oneOf(appletParametersUtil).getRequiredParameter("al_logo");
				will(returnValue("LOGO"));
				oneOf(appletParametersUtil).getRequiredParameter("al_progressbar");
				will(returnValue("PROGRESSBAR"));
			}
		});
		AppletParameters appletParameters = appletParametersProxy.getAppletParameters();
		assertThat(appletParameters, IsNull.notNullValue());
		assertThat(appletParameters.getMain(), IsEqual.equalTo("MAIN"));
		assertThat(appletParameters.getJars(), IsEqual.equalTo("JARS"));
		assertThat(appletParameters.getLogo(), IsEqual.equalTo("LOGO"));
		assertThat(appletParameters.getProgessbar(), IsEqual.equalTo("PROGRESSBAR"));
	}

	public static class AppletLoader extends Applet {

		AppletParametersProxy appletParametersProxy;
		private AppletParameters appletParameters;

		public void setAppletParametersProxy(AppletParametersProxy appletParametersProxy) {
			this.appletParametersProxy = appletParametersProxy;
		}

		@Override
		public void init() {

			appletParameters = appletParametersProxy.getAppletParameters();

			// parseParameters

			// get jars info?

			// check cache and remove already downloader jars from jars to download?

			// download jars

			// update cache?

		}

	}

	public static class MissingRequiredParameter extends RuntimeException {

		private final String parameter;

		public String getParameter() {
			return parameter;
		}

		public MissingRequiredParameter(String parameter) {
			this.parameter = parameter;
		}

		public MissingRequiredParameter(String parameter, String message, Throwable cause) {
			super(message, cause);
			this.parameter = parameter;
		}

		public MissingRequiredParameter(String parameter, Throwable cause) {
			super(cause);
			this.parameter = parameter;
		}

	}

	// alguien que determina los nativos a partir del sistema operativo

	// alguien que convierte todos los parámetros a cosas que necesito directamente.

	// render customizable a mano, usando una clase Progress o algo similar, donde consulto estado actual

	@Test(expected = MissingRequiredParameter.class)
	public void shouldFailWhenRequiredAppletParametersMissing() {

		final AppletParametersProxy appletParametersProxy = mockery.mock(AppletParametersProxy.class);

		String mainClass = "";

		Image logoImage;
		Image progressBarImage;

		List<URL> jarFiles = Lists.newArrayList();
		List<URL> nativeFiles = Lists.newArrayList();

		AppletLoader appletLoader = new AppletLoader();
		appletLoader.setAppletParametersProxy(appletParametersProxy);

		mockery.checking(new Expectations() {
			{
				oneOf(appletParametersProxy).getAppletParameters();
				will(throwException(new MissingRequiredParameter("")));
			}
		});

		appletLoader.init();

	}

}
