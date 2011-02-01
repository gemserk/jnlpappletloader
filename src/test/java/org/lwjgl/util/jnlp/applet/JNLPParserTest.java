package org.lwjgl.util.jnlp.applet;

import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;

public class JNLPParserTest {

	@Test
	public void testParseJnlpFile() throws Exception {

		URL jnlpUrl = Thread.currentThread().getContextClassLoader().getResource("applet.jnlp");

		InputStream jnlpInputStream = jnlpUrl.openStream();

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(jnlpInputStream);

		// Document document = documentBuilder.parse(jnlpFile);

		JNLPInfo jNLPInfo = new JNLPParser().parse(document);
		// JNLPInfo jNLPInfo = new JNLPParser(document).parse();

		System.out.println("codeBase: " + jNLPInfo.codeBase);
		System.out.println("applet.mainClass: " + jNLPInfo.jNLPAppletDescInfo.mainClassName);
		System.out.println("applet.name: " + jNLPInfo.jNLPAppletDescInfo.name);
		System.out.println("applet.parameters: " + jNLPInfo.jNLPAppletDescInfo.parameters);

		for (JNLPInfo.JNLPResourceInfo jNLPResourceInfo : jNLPInfo.resources)
			System.out.println(MessageFormat.format("resource: href={0}, os={1}, native={2}", jNLPResourceInfo.href, jNLPResourceInfo.os, jNLPResourceInfo.nativeLib));

		jnlpInputStream.close();
	}

}
