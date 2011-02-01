package org.lwjgl.util.jnlp.applet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Has the info of the JNLP file.
 */
public class JNLPInfo {

	/**
	 * Info of a single resource, could be a jar or a nativelib.
	 */
	public static class JNLPResourceInfo {
	
		public String href;
	
		public String os;
	
		public boolean nativeLib;
	
		public JNLPResourceInfo(String href, String os, boolean nativeLib) {
			this.href = href;
			this.os = os;
			this.nativeLib = nativeLib;
		}
	
	}

	/**
	 * Has the information of the applet-desc of the JNLP.
	 */
	public static class JNLPAppletDescInfo {
	
		public String mainClassName;
	
		public String name;
	
		public Map<String, String> parameters = new HashMap<String, String>();
	
	}

	public String codeBase;

	public JNLPAppletDescInfo jNLPAppletDescInfo;

	public List<JNLPResourceInfo> resources = new ArrayList<JNLPResourceInfo>();

}