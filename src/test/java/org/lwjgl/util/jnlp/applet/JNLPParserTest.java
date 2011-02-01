package org.lwjgl.util.jnlp.applet;

import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

		// Document document = documentBuilder.parse(jnlpFile);

		JNLPInfo jnlpInfo = new JNLPParser().parse(document);
		// JNLPInfo jNLPInfo = new JNLPParser(document).parse();

		printJnlpInfo(jnlpInfo);

		jnlpInputStream.close();
	}

	private void printJnlpInfo(JNLPInfo jnlpInfo) {
		System.out.println("codeBase: " + jnlpInfo.codeBase);
		System.out.println("applet.mainClass: " + jnlpInfo.jNLPAppletDescInfo.mainClassName);
		System.out.println("applet.name: " + jnlpInfo.jNLPAppletDescInfo.name);
		System.out.println("applet.parameters: " + jnlpInfo.jNLPAppletDescInfo.parameters);

		for (JNLPResourceInfo jnlpResourceInfo : jnlpInfo.resources)
			System.out.println(MessageFormat.format("resource: href={0}, os={1}, native={2}", jnlpResourceInfo.href, jnlpResourceInfo.os, jnlpResourceInfo.nativeLib));
	}

	// <extension name="Scenario-0.5"
	// href="http://download.java.net/javadesktop/scenario/releases/0.5/Scenario-0.5.jnlp"/>
	
	@Test
	public void testParseWithExtension() throws Exception {
		
		final String jnlpUrl = "http://someplace.org/releases/test-with-extensions.jnlp";
		final URL url = new URL("file:");

		final URLBuilder urlBuilder = mockery.mock(URLBuilder.class);
		
		JNLPParser jnlpParser = new JNLPParser();
		jnlpParser.setUrlBuilder(urlBuilder);
		
		mockery.checking(new Expectations() {
			{
				oneOf(urlBuilder).build(jnlpUrl);
				will(returnValue(url));

				oneOf(urlBuilder).open(url);
				will(returnValue(Thread.currentThread().getContextClassLoader().getResourceAsStream("test-with-extensions.jnlp")));
			}
		});
		
		JNLPInfo jnlpInfo = jnlpParser.parseJnlp(jnlpUrl);
		assertThat(jnlpInfo, IsNull.notNullValue());
		
		printJnlpInfo(jnlpInfo);
	}

}
