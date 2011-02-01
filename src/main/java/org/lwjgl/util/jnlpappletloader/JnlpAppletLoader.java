package org.lwjgl.util.jnlpappletloader;

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

import org.lwjgl.util.applet.AppletLoader;
import org.lwjgl.util.jnlpappletloader.exceptions.MissingRequiredParameterException;
import org.w3c.dom.Document;

public class JnlpAppletLoader extends Applet implements AppletStub {

	private static final long serialVersionUID = -2459790398016588477L;

	private JnlpParser.JnlpInfo jnlpInfo;

	private URL codeBase;

	Map<String, String> appletParameters = new HashMap<String, String>();

	UrlBuilder urlBuilder;

	static String jnlpParameterName = "al_jnlp";

	@Override
	public void init() {

		initCodeBase();

		urlBuilder = new UrlBuilder(getCodeBase());

		String jnlpHref = getParameter(jnlpParameterName);

		if (jnlpHref == null)
			throw new MissingRequiredParameterException(jnlpParameterName);

		try {
			URL jnlpUrl = urlBuilder.build(jnlpHref);

			InputStream jnlpInputStream = jnlpUrl.openStream();

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(jnlpInputStream);

			jnlpInfo = new JnlpParser(document).parse();

			setCodeBase(urlBuilder.build(jnlpInfo.codeBase));

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

	private void initCodeBase() {
		String codeBaseParameter = getParameter("al_codebase");

		if (codeBaseParameter == null) {
			setCodeBase(super.getCodeBase());
		} else {
			try {
				setCodeBase(new URL(codeBaseParameter));
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Map<String, String> getAppletParametersFromJnlpInfo(JnlpParser.JnlpInfo jnlpInfo) {
		Map<String, String> appletParameters = new HashMap<String, String>();
		appletParameters.putAll(jnlpInfo.appletDescInfo.parameters);

		appletParameters.put("al_main", jnlpInfo.appletDescInfo.mainClassName);
		appletParameters.put("al_title", jnlpInfo.appletDescInfo.name);

		String al_jars = getJars(jnlpInfo.resources, "", false);
		System.out.println("jars: " + al_jars);
		appletParameters.put("al_jars", al_jars);

		String al_linux = getJars(jnlpInfo.resources, "Linux", true);
		System.out.println("Linux natives: " + al_linux);
		appletParameters.put("al_linux", al_linux);

		String al_windows = getJars(jnlpInfo.resources, "Windows", true);
		System.out.println("Windows natives: " + al_windows);
		appletParameters.put("al_windows", al_windows);

		String al_mac = getJars(jnlpInfo.resources, "Mac OS", true);
		System.out.println("Mac OS natives: " + al_mac);
		appletParameters.put("al_mac", al_mac);
		return appletParameters;
	}

	public String getJars(List<JnlpParser.JnlpJarInfo> resources, String os, boolean nativeLib) {

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < resources.size(); i++) {
			JnlpParser.JnlpJarInfo jnlpJarInfo = resources.get(i);

			if (jnlpJarInfo.nativeLib != nativeLib)
				continue;

			// starts with?
			if (!"".equals(os) && !os.equals(jnlpJarInfo.os))
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

	public void setCodeBase(URL codeBase) {
		this.codeBase = codeBase;
	}

	@Override
	public String getParameter(String name) {

		if (appletParameters.containsKey(name))
			return appletParameters.get(name);

		return super.getParameter(name);
	}

}
