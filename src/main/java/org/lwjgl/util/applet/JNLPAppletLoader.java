package org.lwjgl.util.applet;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JNLPAppletLoader extends Applet implements AppletStub {

	/**
	 * Has the information of the applet-desc of the JNLP.
	 */
	public static class JNLPAppletDescInfo {
	
		public String mainClassName;
	
		public String name;
	
		public Map<String, String> parameters = new HashMap<String, String>();
	
	}

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
	
		public JNLPAppletDescInfo jNLPAppletDescInfo;
	
		public List<JNLPResourceInfo> resources = new ArrayList<JNLPResourceInfo>();
	
	}

	private static final long serialVersionUID = -2459790398016588477L;

	private JNLPAppletLoader.JNLPInfo jNLPInfo;

	URL codeBase;

	Map<String, String> appletParameters = new HashMap<String, String>();

	static String jnlpParameterName = "al_jnlp";

	@Override
	public void init() {

		// starts using the default codebase
		codeBase = super.getCodeBase();

		String jnlpHref = getParameter(jnlpParameterName);

		if (jnlpHref == null)
			throw new RuntimeException("Missing required parameter " + jnlpParameterName);

		try {
			URL jnlpUrl = new URL(codeBase, jnlpHref);

			InputStream jnlpInputStream = jnlpUrl.openStream();

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(jnlpInputStream);

			jNLPInfo = JNLPAppletLoader.parse(document);

			jnlpInputStream.close();

			// replaces codebase with jnlp codebase
			codeBase = new URL(jNLPInfo.codeBase);

			appletParameters.putAll(getAppletParametersFromJnlpInfo(jNLPInfo));

			System.out.println(appletParameters);

			AppletLoader appletLoader = new AppletLoader();
			appletLoader.setStub(this);

			appletLoader.init();
			appletLoader.start();

			setLayout(new BorderLayout());

			this.add(appletLoader);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	@Override
	public void appletResize(int width, int height) {
		resize(width, height);
	}

	@Override
	public URL getCodeBase() {
		return codeBase;
	}

	@Override
	public String getParameter(String name) {
		if (appletParameters.containsKey(name))
			return appletParameters.get(name);
		return super.getParameter(name);
	}
	
	// Helper methods

	protected static Map<String, String> getAppletParametersFromJnlpInfo(JNLPInfo jNLPInfo) {
		Map<String, String> appletParameters = new HashMap<String, String>();
		appletParameters.putAll(jNLPInfo.jNLPAppletDescInfo.parameters);

		appletParameters.put("al_main", jNLPInfo.jNLPAppletDescInfo.mainClassName);
		appletParameters.put("al_title", jNLPInfo.jNLPAppletDescInfo.name);

		String al_jars = getJarsForOsStartingWith(jNLPInfo.resources, "", false);
		System.out.println("jars: " + al_jars);
		appletParameters.put("al_jars", al_jars);

		addNativesFor(jNLPInfo, appletParameters, "Windows", "al_windows");
		addNativesFor(jNLPInfo, appletParameters, "Linux", "al_linux");
		addNativesFor(jNLPInfo, appletParameters, "Mac OS", "al_mac");
		addNativesFor(jNLPInfo, appletParameters, "Solaris", "al_solaris");
		addNativesFor(jNLPInfo, appletParameters, "FreeBSD", "al_freebsd");

		return appletParameters;
	}

	protected static void addNativesFor(JNLPInfo jNLPInfo, Map<String, String> appletParameters, String os, String appletParameter) {
		String parameter = getJarsForOsStartingWith(jNLPInfo.resources, os, true);
		if ("".equals(parameter.trim())) {
			System.out.println(os + " has no natives");
			return;
		}
		System.out.println(os + " natives: " + parameter);
		appletParameters.put(appletParameter, parameter);
	}

	protected static String getJarsForOsStartingWith(List<JNLPAppletLoader.JNLPResourceInfo> resources, String os, boolean nativeLib) {

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < resources.size(); i++) {
			JNLPAppletLoader.JNLPResourceInfo jNLPResourceInfo = resources.get(i);

			if (jNLPResourceInfo.nativeLib != nativeLib)
				continue;

			if (!jNLPResourceInfo.os.toLowerCase().startsWith(os.toLowerCase()))
				continue;

			stringBuilder.append(jNLPResourceInfo.href);
			stringBuilder.append(", ");
		}

		if (stringBuilder.length() > 0)
			stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");

		return stringBuilder.toString();
	}

	// JNLP parse logic
	
	/**
	 * Parses the Document and returns the JnlpInfo with all the JNLP information.
	 * 
	 * @return a JnlpInfo with the JNLP information.
	 */
	public static JNLPInfo parse(Document jnlpDocument) {
		JNLPInfo jnlpInfo = new JNLPAppletLoader.JNLPInfo();

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

	private static void getResourcesInfo(JNLPInfo jNLPInfo, Node resourcesNode) {

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

	private static void getNativeLibInfo(JNLPInfo jNLPInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jNLPInfo.resources.add(new JNLPResourceInfo(hrefAttribute.getNodeValue(), os, true));
	}

	private static void getJarInfo(JNLPInfo jNLPInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jNLPInfo.resources.add(new JNLPResourceInfo(hrefAttribute.getNodeValue(), os, false));
	}

	private static JNLPAppletLoader.JNLPAppletDescInfo getAppletDescInfo(Node appletDescElement) {
		NamedNodeMap attributes = appletDescElement.getAttributes();

		String name = attributes.getNamedItem("name").getNodeValue();
		String mainClass = attributes.getNamedItem("main-class").getNodeValue();

		JNLPAppletDescInfo jNLPAppletDescInfo = new JNLPAppletDescInfo();
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

	private static void getParamInfo(JNLPAppletDescInfo jNLPAppletDescInfo, Node childNode) {
		NamedNodeMap attributes = childNode.getAttributes();
		String nameAttribute = attributes.getNamedItem("name").getNodeValue();
		String valueAttribute = attributes.getNamedItem("value").getNodeValue();
		jNLPAppletDescInfo.parameters.put(nameAttribute, valueAttribute);
	}
}
