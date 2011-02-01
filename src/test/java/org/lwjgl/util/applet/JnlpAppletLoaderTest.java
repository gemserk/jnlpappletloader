package org.lwjgl.util.applet;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class JnlpAppletLoaderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void testGetCodeBasedUrlForJarList() throws MalformedURLException {
		JnlpAppletLoader jnlpAppletLoader = new JnlpAppletLoader();
		List<JnlpParser.JnlpJarInfo> resources = new ArrayList<JnlpParser.JnlpJarInfo>();
		jnlpAppletLoader.getJars(resources, "", false);
	}


}
