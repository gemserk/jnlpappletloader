package org.lwjgl.util.applet;

import java.applet.Applet;

public class AppletParametersUtil {

	private final Applet applet;

	public AppletParametersUtil(Applet applet) {
		this.applet = applet;
	}

	public String getRequiredParameter(String parameterName) {
		String parameterValue = applet.getParameter(parameterName);
		if (parameterValue == null)
			throw new MissingRequiredParameterException(parameterName, "missing required parameter [" + parameterName + "]");
		return parameterValue;
	}
	
	protected String getParameter(String name) {
		return applet.getParameter(name);
	}
	
	protected String getParameter(String name, String defaultValue) {
		String parameter = getParameter(name);
		if (parameter == null)
			return defaultValue;
		return parameter;
	}

	protected boolean getBooleanParameter(String name, boolean defaultValue) {
		String parameter = getParameter(name, Boolean.valueOf(defaultValue).toString());
		if (parameter != null)
			return Boolean.parseBoolean(parameter);
		return defaultValue;
	}

}