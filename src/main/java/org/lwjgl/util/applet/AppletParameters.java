package org.lwjgl.util.applet;

import java.util.List;

public class AppletParameters {

	String main;

	List<String> jars;

	String logo;

	String progessbar;

	public void setMain(String main) {
		this.main = main;
	}

	public String getMain() {
		return main;
	}

	public List<String> getJars() {
		return jars;
	}

	public void setJars(List<String> jars) {
		this.jars = jars;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getProgessbar() {
		return progessbar;
	}

	public void setProgessbar(String progessbar) {
		this.progessbar = progessbar;
	}

}