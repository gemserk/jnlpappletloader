package org.lwjgl.util.applet;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class JarDownloaderTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	
	@Test
	public void test() {
		
		JarDownloader jarDownloader = new JarDownloader();
		
		FileInfo fileInfo = new FileInfo("lwjgl.jar", 100, 100L);
		
		// jarDownloader.download(fileInfo);
		
	}


}
