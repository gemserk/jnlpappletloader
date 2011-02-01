package org.lwjgl.util.applet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JnlpParser {

	public static class JnlpJarInfo {

		public String href;

		public String os;

		public boolean nativeLib;

		public JnlpJarInfo(String href, String os, boolean nativeLib) {
			this.href = href;
			this.os = os;
			this.nativeLib = nativeLib;
		}

	}

	public static class JnlpInfo {

		public String codeBase;

		public JnlpParser.AppletDescInfo appletDescInfo;

		public List<JnlpJarInfo> resources = new ArrayList<JnlpJarInfo>();

	}

	public static class AppletDescInfo {

		public String mainClassName;

		public String name;

		public Map<String, String> parameters = new HashMap<String, String>();

	}

	private final Document jnlpDocument;

	public JnlpParser(Document document) {
		this.jnlpDocument = document;
	}

	public JnlpParser.JnlpInfo parse() {
		JnlpInfo jnlpInfo = new JnlpInfo();

		NodeList jnlpElements = jnlpDocument.getElementsByTagName("jnlp");

		if (jnlpElements.getLength() == 0)
			throw new RuntimeException("Document must have jnlp tag");

		Node jnlpElement = jnlpElements.item(0);

		jnlpInfo.codeBase = jnlpElement.getAttributes().getNamedItem("codebase").getNodeValue();

		// jnlpInfo.resources = new ArrayList<JnlpJarInfo>();

		NodeList childNodes = jnlpElement.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {

			Node childNode = childNodes.item(i);

			if ("resources".equals(childNode.getNodeName())) {
				getResourcesInfo(jnlpInfo, childNode);
			}

		}

		NodeList appletDescElements = jnlpDocument.getElementsByTagName("applet-desc");

		if (appletDescElements.getLength() == 0)
			return jnlpInfo;

		jnlpInfo.appletDescInfo = getAppletDescInfo(appletDescElements.item(0));

		return jnlpInfo;
	}

	private void getResourcesInfo(JnlpParser.JnlpInfo jnlpInfo, Node resourcesNode) {

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

	private void getNativeLibInfo(JnlpParser.JnlpInfo jnlpInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jnlpInfo.resources.add(new JnlpParser.JnlpJarInfo(hrefAttribute.getNodeValue(), os, true));
	}

	private void getJarInfo(JnlpParser.JnlpInfo jnlpInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jnlpInfo.resources.add(new JnlpParser.JnlpJarInfo(hrefAttribute.getNodeValue(), os, false));
	}

	private JnlpParser.AppletDescInfo getAppletDescInfo(Node appletDescElement) {
		NamedNodeMap attributes = appletDescElement.getAttributes();

		String name = attributes.getNamedItem("name").getNodeValue();
		String mainClass = attributes.getNamedItem("main-class").getNodeValue();

		JnlpParser.AppletDescInfo appletDescInfo = new JnlpParser.AppletDescInfo();
		appletDescInfo.mainClassName = mainClass;
		appletDescInfo.name = name;

		NodeList childNodes = appletDescElement.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {

			Node childNode = childNodes.item(i);

			if ("param".equals(childNode.getNodeName()))
				getParamInfo(appletDescInfo, childNode);

		}

		return appletDescInfo;
	}

	private void getParamInfo(JnlpParser.AppletDescInfo appletDescInfo, Node childNode) {
		NamedNodeMap attributes = childNode.getAttributes();
		String nameAttribute = attributes.getNamedItem("name").getNodeValue();
		String valueAttribute = attributes.getNamedItem("value").getNodeValue();
		appletDescInfo.parameters.put(nameAttribute, valueAttribute);
	}

}