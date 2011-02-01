package org.lwjgl.util.applet.tests;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JnlpParserTest {

	public static class AppletDescInfo {

		public String mainClassName;

		public String name;

		//

		public Map<String, String> parameters;

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

	public static JnlpInfo parse(Document jnlpDocument) {
		JnlpInfo jnlpInfo = new JnlpInfo();

		NodeList jnlpElements = jnlpDocument.getElementsByTagName("jnlp");

		if (jnlpElements.getLength() == 0)
			throw new RuntimeException("Document must have jnlp tag");

		Node jnlpElement = jnlpElements.item(0);

		jnlpInfo.codeBase = jnlpElement.getAttributes().getNamedItem("codebase").getNodeValue();

		jnlpInfo.resources = new ArrayList<JarInfo>();

		NodeList childNodes = jnlpElement.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {

			Node childNode = childNodes.item(i);

			if ("resources".equals(childNode.getNodeName())) {
				getResourcesInfo(jnlpInfo, childNode);
			}

		}

		// jnlpInfo.resources.add(new ResourceInfo("aopalliance-1.0.jar"));
		// jnlpInfo.resources.add(new ResourceInfo("asm-3.1.jar"));
		// jnlpInfo.resources.add(new ResourceInfo("jnlpappletloader-0.0.1-SNAPSHOT-signed.jar"));

		NodeList appletDescElements = jnlpDocument.getElementsByTagName("applet-desc");

		if (appletDescElements.getLength() == 0)
			return jnlpInfo;

		jnlpInfo.appletDescInfo = getAppletDescInfo(appletDescElements.item(0));

		return jnlpInfo;
	}

	private static void getResourcesInfo(JnlpInfo jnlpInfo, Node resourcesNode) {

		NamedNodeMap attributes = resourcesNode.getAttributes();

		String os = "";
		Node osAttribute = attributes.getNamedItem("os");

		if (osAttribute != null)
			os = osAttribute.getNodeValue();

		NodeList childNodes = resourcesNode.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {

			Node childNode = childNodes.item(i);

			if ("jar".equals(childNode.getNodeName()))
				getJarInfo(jnlpInfo, childNode, os);

			if ("nativelib".equals(childNode.getNodeName()))
				getNativeLibInfo(jnlpInfo, childNode, os);

		}

	}

	private static void getNativeLibInfo(JnlpInfo jnlpInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jnlpInfo.resources.add(new JarInfo(hrefAttribute.getNodeValue(), os, true));
	}

	private static void getJarInfo(JnlpInfo jnlpInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jnlpInfo.resources.add(new JarInfo(hrefAttribute.getNodeValue(), os, false));
	}

	private static AppletDescInfo getAppletDescInfo(Node appletDescElement) {
		NamedNodeMap attributes = appletDescElement.getAttributes();

		String name = attributes.getNamedItem("name").getNodeValue();
		String mainClass = attributes.getNamedItem("main-class").getNodeValue();

		AppletDescInfo appletDescInfo = new AppletDescInfo();
		appletDescInfo.mainClassName = mainClass;
		appletDescInfo.name = name;
		return appletDescInfo;
	}

}
