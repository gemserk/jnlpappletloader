package org.lwjgl.util.applet;

import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.lwjgl.util.applet.JnlpParser.JnlpInfo;
import org.lwjgl.util.applet.JnlpParser.JnlpJarInfo;
import org.w3c.dom.Document;

public class JnlpParserTest {

	@Test
	public void testParseJnlpFile() throws Exception {

		URL jnlpUrl = Thread.currentThread().getContextClassLoader().getResource("applet.jnlp");

		InputStream jnlpInputStream = jnlpUrl.openStream();

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(jnlpInputStream);
		
		// Document document = documentBuilder.parse(jnlpFile);

		JnlpInfo jnlpInfo = new JnlpParser(document).parse();
		
		System.out.println("codeBase: " + jnlpInfo.codeBase);
		System.out.println("applet.mainClass: " + jnlpInfo.appletDescInfo.mainClassName);
		System.out.println("applet.name: " + jnlpInfo.appletDescInfo.name);
		System.out.println("applet.parameters: " + jnlpInfo.appletDescInfo.parameters);
		
		for (JnlpJarInfo jnlpJarInfo : jnlpInfo.resources) 
			System.out.println(MessageFormat.format("resource: href={0}, os={1}, native={2}", jnlpJarInfo.href, jnlpJarInfo.os, jnlpJarInfo.nativeLib));
		
		jnlpInputStream.close();
	}

}
