package org.lwjgl.util.jnlp.applet;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.util.jnlp.applet.JnlpInfo.JnlpAppletDescInfo;
import org.lwjgl.util.jnlp.applet.JnlpInfo.JnlpResourceInfo;
import org.lwjgl.util.jnlp.applet.JnlpInfo.JnlpResourceInfo.ResourceType;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JnlpParser {

	public JnlpInfo parseJnlp(InputStream is) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(is);

			return parse(document);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			closeStream(is);
		}
	}

	/**
	 * Parses the Document and returns the JnlpInfo with all the JNLP information.
	 * 
	 * @return a JnlpInfo with the JNLP information.
	 */
	protected JnlpInfo parse(Document jnlpDocument) {
		JnlpInfo jnlpInfo = new JnlpInfo();

		NodeList jnlpElements = jnlpDocument.getElementsByTagName("jnlp");

		if (jnlpElements.getLength() == 0)
			throw new RuntimeException("Failed to parse document, jnlp file should have jnlp tag");

		Node jnlpElement = jnlpElements.item(0);

		jnlpInfo.codeBase = jnlpElement.getAttributes().getNamedItem("codebase").getNodeValue();

		NodeList childNodes = jnlpElement.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {

			Node childNode = childNodes.item(i);

			if ("resources".equals(childNode.getNodeName())) 
				getResourcesInfo(jnlpInfo, childNode);

		}

		NodeList appletDescElements = jnlpDocument.getElementsByTagName("applet-desc");

		if (appletDescElements.getLength() == 0)
			return jnlpInfo;

		jnlpInfo.jnlpAppletDescInfo = getAppletDescInfo(appletDescElements.item(0));

		return jnlpInfo;
	}

	private void closeStream(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
		}
	}

	private JnlpAppletDescInfo getAppletDescInfo(Node appletDescElement) {
		NamedNodeMap attributes = appletDescElement.getAttributes();

		String name = attributes.getNamedItem("name").getNodeValue();
		String mainClass = attributes.getNamedItem("main-class").getNodeValue();

		JnlpInfo.JnlpAppletDescInfo jnlpAppletDescInfo = new JnlpInfo.JnlpAppletDescInfo();
		jnlpAppletDescInfo.mainClassName = mainClass;
		jnlpAppletDescInfo.name = name;

		NodeList childNodes = appletDescElement.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {

			Node childNode = childNodes.item(i);

			if ("param".equals(childNode.getNodeName()))
				getParamInfo(jnlpAppletDescInfo, childNode);

		}

		return jnlpAppletDescInfo;
	}

	private void getParamInfo(JnlpInfo.JnlpAppletDescInfo jnlpAppletDescInfo, Node childNode) {
		NamedNodeMap attributes = childNode.getAttributes();
		String nameAttribute = attributes.getNamedItem("name").getNodeValue();
		String valueAttribute = attributes.getNamedItem("value").getNodeValue();
		jnlpAppletDescInfo.parameters.put(nameAttribute, valueAttribute);
	}
	
	private void getResourcesInfo(JnlpInfo jnlpInfo, Node resourcesNode) {

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

			if ("extension".equals(childNode.getNodeName()))
				getExtensionInfo(jnlpInfo, childNode, os);

		}

	}

	private void getExtensionInfo(JnlpInfo jnlpInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jnlpInfo.resources.add(new JnlpResourceInfo(hrefAttribute.getNodeValue(), os, ResourceType.Extension));

		// NamedNodeMap attributes = childNode.getAttributes();
		// Node hrefAttribute = attributes.getNamedItem("href");
		//		
		// JNLPParser jnlpParser = new JNLPParser(urlBuilder);
		// jnlpParser.setUrlBuilder(urlBuilder);
		//		
		// URL codeBase = urlBuilder.build(jnlpInfo.codeBase);
		// JNLPInfo extensionJnlpInfo = jnlpParser.parseJnlp(urlBuilder.build(codeBase, hrefAttribute.getNodeValue()));
		//		
		// jnlpInfo.extensions.add(extensionJnlpInfo);
	}

	private void getNativeLibInfo(JnlpInfo jnlpInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jnlpInfo.resources.add(new JnlpResourceInfo(hrefAttribute.getNodeValue(), os, ResourceType.NativeLib));
	}

	private void getJarInfo(JnlpInfo jnlpInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jnlpInfo.resources.add(new JnlpResourceInfo(hrefAttribute.getNodeValue(), os, ResourceType.Jar));
	}

}