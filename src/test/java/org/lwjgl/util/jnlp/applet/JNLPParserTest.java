package org.lwjgl.util.jnlp.applet;

import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;

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
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo;
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

		JNLPInfo jnlpInfo = new JNLPParser().parse(document);

		printJnlpInfo(jnlpInfo);

		jnlpInputStream.close();
	}

	private void printJnlpInfo(JNLPInfo jnlpInfo) {
		System.out.println("codeBase: " + jnlpInfo.codeBase);

		if (jnlpInfo.jnlpAppletDescInfo != null) {
			System.out.println("applet.mainClass: " + jnlpInfo.jnlpAppletDescInfo.mainClassName);
			System.out.println("applet.name: " + jnlpInfo.jnlpAppletDescInfo.name);
			System.out.println("applet.parameters: " + jnlpInfo.jnlpAppletDescInfo.parameters);
		}

		for (JNLPResourceInfo jnlpResourceInfo : jnlpInfo.resources)
			System.out.println(MessageFormat.format("resource: href={0}, os={1}, native={2}", jnlpResourceInfo.href, jnlpResourceInfo.os, jnlpResourceInfo.nativeLib));

		for (JNLPInfo jnlpInfo2 : jnlpInfo.extensions)
			printJnlpInfo(jnlpInfo2);
	}

	@Test
	public void testParseWithExtension() throws Exception {
		final URL url = new URL("file:");

		final URLBuilder urlBuilder = mockery.mock(URLBuilder.class);

		JNLPParser jnlpParser = new JNLPParser();
		jnlpParser.setUrlBuilder(urlBuilder);

		mockery.checking(new Expectations() {
			{
				oneOf(urlBuilder).open(url);
				will(returnValue(Thread.currentThread().getContextClassLoader().getResourceAsStream("test-with-extensions.jnlp")));

				oneOf(urlBuilder).build("http://someplace.org/releases/");
				will(returnValue(url));

				oneOf(urlBuilder).build(url, "test-extension.jnlp");
				will(returnValue(url));

				oneOf(urlBuilder).open(url);
				will(returnValue(Thread.currentThread().getContextClassLoader().getResourceAsStream("test-extension.jnlp")));
			}
		});

		JNLPInfo jnlpInfo = jnlpParser.parseJnlp(url);

		assertThat(jnlpInfo, IsNull.notNullValue());
		assertThat(jnlpInfo.extensions.size(), IsEqual.equalTo(1));

		printJnlpInfo(jnlpInfo);
	}

}
