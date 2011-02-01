package org.lwjgl.util.jnlp.applet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo;

/**
 * Helper class to build lwjgl AppletLoader needed parameters.
 * 
 * @author acoppes
 *
 */
public class AppletLoaderParametersBuilder {

	private final JNLPInfo jnlpInfo;

	public AppletLoaderParametersBuilder(JNLPInfo jnlpInfo) {
		this.jnlpInfo = jnlpInfo;
	}

	public Map<String, String> getAppletParametersFromJnlpInfo() {
		Map<String, String> appletParameters = new HashMap<String, String>();
		appletParameters.putAll(jnlpInfo.jnlpAppletDescInfo.parameters);

		appletParameters.put("al_main", jnlpInfo.jnlpAppletDescInfo.mainClassName);
		appletParameters.put("al_title", jnlpInfo.jnlpAppletDescInfo.name);

		String al_jars = getJarsForOsStartingWith(jnlpInfo.resources, "", false);
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
		String parameter = getJarsForOsStartingWith(jnlpInfo.resources, os, true);
		if ("".equals(parameter.trim())) {
			System.out.println(os + " has no natives");
			return;
		}
		System.out.println(os + " natives: " + parameter);
		appletParameters.put(appletParameter, parameter);
	}

	protected String getJarsForOsStartingWith(List<JNLPResourceInfo> resources, String os, boolean nativeLib) {

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < resources.size(); i++) {
			JNLPResourceInfo jNLPResourceInfo = resources.get(i);

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

}