package org.lwjgl.util.jnlp.applet;

import java.net.URL;

import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo.ResourceType;

public class JnlpMerger {

	URLBuilder urlBuilder;

	JNLPParser jnlpParser;

	public void setJnlpParser(JNLPParser jnlpParser) {
		this.jnlpParser = jnlpParser;
	}

	public void setUrlBuilder(URLBuilder urlBuilder) {
		this.urlBuilder = urlBuilder;
	}

	public void mergeWithExtensions(JNLPInfo jnlpInfo, URL codeBase) {

		while (jnlpInfo.hasExtensions()) {
			JNLPResourceInfo extensionResourceInfo = jnlpInfo.getFirstResource(ResourceType.Extension);
			jnlpInfo.removeResourceInfo(extensionResourceInfo);

			URL extensionUrl = urlBuilder.build(codeBase, extensionResourceInfo.href);
			JNLPInfo extensionJnlpInfo = jnlpParser.parseJnlp(extensionUrl);
			mergeWithExtensions(extensionJnlpInfo, extensionUrl);

			URL extensionCodeBase = urlBuilder.build(extensionJnlpInfo.codeBase);
			for (int i = 0; i < extensionJnlpInfo.resources.size(); i++) {
				JNLPResourceInfo resourceInfo = extensionJnlpInfo.resources.get(i);
				URL resourceUrl = urlBuilder.build(extensionCodeBase, resourceInfo.href);
				jnlpInfo.resources.add(new JNLPResourceInfo(resourceUrl.toString(), resourceInfo.os, resourceInfo.type));
			}
		}

	}

}