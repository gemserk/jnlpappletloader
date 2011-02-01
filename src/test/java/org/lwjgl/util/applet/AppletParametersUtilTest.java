package org.lwjgl.util.applet;

import static org.junit.Assert.assertThat;

import java.applet.Applet;
import java.net.MalformedURLException;
import java.net.URL;

import org.hamcrest.core.IsEqual;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class AppletParametersUtilTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test(expected = MissingRequiredParameterException.class)
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

	@Test
	public void shouldReturnParameterFromApplet() {
		final String parameterName = "parameterName";
		final Applet applet = mockery.mock(Applet.class);
		AppletParametersUtil appletParametersUtil = new AppletParametersUtil(applet);
		mockery.checking(new Expectations() {
			{
				oneOf(applet).getParameter(parameterName);
				will(returnValue("parameterValue"));
			}
		});
		assertThat(appletParametersUtil.getRequiredParameter(parameterName), IsEqual.equalTo("parameterValue"));
	}
	
	@Test
	public void shouldReturnCodeBaseFromApplet() throws MalformedURLException {
		final Applet applet = mockery.mock(Applet.class);
		AppletParametersUtil appletParametersUtil = new AppletParametersUtil(applet);
		mockery.checking(new Expectations() {
			{
				oneOf(applet).getCodeBase();
				will(returnValue(new URL("http://lwjgl.com")));
			}
		});
		assertThat(appletParametersUtil.getCodeBase(), IsEqual.equalTo(new URL("http://lwjgl.com")));
	}

}
