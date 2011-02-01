package org.lwjgl.util.jnlp.applet;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPAppletDescInfo;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo.ResourceType;

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
		JNLPInfo jnlpInfo = new JNLPInfo();
		jnlpInfo.resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPResourceInfo("lwjgl.jar", "Windows", ResourceType.Jar));
				add(new JNLPResourceInfo("lwjgl-win.jar", "Windows", ResourceType.NativeLib));
				add(new JNLPResourceInfo("lwjgl-linux.jar", "Linux", ResourceType.NativeLib));
				add(new JNLPResourceInfo("lwjgl-mac.jar", "Mac OS", ResourceType.NativeLib));
			}
		};
		AppletLoaderParametersBuilder appletLoaderParametersBuilder = new AppletLoaderParametersBuilder(jnlpInfo);
		assertThat(appletLoaderParametersBuilder.getJarsForOsStartingWith(jnlpInfo, "Windows", ResourceType.NativeLib), IsEqual.equalTo("lwjgl-win.jar"));
	}

	@Test
	public void shouldGetOnlyAllNotNativeDependencies() throws MalformedURLException {
		JNLPInfo jnlpInfo = new JNLPInfo();
		jnlpInfo.resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPResourceInfo("lwjgl.jar", "", ResourceType.Jar));
				add(new JNLPResourceInfo("jinput.jar", "", ResourceType.Jar));
				add(new JNLPResourceInfo("lwjgl-win.jar", "Windows", ResourceType.NativeLib));
				add(new JNLPResourceInfo("lwjgl-linux.jar", "Linux", ResourceType.NativeLib));
				add(new JNLPResourceInfo("lwjgl-mac.jar", "Mac OS", ResourceType.NativeLib));
			}
		};
		AppletLoaderParametersBuilder appletLoaderParametersBuilder = new AppletLoaderParametersBuilder(jnlpInfo);
		assertThat(appletLoaderParametersBuilder.getJarsForOsStartingWith(jnlpInfo, "", ResourceType.Jar), IsEqual.equalTo("lwjgl.jar, jinput.jar"));
	}

	@Test
	public void shouldGetAllResourcesForAGivenOS() throws MalformedURLException {
		JNLPInfo jnlpInfo = new JNLPInfo();
		jnlpInfo.resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPResourceInfo("lwjgl-win95.jar", "Windows 95", ResourceType.Jar));
				add(new JNLPResourceInfo("lwjgl-win98.jar", "Windows 98", ResourceType.Jar));
				add(new JNLPResourceInfo("lwjgl-win2000.jar", "Windows 2000", ResourceType.Jar));
			}
		};
		AppletLoaderParametersBuilder appletLoaderParametersBuilder = new AppletLoaderParametersBuilder(jnlpInfo);
		assertThat(appletLoaderParametersBuilder.getJarsForOsStartingWith(jnlpInfo, "Windows", ResourceType.Jar), IsEqual.equalTo("lwjgl-win95.jar, lwjgl-win98.jar, lwjgl-win2000.jar"));
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
		jnlpInfo.resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPResourceInfo("lwjgl.jar", "Windows", ResourceType.NativeLib));
			}
		};
		HashMap<String, String> appletParameters = new HashMap<String, String>();
		new AppletLoaderParametersBuilder(jnlpInfo).addNativesFor(appletParameters, "Windows", "al_windows");
		assertNotNull(appletParameters.get("al_windows"));
	}

	@Test
	public void shouldAddExtensionResourcesAsAbsoluteUrls() {
		JNLPInfo extensionJnlpInfo = new JNLPInfo();
		extensionJnlpInfo.codeBase = "http://someplace.net/releases/";
		extensionJnlpInfo.resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPResourceInfo("jinput.jar", "", ResourceType.Jar));
				add(new JNLPResourceInfo("jutils.jar", "", ResourceType.Jar));
			}
		};

		JNLPInfo jnlpInfo = new JNLPInfo();
		jnlpInfo.codeBase = ".";
		jnlpInfo.jnlpAppletDescInfo = new JNLPAppletDescInfo() {
			{
				mainClassName = "Main";
				name = "name";
			}
		};
		jnlpInfo.resources = new ArrayList<JNLPResourceInfo>() {
			{
				add(new JNLPResourceInfo("lwjgl.jar", "", ResourceType.Jar));
			}
		};
		jnlpInfo.extensions.add(extensionJnlpInfo);

		Map<String, String> appletParameters = new AppletLoaderParametersBuilder(jnlpInfo).getParametersFromJnlpInfo();

		String jarsParameter = appletParameters.get("al_jars");
		assertThat(jarsParameter, IsNull.notNullValue());
		assertThat(jarsParameter, IsEqual.equalTo("lwjgl.jar, http://someplace.net/releases/jinput.jar, http://someplace.net/releases/jutils.jar"));
	}

}
