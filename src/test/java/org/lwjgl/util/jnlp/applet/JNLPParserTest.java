package org.lwjgl.util.jnlp.applet;

import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

@RunWith(JMock.class)
public class JNLPParserTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void testParseJnlpFile() throws Exception {

		URL jnlpUrl = Thread.currentThread().getContextClassLoader().getResource("applet.jnlp");

		InputStream jnlpInputStream = jnlpUrl.openStream();

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(jnlpInputStream);

		JNLPInfo jnlpInfo = new JNLPParser(new URLBuilder()).parse(document);

		new JnlpPrinter().printJnlpInfo(jnlpInfo);

		jnlpInputStream.close();
	}
	
	@Test
	public void testParseWithExtension() throws Exception {
		final URL url = new URL("file:");

		final URLBuilder urlBuilder = mockery.mock(URLBuilder.class);

		JNLPParser jnlpParser = new JNLPParser(urlBuilder);
		jnlpParser.setUrlBuilder(urlBuilder);

		mockery.checking(new Expectations() {
			{
				oneOf(urlBuilder).open(url);
				will(returnValue(Thread.currentThread().getContextClassLoader().getResourceAsStream("test-with-extensions.jnlp")));

				// ignoring(urlBuilder).build("http://someplace.org/releases/");
				// will(returnValue(url));
				//
				// oneOf(urlBuilder).build(url, "test-extension1.jnlp");
				// will(returnValue(url));
				//
				// oneOf(urlBuilder).open(url);
				// will(returnValue(Thread.currentThread().getContextClassLoader().getResourceAsStream("test-extension1.jnlp")));
				//
				// oneOf(urlBuilder).build(url, "http://anotherplace.net/releases/test-extension2.jnlp");
				// will(returnValue(url));
				//
				// oneOf(urlBuilder).open(url);
				// will(returnValue(Thread.currentThread().getContextClassLoader().getResourceAsStream("test-extension2.jnlp")));
			}
		});

		JNLPInfo jnlpInfo = jnlpParser.parseJnlp(url);

		assertThat(jnlpInfo, IsNull.notNullValue());
		assertThat(jnlpInfo.hasExtensions(), IsEqual.equalTo(true));
		// assertThat(jnlpInfo.extensions.size(), IsEqual.equalTo(2));

		// JNLPInfo firstExtensionJnlpInfo = jnlpInfo.extensions.get(0);
		// assertThat(firstExtensionJnlpInfo.codeBase, IsEqual.equalTo("http://someplace.org/releases/"));
		//
		// JNLPInfo secondExtensionJnlpInfo = jnlpInfo.extensions.get(1);
		// assertThat(secondExtensionJnlpInfo.codeBase, IsEqual.equalTo("http://anotherplace.net/releases/"));

		new JnlpPrinter().printJnlpInfo(jnlpInfo);
	}

}
