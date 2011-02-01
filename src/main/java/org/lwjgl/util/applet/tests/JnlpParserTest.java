package org.lwjgl.util.applet.tests;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.util.applet.JnlpParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JnlpParserTest {

	public static class AppletDescInfo {

		public String mainClassName;

		public String name;

		//

		public Map<String, String> parameters = new HashMap<String, String>();

	}

	public static class JarInfo {

		public String href;

		public String os;
		
		public boolean nativeLib;

		public JarInfo(String href, String os, boolean nativeLib) {
			this.href = href;
			this.os = os;
			this.nativeLib = nativeLib;
		}

	}

	public static class JnlpInfo {

		public String codeBase;

		public AppletDescInfo appletDescInfo;

		public List<JarInfo> resources;

	}

	public static void main(String[] args) throws Exception {

		URL jnlpUrl = new URL("http://acoppes-laptop.local/prototipos/discoverthename-test/launch-applet.jnlp");

		InputStream jnlpInputStream = jnlpUrl.openStream();

		// File jnlpFile = new File("/tmp/file.jnlp");
		//		
		// new FileUtils().copy(jnlpInputStream, new FileOutputStream(jnlpFile));

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(jnlpInputStream);
		// Document document = documentBuilder.parse(jnlpFile);

		NodeList jnlpElements = document.getElementsByTagName("jnlp");

		System.out.println(document.getTextContent());

		for (int i = 0; i < jnlpElements.getLength(); i++) {
			System.out.println(jnlpElements.item(i));
			System.out.println(jnlpElements.item(i).getAttributes().getNamedItem("spec"));
		}

		NodeList appletDescElements = document.getElementsByTagName("applet-desc");
		for (int i = 0; i < appletDescElements.getLength(); i++) {
			Node appletDescElement = appletDescElements.item(i);

			NamedNodeMap attributes = appletDescElement.getAttributes();

			String name = attributes.getNamedItem("name").getNodeValue();
			String mainClass = attributes.getNamedItem("main-class").getNodeValue();

			System.out.println("name = " + name);
			System.out.println("mainClass = " + mainClass);

			// applet parameters
			// NodeList appletParameterElements = appletDescElement.getChildNodes();

		}

	}

}
