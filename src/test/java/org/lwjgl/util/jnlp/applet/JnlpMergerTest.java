package org.lwjgl.util.jnlp.applet;

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
import org.junit.runner.RunWith;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo.ResourceType;

@RunWith(JMock.class)
public class JnlpMergerTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void testMergeWithExtensions() throws MalformedURLException {

		final URLBuilder urlBuilder = mockery.mock(URLBuilder.class);
		final JNLPParser jnlpParser = mockery.mock(JNLPParser.class);

		JnlpMerger jnlpMerger = new JnlpMerger();
		jnlpMerger.setJnlpParser(jnlpParser);
		jnlpMerger.setUrlBuilder(urlBuilder);

		final URL jnlpUrl = new URL("file:somefile.jnlp");

		final JNLPInfo jnlpInfo = new JNLPInfo();
		jnlpInfo.codeBase = "jnlpcontext";
		jnlpInfo.resources.add(new JNLPResourceInfo("localjar.jar", "", ResourceType.Jar));
		jnlpInfo.resources.add(new JNLPResourceInfo("http://remote/remotejar.jar", "", ResourceType.Jar));
		jnlpInfo.resources.add(new JNLPResourceInfo("http://someplace.net/extension1.jnlp", "", ResourceType.Extension));

		final JNLPInfo extensionJnlpInfo = new JNLPInfo();
		extensionJnlpInfo.codeBase = "http://someplace.net/";
		extensionJnlpInfo.resources.add(new JNLPResourceInfo("lwjgl.jar", "", ResourceType.Jar));
		extensionJnlpInfo.resources.add(new JNLPResourceInfo("http://remote/remotejar2.jar", "", ResourceType.Jar));

		mockery.checking(new Expectations() {
			{
				oneOf(urlBuilder).build(jnlpUrl, "http://someplace.net/extension1.jnlp");
				will(returnValue(jnlpUrl));

				oneOf(jnlpParser).parseJnlp(jnlpUrl);
				will(returnValue(extensionJnlpInfo));

				oneOf(urlBuilder).build("http://someplace.net/");
				will(returnValue(jnlpUrl));

				oneOf(urlBuilder).build(jnlpUrl, "lwjgl.jar");
				will(returnValue(new URL("http://someplace.net/lwjgl.jar")));

				oneOf(urlBuilder).build(jnlpUrl, "http://remote/remotejar2.jar");
				will(returnValue(new URL("http://remote/remotejar2.jar")));
			}
		});

		jnlpMerger.mergeWithExtensions(jnlpInfo, jnlpUrl);

		assertThat(jnlpInfo, IsNull.notNullValue());
		assertThat(jnlpInfo.hasExtensions(), IsEqual.equalTo(false));
		assertThat(jnlpInfo.resources.size(), IsEqual.equalTo(4));
		assertThat(jnlpInfo.resources.get(2).type, IsEqual.equalTo(ResourceType.Jar));
		assertThat(jnlpInfo.resources.get(2).href, IsEqual.equalTo("http://someplace.net/lwjgl.jar"));

		new JnlpPrinter().printJnlpInfo(jnlpInfo);

	}
	
}
