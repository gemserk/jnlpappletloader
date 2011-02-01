package org.lwjgl.util.applet;

import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.matchers.IsCollectionContaining;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class AppletParametersProxyTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void shouldGetAppletParametersFromApplet() throws MalformedURLException {
		final AppletParametersUtil appletParametersUtil = mockery.mock(AppletParametersUtil.class);

		AppletParametersProxy appletParametersProxy = new AppletParametersProxy(appletParametersUtil);
		mockery.checking(new Expectations() {
			{
				oneOf(appletParametersUtil).getRequiredParameter("al_main");
				will(returnValue("MAIN"));
				oneOf(appletParametersUtil).getCodeBase();
				will(returnValue(new URL("http://localhost/")));
				oneOf(appletParametersUtil).getParameter("al_jars", "");
				will(returnValue("lwjgl.jar, jinput.jar"));
				oneOf(appletParametersUtil).getRequiredParameter("al_logo");
				will(returnValue("LOGO"));
				oneOf(appletParametersUtil).getRequiredParameter("al_progressbar");
				will(returnValue("PROGRESSBAR"));
			}
		});
		AppletParameters appletParameters = appletParametersProxy.getAppletParameters();
		assertThat(appletParameters, IsNull.notNullValue());
		assertThat(appletParameters.getMain(), IsEqual.equalTo("MAIN"));
		assertThat(appletParameters.getJars(), IsCollectionContaining.hasItems(new URL("http://localhost/lwjgl.jar"), new URL("http://localhost/jinput.jar")));
		assertThat(appletParameters.getLogo(), IsEqual.equalTo("LOGO"));
		assertThat(appletParameters.getProgessbar(), IsEqual.equalTo("PROGRESSBAR"));
	}

}
