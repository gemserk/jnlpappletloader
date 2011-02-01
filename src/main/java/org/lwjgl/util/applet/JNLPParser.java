package org.lwjgl.util.applet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides a way to parse a JNLP file and produce a class with the file info.
 * 
 * @author acoppes
 */
public class JNLPParser {

	/**
	 * Info of a single resource, could be a jar or a nativelib.
	 */
	public static class JNLPResourceInfo {

		public String href;

		public String os;

		public boolean nativeLib;

		public JNLPResourceInfo(String href, String os, boolean nativeLib) {
			this.href = href;
			this.os = os;
			this.nativeLib = nativeLib;
		}

	}

	/**
	 * Has the info of the JNLP file.
	 */
	public static class JNLPInfo {

		public String codeBase;

		public JNLPParser.JNLPAppletDescInfo jNLPAppletDescInfo;

		public List<JNLPResourceInfo> resources = new ArrayList<JNLPResourceInfo>();

	}

	/**
	 * Has the information of the applet-desc of the JNLP.
	 */
	public static class JNLPAppletDescInfo {

		public String mainClassName;

		public String name;

		public Map<String, String> parameters = new HashMap<String, String>();

	}

	private final Document jnlpDocument;

	public JNLPParser(Document document) {
		this.jnlpDocument = document;
	}

	/**
	 * Parses the Document and returns the JnlpInfo with all the JNLP information.
	 * 
	 * @return a JnlpInfo with the JNLP information.
	 */
	public JNLPInfo parse() {
		JNLPInfo jnlpInfo = new JNLPInfo();

		NodeList jnlpElements = jnlpDocument.getElementsByTagName("jnlp");

		if (jnlpElements.getLength() == 0)
			throw new RuntimeException("Document must have jnlp tag");

		Node jnlpElement = jnlpElements.item(0);

		jnlpInfo.codeBase = jnlpElement.getAttributes().getNamedItem("codebase").getNodeValue();

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

		jnlpInfo.jNLPAppletDescInfo = getAppletDescInfo(appletDescElements.item(0));

		return jnlpInfo;
	}

	private void getResourcesInfo(JNLPParser.JNLPInfo jNLPInfo, Node resourcesNode) {

		NamedNodeMap attributes = resourcesNode.getAttributes();

		String os = "";
		Node osAttribute = attributes.getNamedItem("os");

		if (osAttribute != null)
			os = osAttribute.getNodeValue();

		NodeList childNodes = resourcesNode.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {

			Node childNode = childNodes.item(i);

			if ("jar".equals(childNode.getNodeName()))
				getJarInfo(jNLPInfo, childNode, os);

			if ("nativelib".equals(childNode.getNodeName()))
				getNativeLibInfo(jNLPInfo, childNode, os);

		}

	}

	private void getNativeLibInfo(JNLPParser.JNLPInfo jNLPInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jNLPInfo.resources.add(new JNLPParser.JNLPResourceInfo(hrefAttribute.getNodeValue(), os, true));
	}

	private void getJarInfo(JNLPParser.JNLPInfo jNLPInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jNLPInfo.resources.add(new JNLPParser.JNLPResourceInfo(hrefAttribute.getNodeValue(), os, false));
	}

	private JNLPParser.JNLPAppletDescInfo getAppletDescInfo(Node appletDescElement) {
		NamedNodeMap attributes = appletDescElement.getAttributes();

		String name = attributes.getNamedItem("name").getNodeValue();
		String mainClass = attributes.getNamedItem("main-class").getNodeValue();

		JNLPParser.JNLPAppletDescInfo jNLPAppletDescInfo = new JNLPParser.JNLPAppletDescInfo();
		jNLPAppletDescInfo.mainClassName = mainClass;
		jNLPAppletDescInfo.name = name;

		NodeList childNodes = appletDescElement.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {

			Node childNode = childNodes.item(i);

			if ("param".equals(childNode.getNodeName()))
				getParamInfo(jNLPAppletDescInfo, childNode);

		}

		return jNLPAppletDescInfo;
	}

	private void getParamInfo(JNLPParser.JNLPAppletDescInfo jNLPAppletDescInfo, Node childNode) {
		NamedNodeMap attributes = childNode.getAttributes();
		String nameAttribute = attributes.getNamedItem("name").getNodeValue();
		String valueAttribute = attributes.getNamedItem("value").getNodeValue();
		jNLPAppletDescInfo.parameters.put(nameAttribute, valueAttribute);
	}

}