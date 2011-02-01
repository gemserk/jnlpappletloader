package org.lwjgl.util.jnlpappletloader;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.applet.AppletStub;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.IsEqual;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lwjgl.util.jnlpappletloader.JnlpParser.JnlpJarInfo;
import org.lwjgl.util.jnlpappletloader.exceptions.MissingRequiredParameterException;

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

		assertThat(jnlpAppletLoader.getJars(resources, "Windows", true), IsEqual.equalTo("lwjgl-win.jar"));
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

		assertThat(jnlpAppletLoader.getJars(resources, "", false), IsEqual.equalTo("lwjgl.jar, jinput.jar"));
	}

	@Test(expected = MissingRequiredParameterException.class)
	public void shouldFailWhenMissingRequiredParameters() {

		final AppletStub appletStub = mockery.mock(AppletStub.class);

		mockery.checking(new Expectations() {
			{
				oneOf(appletStub).getParameter("al_jnlp");
				will(returnValue(null));
			}
		});

		JnlpAppletLoader jnlpAppletLoader = new JnlpAppletLoader();
		jnlpAppletLoader.setStub(appletStub);

		jnlpAppletLoader.init();
		fail("should fail if parameter not found");

	}

}
