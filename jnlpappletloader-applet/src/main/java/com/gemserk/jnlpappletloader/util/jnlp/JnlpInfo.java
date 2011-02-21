package com.gemserk.jnlpappletloader.util.jnlp;

import java.util.ArrayList;
import java.util.List;

import com.gemserk.jnlpappletloader.util.jnlp.JnlpResourceInfo.ResourceType;



/**
 * Has the info of the JNLP file.
 */
public class JnlpInfo {

	public String codeBase;

	public JnlpAppletDescInfo jnlpAppletDescInfo;

	public List<JnlpResourceInfo> resources = new ArrayList<JnlpResourceInfo>();
	
	public boolean hasExtensions() {
		for (int i = 0; i < resources.size(); i++) {
			if (resources.get(i).type == ResourceType.Extension)
				return true;
		}
		return false;
	}

	public JnlpResourceInfo getFirstResource(ResourceType type) {
		for (int i = 0; i < resources.size(); i++) {
			JnlpResourceInfo jnlpResourceInfo = resources.get(i);
			if (jnlpResourceInfo.type == type)
				return jnlpResourceInfo;
		}
		return null;
	}

	public void removeResourceInfo(JnlpResourceInfo jnlpResourceInfo) {
		resources.remove(jnlpResourceInfo);
	}

}