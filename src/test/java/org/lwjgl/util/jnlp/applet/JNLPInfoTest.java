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

	// @Test
	// public void testMergeJnlp() {
	//		
	// JNLPInfo jnlpInfo = new JNLPInfo();
	// jnlpInfo.codeBase = "http://localhost";
	// jnlpInfo.resources.add(new JNLPResourceInfo("lwjgl.jar", "", ResourceType.Jar));
	// jnlpInfo.resources.add(new JNLPResourceInfo("lwjgl-util.jar", "", ResourceType.Jar));
	//		
	// JNLPInfo extensionJnlpInfo = new JNLPInfo();
	// extensionJnlpInfo.codeBase = "http://someplace.net";
	// extensionJnlpInfo.resources.add(new JNLPResourceInfo("jinput.jar", "", ResourceType.Jar));
	//		
	// jnlpInfo.merge(extensionJnlpInfo);
	//
	// assertThat(jnlpInfo.resources.size(), IsEqual.equalTo(3));
	// assertThat(jnlpInfo.resources, IsCollectionContaining.hasItem(jnlpInfo.resources.get(0)));
	// assertThat(jnlpInfo.resources, IsCollectionContaining.hasItem(jnlpInfo.resources.get(1)));
	// assertThat(jnlpInfo.resources.get(2).href, IsEqual.equalTo("http://someplace.net/jinput.jar"));
	//		
	// }
}
