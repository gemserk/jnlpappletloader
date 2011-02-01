package org.lwjgl.util.jnlp.applet;

import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.hamcrest.collection.IsCollectionContaining;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo.ResourceType;

@RunWith(JMock.class)
public class JNLPAppletLoaderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void emptyTest() throws MalformedURLException {
		
		final URLBuilder urlBuilder = mockery.mock(URLBuilder.class);
		final JNLPParser jnlpParser = mockery.mock(JNLPParser.class);

		JNLPAppletLoader jnlpAppletLoader = new JNLPAppletLoader();
		
		jnlpAppletLoader.codeBase = new URL("file:");
		jnlpAppletLoader.setJnlpParser(jnlpParser);
		jnlpAppletLoader.setUrlBuilder(urlBuilder);
		
		final URL jnlpUrl = new URL("file:somefile.jnlp");
		
		final JNLPInfo jnlpInfo = new JNLPInfo();
		jnlpInfo.codeBase = "jnlpcontext";
		jnlpInfo.resources.add(new JNLPResourceInfo("http://someplace.net/extension1.jnlp", "", ResourceType.Extension));

		final JNLPInfo extensionJnlpInfo = new JNLPInfo();
		extensionJnlpInfo.codeBase = "anothercontext";
		extensionJnlpInfo.resources.add(new JNLPResourceInfo("lwjgl.jar", "", ResourceType.Jar));

		mockery.checking(new Expectations() {
			{
				oneOf(jnlpParser).parseJnlp(jnlpUrl);
				will(returnValue(jnlpInfo));
				
				oneOf(urlBuilder).build(jnlpInfo.codeBase);
				will(returnValue(jnlpUrl));

				oneOf(urlBuilder).build(jnlpUrl, "http://someplace.net/extension1.jnlp");
				will(returnValue(jnlpUrl));

				oneOf(jnlpParser).parseJnlp(jnlpUrl);
				will(returnValue(extensionJnlpInfo));
			}
		});

		JNLPInfo mergedJnlp = jnlpAppletLoader.getMergedJnlp(jnlpUrl);
		
		assertThat(mergedJnlp, IsNull.notNullValue());
		assertThat(mergedJnlp.hasExtensions(), IsEqual.equalTo(false));
//		assertThat(mergedJnlp.resources, IsCollectionContaining.hasItem(extensionJnlpInfo.resources.get(0)));
		assertThat(mergedJnlp.extensions, IsCollectionContaining.hasItem(extensionJnlpInfo));
		
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

