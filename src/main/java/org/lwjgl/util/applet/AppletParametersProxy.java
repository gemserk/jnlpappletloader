package org.lwjgl.util.applet;

public class AppletParametersProxy {

	private AppletParametersUtil appletParametersUtil;

	public AppletParametersProxy(AppletParametersUtil appletParametersUtil) {
		this.appletParametersUtil = appletParametersUtil;
	}

	public AppletParameters getAppletParameters() {
		AppletParameters appletParameters = new AppletParameters();
		appletParameters.setMain(appletParametersUtil.getRequiredParameter("al_main"));
		appletParameters.setJars(appletParametersUtil.getParameter("al_jars", ""));
		appletParameters.setLogo(appletParametersUtil.getRequiredParameter("al_logo"));
		appletParameters.setProgessbar(appletParametersUtil.getRequiredParameter("al_progressbar"));
		return appletParameters;
	}
}