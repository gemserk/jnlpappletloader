package org.lwjgl.util.applet;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.util.applet.tests.JnlpParserTest;
import org.lwjgl.util.applet.tests.JnlpParserTest.JarInfo;
import org.lwjgl.util.applet.tests.JnlpParserTest.JnlpInfo;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class RealJnlpAppletLoader extends Applet implements AppletStub {

	private static final long serialVersionUID = -2459790398016588477L;

	private JnlpInfo jnlpInfo;

	private URL codeBase;

	Map<String, String> appletParameters = new HashMap<String, String>();

	@Override
	public void init() {

		// read and parses a jnlp from a parameter ....

		// redirects to another applet, setting new parameters using jnlp info

		// Container parent = getParent();
		// System.out.println(parent);

		try {
			String jnlpHref = getParameter("al_jnlp");
			// String al_jnlp = "http://acoppes-laptop.local/prototipos/discoverthename-test/launch-applet.jnlp";

			URL jnlpUrl = new URL(jnlpHref);

			InputStream jnlpInputStream = jnlpUrl.openStream();

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(jnlpInputStream);

			jnlpInfo = JnlpParserTest.parse(document);

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

			codeBase = new URL(jnlpInfo.codeBase);

			// al_jars = jnlpInfo.appletDescInfo.mainClassName;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AppletLoader appletLoader = new AppletLoader();
		appletLoader.setStub(this);

		appletLoader.init();
		appletLoader.start();

		setLayout(new BorderLayout());

		this.add(appletLoader);

		// parent.add(appletLoader);
		// parent.remove(this);

	}

	private String getJars(List<JarInfo> resources, String os, boolean nativeLib) {

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < resources.size(); i++) {
			JarInfo jarInfo = resources.get(i);

			if (jarInfo.nativeLib != nativeLib)
				continue;

			// starts with?
			if (!"".equals(os) && !os.equals(jarInfo.os))
				continue;

			stringBuilder.append(jarInfo.href);
			stringBuilder.append(", ");
		}

		if (stringBuilder.length() > 0)
			stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");

		String al_jars = stringBuilder.toString();
		return al_jars;
	}

	@Override
	public void appletResize(int width, int height) {

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

}
