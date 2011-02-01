package org.lwjgl.util.jnlp.applet;

import java.net.MalformedURLException;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class JNLPAppletLoaderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void emptyTest() throws MalformedURLException {
		
	}

	// @Test(expected = RuntimeException.class)
	// public void shouldFailWhenMissingRequiredParameters() throws MalformedURLException {
	//
	// final AppletStub appletStub = mockery.mock(AppletStub.class);
	//
	// mockery.checking(new Expectations() {
	// {
	// oneOf(appletStub).getCodeBase();
	// will(returnValue(new URL("file:")));
	// oneOf(appletStub).getParameter("al_jnlp");
	// will(returnValue(null));
	// }
	// });
	//
	// JNLPAppletLoader jnlpAppletLoader = new JNLPAppletLoader();
	// jnlpAppletLoader.setStub(appletStub);
	//
	// jnlpAppletLoader.init();
	// // expect panel added for new error
	// fail("should fail if parameter not found");
	//
	// }

}
