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
import org.lwjgl.util.applet.JNLPParser.JNLPInfo;
import org.lwjgl.util.applet.JNLPParser.JNLPResourceInfo;

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

		List<JNLPResourceInfo> resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPResourceInfo("lwjgl.jar", "Windows", false));
				add(new JNLPResourceInfo("lwjgl-win.jar", "Windows", true));
				add(new JNLPResourceInfo("lwjgl-linux.jar", "Linux", true));
				add(new JNLPResourceInfo("lwjgl-mac.jar", "Mac OS", true));
			}
		};

		assertThat(jNLPAppletLoader.getJarsForOsStartingWith(resources, "Windows", true), IsEqual.equalTo("lwjgl-win.jar"));
	}

	@Test
	public void shouldGetOnlyAllNotNativeDependencies() throws MalformedURLException {
		JNLPAppletLoader jNLPAppletLoader = new JNLPAppletLoader();

		List<JNLPResourceInfo> resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPResourceInfo("lwjgl.jar", "", false));
				add(new JNLPResourceInfo("jinput.jar", "", false));
				add(new JNLPResourceInfo("lwjgl-win.jar", "Windows", true));
				add(new JNLPResourceInfo("lwjgl-linux.jar", "Linux", true));
				add(new JNLPResourceInfo("lwjgl-mac.jar", "Mac OS", true));
			}
		};

		assertThat(jNLPAppletLoader.getJarsForOsStartingWith(resources, "", false), IsEqual.equalTo("lwjgl.jar, jinput.jar"));
	}

	@Test
	public void shouldGetAllResourcesForAGivenOS() throws MalformedURLException {
		JNLPAppletLoader jNLPAppletLoader = new JNLPAppletLoader();

		List<JNLPResourceInfo> resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPResourceInfo("lwjgl-win95.jar", "Windows 95", false));
				add(new JNLPResourceInfo("lwjgl-win98.jar", "Windows 98", false));
				add(new JNLPResourceInfo("lwjgl-win2000.jar", "Windows 2000", false));
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

		JNLPInfo jNLPInfo = new JNLPInfo();

		HashMap<String, String> appletParameters = new HashMap<String, String>();
		jNLPAppletLoader.addNativesFor(jNLPInfo, appletParameters, "Windows", "al_windows");
		assertNull(appletParameters.get("al_windows"));

	}
	
	@Test
	public void shouldAddParameterIfNoNativesFound() {

		JNLPAppletLoader jNLPAppletLoader = new JNLPAppletLoader();

		JNLPInfo jNLPInfo = new JNLPInfo();
		jNLPInfo.resources = new ArrayList<JNLPResourceInfo>(){{
			add(new JNLPResourceInfo("lwjgl.jar", "Windows", true));
		}};

		HashMap<String, String> appletParameters = new HashMap<String, String>();
		jNLPAppletLoader.addNativesFor(jNLPInfo, appletParameters, "Windows", "al_windows");
		assertNotNull(appletParameters.get("al_windows"));

	}

}
