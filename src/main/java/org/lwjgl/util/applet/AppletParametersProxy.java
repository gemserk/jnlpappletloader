package org.lwjgl.util.applet;

import java.net.URL;
import java.util.ArrayList;

public class AppletParametersProxy {

	private AppletParametersUtil appletParametersUtil;

	public AppletParametersProxy(AppletParametersUtil appletParametersUtil) {
		this.appletParametersUtil = appletParametersUtil;
	}

	public AppletParameters getAppletParameters() {
		AppletParameters appletParameters = new AppletParameters();
		
		String jars = appletParametersUtil.getParameter("al_jars", "");
		
		appletParameters.setMain(appletParametersUtil.getRequiredParameter("al_main"));
//		appletParameters.setJars(new JarUtil().getUrls(, jars));
		appletParameters.setJars(new ArrayList<URL>(new JarUtil().getUrls(appletParametersUtil.getCodeBase(), jars)));
		appletParameters.setLogo(appletParametersUtil.getRequiredParameter("al_logo"));
		appletParameters.setProgessbar(appletParametersUtil.getRequiredParameter("al_progressbar"));
		return appletParameters;
	}
}