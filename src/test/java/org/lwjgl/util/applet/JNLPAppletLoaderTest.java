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
import org.lwjgl.util.applet.JNLPAppletLoader.JNLPInfo;
import org.lwjgl.util.applet.JNLPAppletLoader.JNLPResourceInfo;

@SuppressWarnings("serial")
@RunWith(JMock.class)
public class JNLPAppletLoaderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void shouldGetOnlyNativesForOnePlatform() throws MalformedURLException {
		JNLPAppletLoader jNLPAppletLoader = new JNLPAppletLoader();

		List<JNLPAppletLoader.JNLPResourceInfo> resources = new ArrayList<JNLPAppletLoader.JNLPResourceInfo>() {
			{
				add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl.jar", "Windows", false));
				add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl-win.jar", "Windows", true));
				add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl-linux.jar", "Linux", true));
				add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl-mac.jar", "Mac OS", true));
			}
		};

		assertThat(jNLPAppletLoader.getJarsForOsStartingWith(resources, "Windows", true), IsEqual.equalTo("lwjgl-win.jar"));
	}

	@Test
	public void shouldGetOnlyAllNotNativeDependencies() throws MalformedURLException {
		JNLPAppletLoader jNLPAppletLoader = new JNLPAppletLoader();

		List<JNLPAppletLoader.JNLPResourceInfo> resources = new ArrayList<JNLPAppletLoader.JNLPResourceInfo>() {
			{
				add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl.jar", "", false));
				add(new JNLPAppletLoader.JNLPResourceInfo("jinput.jar", "", false));
				add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl-win.jar", "Windows", true));
				add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl-linux.jar", "Linux", true));
				add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl-mac.jar", "Mac OS", true));
			}
		};

		assertThat(jNLPAppletLoader.getJarsForOsStartingWith(resources, "", false), IsEqual.equalTo("lwjgl.jar, jinput.jar"));
	}

	@Test
	public void shouldGetAllResourcesForAGivenOS() throws MalformedURLException {
		JNLPAppletLoader jNLPAppletLoader = new JNLPAppletLoader();

		List<JNLPAppletLoader.JNLPResourceInfo> resources = new ArrayList<JNLPAppletLoader.JNLPResourceInfo>() {
			{
				add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl-win95.jar", "Windows 95", false));
				add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl-win98.jar", "Windows 98", false));
				add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl-win2000.jar", "Windows 2000", false));
			}
		};

		assertThat(jNLPAppletLoader.getJarsForOsStartingWith(resources, "Windows", false), IsEqual.equalTo("lwjgl-win95.jar, lwjgl-win98.jar, lwjgl-win2000.jar"));
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

		JNLPAppletLoader jNLPAppletLoader = new JNLPAppletLoader();
		jNLPAppletLoader.setStub(appletStub);

		jNLPAppletLoader.init();
		fail("should fail if parameter not found");

	}

	@Test
	public void shouldNotAddParameterIfNoNativesFound() {

		JNLPAppletLoader jNLPAppletLoader = new JNLPAppletLoader();

		JNLPAppletLoader.JNLPInfo jNLPInfo = new JNLPAppletLoader.JNLPInfo();

		HashMap<String, String> appletParameters = new HashMap<String, String>();
		jNLPAppletLoader.addNativesFor(jNLPInfo, appletParameters, "Windows", "al_windows");
		assertNull(appletParameters.get("al_windows"));

	}
	
	@Test
	public void shouldAddParameterIfNoNativesFound() {

		JNLPAppletLoader jNLPAppletLoader = new JNLPAppletLoader();

		JNLPAppletLoader.JNLPInfo jNLPInfo = new JNLPAppletLoader.JNLPInfo();
		jNLPInfo.resources = new ArrayList<JNLPAppletLoader.JNLPResourceInfo>(){{
			add(new JNLPAppletLoader.JNLPResourceInfo("lwjgl.jar", "Windows", true));
		}};

		HashMap<String, String> appletParameters = new HashMap<String, String>();
		jNLPAppletLoader.addNativesFor(jNLPInfo, appletParameters, "Windows", "al_windows");
		assertNotNull(appletParameters.get("al_windows"));

	}

}
