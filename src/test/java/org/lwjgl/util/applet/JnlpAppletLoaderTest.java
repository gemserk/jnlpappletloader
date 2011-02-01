package org.lwjgl.util.applet;

import static org.junit.Assert.*;

import java.applet.AppletStub;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hamcrest.core.IsEqual;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lwjgl.util.applet.JnlpParser.JnlpInfo;
import org.lwjgl.util.applet.JnlpParser.JnlpJarInfo;

@SuppressWarnings("serial")
@RunWith(JMock.class)
public class JnlpAppletLoaderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void shouldGetOnlyNativesForOnePlatform() throws MalformedURLException {
		JnlpAppletLoader jnlpAppletLoader = new JnlpAppletLoader();

		List<JnlpJarInfo> resources = new ArrayList<JnlpJarInfo>() {
			{
				add(new JnlpJarInfo("lwjgl.jar", "Windows", false));
				add(new JnlpJarInfo("lwjgl-win.jar", "Windows", true));
				add(new JnlpJarInfo("lwjgl-linux.jar", "Linux", true));
				add(new JnlpJarInfo("lwjgl-mac.jar", "Mac OS", true));
			}
		};

		assertThat(jnlpAppletLoader.getJarsForOsStartingWith(resources, "Windows", true), IsEqual.equalTo("lwjgl-win.jar"));
	}

	@Test
	public void shouldGetOnlyAllNotNativeDependencies() throws MalformedURLException {
		JnlpAppletLoader jnlpAppletLoader = new JnlpAppletLoader();

		List<JnlpJarInfo> resources = new ArrayList<JnlpJarInfo>() {
			{
				add(new JnlpJarInfo("lwjgl.jar", "", false));
				add(new JnlpJarInfo("jinput.jar", "", false));
				add(new JnlpJarInfo("lwjgl-win.jar", "Windows", true));
				add(new JnlpJarInfo("lwjgl-linux.jar", "Linux", true));
				add(new JnlpJarInfo("lwjgl-mac.jar", "Mac OS", true));
			}
		};

		assertThat(jnlpAppletLoader.getJarsForOsStartingWith(resources, "", false), IsEqual.equalTo("lwjgl.jar, jinput.jar"));
	}

	@Test
	public void shouldGetAllResourcesForAGivenOS() throws MalformedURLException {
		JnlpAppletLoader jnlpAppletLoader = new JnlpAppletLoader();

		List<JnlpJarInfo> resources = new ArrayList<JnlpJarInfo>() {
			{
				add(new JnlpJarInfo("lwjgl-win95.jar", "Windows 95", false));
				add(new JnlpJarInfo("lwjgl-win98.jar", "Windows 98", false));
				add(new JnlpJarInfo("lwjgl-win2000.jar", "Windows 2000", false));
			}
		};

		assertThat(jnlpAppletLoader.getJarsForOsStartingWith(resources, "Windows", false), IsEqual.equalTo("lwjgl-win95.jar, lwjgl-win98.jar, lwjgl-win2000.jar"));
	}

	@Test(expected = RuntimeException.class)
	public void shouldFailWhenMissingRequiredParameters() throws MalformedURLException {

		final AppletStub appletStub = mockery.mock(AppletStub.class);

		mockery.checking(new Expectations() {
			{
				oneOf(appletStub).getCodeBase();
				will(returnValue(new URL("file:")));
				oneOf(appletStub).getParameter("al_jnlp");
				will(returnValue(null));
			}
		});

		JnlpAppletLoader jnlpAppletLoader = new JnlpAppletLoader();
		jnlpAppletLoader.setStub(appletStub);

		jnlpAppletLoader.init();
		fail("should fail if parameter not found");

	}

	@Test
	public void shouldNotAddParameterIfNoNativesFound() {

		JnlpAppletLoader jnlpAppletLoader = new JnlpAppletLoader();

		JnlpInfo jnlpInfo = new JnlpInfo();

		HashMap<String, String> appletParameters = new HashMap<String, String>();
		jnlpAppletLoader.addNativesFor(jnlpInfo, appletParameters, "Windows", "al_windows");
		assertNull(appletParameters.get("al_windows"));

	}
	
	@Test
	public void shouldAddParameterIfNoNativesFound() {

		JnlpAppletLoader jnlpAppletLoader = new JnlpAppletLoader();

		JnlpInfo jnlpInfo = new JnlpInfo();
		jnlpInfo.resources = new ArrayList<JnlpJarInfo>(){{
			add(new JnlpJarInfo("lwjgl.jar", "Windows", true));
		}};

		HashMap<String, String> appletParameters = new HashMap<String, String>();
		jnlpAppletLoader.addNativesFor(jnlpInfo, appletParameters, "Windows", "al_windows");
		assertNotNull(appletParameters.get("al_windows"));

	}

}
