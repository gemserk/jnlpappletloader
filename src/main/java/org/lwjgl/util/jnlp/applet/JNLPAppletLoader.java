package org.lwjgl.util.jnlp.applet;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.applet.AppletLoader;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JNLPAppletLoader extends Applet implements AppletStub {

	private static final long serialVersionUID = -2459790398016588477L;

	private JNLPInfo jnlpInfo;

	URL codeBase;

	Map<String, String> appletParameters = new HashMap<String, String>();

	static String jnlpParameterName = "al_jnlp";

	private JNLPParser jnlpParser = new JNLPParser();

	private URLBuilder urlBuilder = new URLBuilder();

	public void setJnlpParser(JNLPParser jnlpParser) {
		this.jnlpParser = jnlpParser;
	}

	public void setUrlBuilder(URLBuilder urlBuilder) {
		this.urlBuilder = urlBuilder;
	}
	
	@Override
	public void init() {

		// starts using the default codebase
		codeBase = super.getCodeBase();

		String jnlpHref = getParameter(jnlpParameterName);

		if (jnlpHref == null)
			throw new RuntimeException("Missing required parameter " + jnlpParameterName);

		// URL jnlpUrl = new URL(codeBase, jnlpHref);
		URL jnlpUrl = urlBuilder.build(codeBase, jnlpHref);

		jnlpInfo = jnlpParser.parseJnlp(jnlpUrl);

		// replaces codebase with jnlp codebase
		// codeBase = new URL(jnlpInfo.codeBase);
		codeBase = urlBuilder.build(codeBase, jnlpInfo.codeBase);

		appletParameters.putAll(getAppletParametersFromJnlpInfo(jnlpInfo));

		System.out.println(appletParameters);

		AppletLoader appletLoader = new AppletLoader();
		appletLoader.setStub(this);

		appletLoader.init();
		appletLoader.start();

		setLayout(new BorderLayout());

		this.add(appletLoader);

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

	protected static String getJarsForOsStartingWith(List<JNLPInfo.JNLPResourceInfo> resources, String os, boolean nativeLib) {

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < resources.size(); i++) {
			JNLPInfo.JNLPResourceInfo jNLPResourceInfo = resources.get(i);

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

	static void getResourcesInfo(JNLPInfo jNLPInfo, Node resourcesNode) {

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
		jNLPInfo.resources.add(new JNLPInfo.JNLPResourceInfo(hrefAttribute.getNodeValue(), os, true));
	}

	private static void getJarInfo(JNLPInfo jNLPInfo, Node childNode, String os) {
		NamedNodeMap attributes = childNode.getAttributes();
		Node hrefAttribute = attributes.getNamedItem("href");
		jNLPInfo.resources.add(new JNLPInfo.JNLPResourceInfo(hrefAttribute.getNodeValue(), os, false));
	}

}
