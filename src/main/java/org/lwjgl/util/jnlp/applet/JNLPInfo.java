package org.lwjgl.util.jnlp.applet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo.ResourceType;


/**
 * Has the info of the JNLP file.
 */
public class JNLPInfo {

	/**
	 * Info of a single resource, could be a jar or a nativelib.
	 */
	public static class JNLPResourceInfo {
		
		public static enum ResourceType { Jar, NativeLib, Extension }
	
		public String href;
	
		public String os;
	
		public ResourceType type;
	
		public JNLPResourceInfo(String href, String os, ResourceType type) {
			this.href = href;
			this.os = os;
			this.type = type;
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

	public JNLPAppletDescInfo jnlpAppletDescInfo;

	public List<JNLPResourceInfo> resources = new ArrayList<JNLPResourceInfo>();
	
	public boolean hasExtensions() {
		for (int i = 0; i < resources.size(); i++) {
			if (resources.get(i).type == ResourceType.Extension)
				return true;
		}
		return false;
	}

	public JNLPResourceInfo getFirstResource(ResourceType type) {
		for (int i = 0; i < resources.size(); i++) {
			JNLPResourceInfo jnlpResourceInfo = resources.get(i);
			if (jnlpResourceInfo.type == type)
				return jnlpResourceInfo;
		}
		return null;
	}

	public void removeResourceInfo(JNLPResourceInfo jnlpResourceInfo) {
		resources.remove(jnlpResourceInfo);
	}

}