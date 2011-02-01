package org.lwjgl.util.jnlp.applet;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsSame;
import org.junit.Test;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo;
import org.lwjgl.util.jnlp.applet.JNLPInfo.JNLPResourceInfo.ResourceType;

public class JNLPInfoTest {

	@Test
	public void testHasExtensions() {
		JNLPInfo jnlpInfo = new JNLPInfo();
		assertThat(jnlpInfo.hasExtensions(), IsEqual.equalTo(false));
		jnlpInfo.resources.add(new JNLPResourceInfo("http://someplace.net", "", ResourceType.Extension));
		assertThat(jnlpInfo.hasExtensions(), IsEqual.equalTo(true));
	}

	@Test
	public void testGetFirstResourceMatching() {
		JNLPInfo jnlpInfo = new JNLPInfo();

		JNLPResourceInfo first = new JNLPResourceInfo("http://someplace.net/1", "", ResourceType.Extension);
		JNLPResourceInfo second = new JNLPResourceInfo("http://someplace.net/2", "", ResourceType.Extension);

		jnlpInfo.resources.add(first);
		jnlpInfo.resources.add(second);

		assertThat(jnlpInfo.getFirstResource(ResourceType.Extension), IsSame.sameInstance(first));
	}

	@Test
	public void testRemoveResourceInfo() {
		JNLPInfo jnlpInfo = new JNLPInfo();

		JNLPResourceInfo first = new JNLPResourceInfo("http://someplace.net/1", "", ResourceType.Extension);
		JNLPResourceInfo second = new JNLPResourceInfo("http://someplace.net/2", "", ResourceType.Extension);

		jnlpInfo.resources.add(first);
		jnlpInfo.resources.add(second);

		jnlpInfo.removeResourceInfo(first);
		assertThat(jnlpInfo.getFirstResource(ResourceType.Extension), IsSame.sameInstance(second));
	}

}
