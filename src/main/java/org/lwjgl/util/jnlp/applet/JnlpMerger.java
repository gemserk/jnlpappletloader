package org.lwjgl.util.jnlp.applet;

import java.net.URL;

import org.lwjgl.util.jnlp.applet.JnlpInfo.JnlpResourceInfo;
import org.lwjgl.util.jnlp.applet.JnlpInfo.JnlpResourceInfo.ResourceType;

public class JnlpMerger {

	UrlBuilder urlBuilder;

	JnlpParser jnlpParser;

	public void setJnlpParser(JnlpParser jnlpParser) {
		this.jnlpParser = jnlpParser;
	}

	public void setUrlBuilder(UrlBuilder urlBuilder) {
		this.urlBuilder = urlBuilder;
	}

	public void mergeWithExtensions(JnlpInfo jnlpInfo, URL codeBase) {

		while (jnlpInfo.hasExtensions()) {
			JnlpResourceInfo extensionResourceInfo = jnlpInfo.getFirstResource(ResourceType.Extension);
			jnlpInfo.removeResourceInfo(extensionResourceInfo);

			URL extensionUrl = urlBuilder.build(codeBase, extensionResourceInfo.href);
			JnlpInfo extensionJnlpInfo = jnlpParser.parseJnlp(extensionUrl);
			mergeWithExtensions(extensionJnlpInfo, extensionUrl);

			URL extensionCodeBase = urlBuilder.build(extensionJnlpInfo.codeBase);
			for (int i = 0; i < extensionJnlpInfo.resources.size(); i++) {
				JnlpResourceInfo resourceInfo = extensionJnlpInfo.resources.get(i);
				URL resourceUrl = urlBuilder.build(extensionCodeBase, resourceInfo.href);
				jnlpInfo.resources.add(new JnlpResourceInfo(resourceUrl.toString(), resourceInfo.os, resourceInfo.type));
			}
		}

	}

}