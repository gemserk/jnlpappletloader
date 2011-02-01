package org.lwjgl.util.jnlp.applet;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo.ResourceType;

/**
 * Helper class to build LWJGL AppletLoader needed parameters from a JNLPInfo.
 * 
 * @author acoppes
 * 
 */
public class AppletLoaderParametersBuilder {

	private final JNLPInfo jnlpInfo;

	public AppletLoaderParametersBuilder(JNLPInfo jnlpInfo) {
		this.jnlpInfo = jnlpInfo;
	}

	public Map<String, String> getParametersFromJnlpInfo() {
		Map<String, String> appletParameters = new HashMap<String, String>();

		if (jnlpInfo.jnlpAppletDescInfo != null) {
			appletParameters.putAll(jnlpInfo.jnlpAppletDescInfo.parameters);
			appletParameters.put("al_main", jnlpInfo.jnlpAppletDescInfo.mainClassName);
			appletParameters.put("al_title", jnlpInfo.jnlpAppletDescInfo.name);
		}

		String al_jars = getJarsForOsStartingWith(jnlpInfo, "", ResourceType.Jar);
		System.out.println("jars: " + al_jars);
		appletParameters.put("al_jars", al_jars);

		addNativesFor(appletParameters, "Windows", "al_windows");
		addNativesFor(appletParameters, "Linux", "al_linux");
		addNativesFor(appletParameters, "Mac OS", "al_mac");
		addNativesFor(appletParameters, "Solaris", "al_solaris");
		addNativesFor(appletParameters, "FreeBSD", "al_freebsd");

		return appletParameters;
	}

	protected void addNativesFor(Map<String, String> appletParameters, String os, String appletParameter) {
		String parameter = getJarsForOsStartingWith(jnlpInfo, os, ResourceType.NativeLib);
		if ("".equals(parameter.trim())) {
			System.out.println(os + " has no natives");
			return;
		}
		System.out.println(os + " natives: " + parameter);
		appletParameters.put(appletParameter, parameter);
	}

	protected String getJarsForOsStartingWith(JNLPInfo jnlpInfo, String os, ResourceType type) {

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < jnlpInfo.resources.size(); i++) {
			JNLPResourceInfo jnlpResourceInfo = jnlpInfo.resources.get(i);

			if (jnlpResourceInfo.type != type)
				continue;

			if (!jnlpResourceInfo.os.toLowerCase().startsWith(os.toLowerCase()))
				continue;

			if (this.jnlpInfo == jnlpInfo)
				stringBuilder.append(jnlpResourceInfo.href);
			else
				stringBuilder.append(jnlpInfo.codeBase + jnlpResourceInfo.href);
			stringBuilder.append(", ");
		}

		if (stringBuilder.length() > 0 && jnlpInfo.extensions.size() == 0)
			stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");

		for (int i = 0; i < jnlpInfo.extensions.size(); i++) {
			JNLPInfo extensionJnlpInfo = jnlpInfo.extensions.get(i);
			stringBuilder.append(getJarsForOsStartingWith(extensionJnlpInfo, os, type));
		}

		return stringBuilder.toString();
	}

}