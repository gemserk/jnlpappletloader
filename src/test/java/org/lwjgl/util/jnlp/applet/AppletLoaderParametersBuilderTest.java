package org.lwjgl.util.jnlp.applet;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hamcrest.core.IsEqual;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo;

@SuppressWarnings("serial")
@RunWith(JMock.class)
public class AppletLoaderParametersBuilderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void shouldGetOnlyNativesForOnePlatform() throws MalformedURLException {
		List<JNLPResourceInfo> resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPInfo.JNLPResourceInfo("lwjgl.jar", "Windows", false));
				add(new JNLPInfo.JNLPResourceInfo("lwjgl-win.jar", "Windows", true));
				add(new JNLPInfo.JNLPResourceInfo("lwjgl-linux.jar", "Linux", true));
				add(new JNLPInfo.JNLPResourceInfo("lwjgl-mac.jar", "Mac OS", true));
			}
		};
		assertThat(new AppletLoaderParametersBuilder(null).getJarsForOsStartingWith(resources, "Windows", true), IsEqual.equalTo("lwjgl-win.jar"));
	}

	@Test
	public void shouldGetOnlyAllNotNativeDependencies() throws MalformedURLException {
		List<JNLPResourceInfo> resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPInfo.JNLPResourceInfo("lwjgl.jar", "", false));
				add(new JNLPInfo.JNLPResourceInfo("jinput.jar", "", false));
				add(new JNLPInfo.JNLPResourceInfo("lwjgl-win.jar", "Windows", true));
				add(new JNLPInfo.JNLPResourceInfo("lwjgl-linux.jar", "Linux", true));
				add(new JNLPInfo.JNLPResourceInfo("lwjgl-mac.jar", "Mac OS", true));
			}
		};
		assertThat(new AppletLoaderParametersBuilder(null).getJarsForOsStartingWith(resources, "", false), IsEqual.equalTo("lwjgl.jar, jinput.jar"));
	}

	@Test
	public void shouldGetAllResourcesForAGivenOS() throws MalformedURLException {
		List<JNLPResourceInfo> resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPInfo.JNLPResourceInfo("lwjgl-win95.jar", "Windows 95", false));
				add(new JNLPInfo.JNLPResourceInfo("lwjgl-win98.jar", "Windows 98", false));
				add(new JNLPInfo.JNLPResourceInfo("lwjgl-win2000.jar", "Windows 2000", false));
			}
		};
		assertThat(new AppletLoaderParametersBuilder(null).getJarsForOsStartingWith(resources, "Windows", false), IsEqual.equalTo("lwjgl-win95.jar, lwjgl-win98.jar, lwjgl-win2000.jar"));
	}

	@Test
	public void shouldNotAddParameterIfNoNativesFound() {
		JNLPInfo jnlpInfo = new JNLPInfo();
		HashMap<String, String> appletParameters = new HashMap<String, String>();
		new AppletLoaderParametersBuilder(jnlpInfo).addNativesFor(appletParameters, "Windows", "al_windows");
		assertNull(appletParameters.get("al_windows"));
	}

	@Test
	public void shouldAddParameterIfNoNativesFound() {
		JNLPInfo jnlpInfo = new JNLPInfo();
		jnlpInfo.resources = new ArrayList<JNLPInfo.JNLPResourceInfo>() {
			{
				add(new JNLPInfo.JNLPResourceInfo("lwjgl.jar", "Windows", true));
			}
		};
		HashMap<String, String> appletParameters = new HashMap<String, String>();
		new AppletLoaderParametersBuilder(jnlpInfo).addNativesFor(appletParameters, "Windows", "al_windows");
		assertNotNull(appletParameters.get("al_windows"));
	}

}
