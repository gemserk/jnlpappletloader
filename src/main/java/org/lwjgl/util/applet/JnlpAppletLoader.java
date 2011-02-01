package org.lwjgl.util.applet;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.util.applet.JnlpParser.JnlpInfo;
import org.w3c.dom.Document;

public class JnlpAppletLoader extends Applet implements AppletStub {

	private static final long serialVersionUID = -2459790398016588477L;

	private JnlpInfo jnlpInfo;

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

			jnlpInfo = new JnlpParser(document).parse();

			jnlpInputStream.close();

			// replaces codebase with jnlp codebase
			codeBase = new URL(jnlpInfo.codeBase);

			appletParameters.putAll(getAppletParametersFromJnlpInfo(jnlpInfo));

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

	protected Map<String, String> getAppletParametersFromJnlpInfo(JnlpParser.JnlpInfo jnlpInfo) {
		Map<String, String> appletParameters = new HashMap<String, String>();
		appletParameters.putAll(jnlpInfo.appletDescInfo.parameters);

		appletParameters.put("al_main", jnlpInfo.appletDescInfo.mainClassName);
		appletParameters.put("al_title", jnlpInfo.appletDescInfo.name);

		String al_jars = getJarsForOsStartingWith(jnlpInfo.resources, "", false);
		System.out.println("jars: " + al_jars);
		appletParameters.put("al_jars", al_jars);

		addNativesFor(jnlpInfo, appletParameters, "Windows", "al_windows");
		addNativesFor(jnlpInfo, appletParameters, "Linux", "al_linux");
		addNativesFor(jnlpInfo, appletParameters, "Mac OS", "al_mac");
		addNativesFor(jnlpInfo, appletParameters, "Solaris", "al_solaris");
		addNativesFor(jnlpInfo, appletParameters, "FreeBSD", "al_freebsd");

		return appletParameters;
	}

	protected void addNativesFor(JnlpParser.JnlpInfo jnlpInfo, Map<String, String> appletParameters, String os, String appletParameter) {
		String parameter = getJarsForOsStartingWith(jnlpInfo.resources, os, true);
		if ("".equals(parameter.trim())) {
			System.out.println(os + " has no natives");
			return;
		}
		System.out.println(os + " natives: " + parameter);
		appletParameters.put(appletParameter, parameter);
	}

	protected String getJarsForOsStartingWith(List<JnlpParser.JnlpJarInfo> resources, String os, boolean nativeLib) {

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < resources.size(); i++) {
			JnlpParser.JnlpJarInfo jnlpJarInfo = resources.get(i);

			if (jnlpJarInfo.nativeLib != nativeLib)
				continue;

			if (!jnlpJarInfo.os.toLowerCase().startsWith(os.toLowerCase()))
				continue;

			stringBuilder.append(jnlpJarInfo.href);
			stringBuilder.append(", ");
		}

		if (stringBuilder.length() > 0)
			stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");

		return stringBuilder.toString();
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

	/**
	 * Returns an URL using the context set.
	 * 
	 * @param url
	 *            a String with the path of the URL to build, could be relative or absolute, if absolute then context is not used.
	 * @return an URL which could be relative to context or absolute.
	 */
	public URL build(String url) {
		try {
			return new URL(codeBase, url);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Failed to create url for " + url, e);
		}
	}
}
