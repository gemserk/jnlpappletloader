package org.lwjgl.util.applet;

import static org.junit.Assert.assertThat;

import java.util.HashMap;

import org.hamcrest.core.IsEqual;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class CacheTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void shouldFailCacheWhenEntryNotFound() {
		Cache cache = new Cache(new HashMap<String, FileInfo>());
		assertThat(cache.isAlreadyDownloaded(new FileInfo("lwjgl.jar", 100, 1000L)), IsEqual.equalTo(false));
	}
	
	@Test
	public void shouldFailCacheWhenFoundButDifferentModifiedDate() {
		HashMap<String, FileInfo> cachedFiles = new HashMap<String, FileInfo>() {{
			put("lwjgl.jar", new FileInfo("lwjgl.jar", 100, 500L));
		}};
		Cache cache = new Cache(new HashMap<String, FileInfo>());
		assertThat(cache.isAlreadyDownloaded(new FileInfo("lwjgl.jar", 100, 1000L)), IsEqual.equalTo(false));
		assertThat(cache.isAlreadyDownloaded(new FileInfo("lwjgl.jar", 100, 100L)), IsEqual.equalTo(false));
	}

	@Test
	public void shouldPassCacheWhenFoundWithSameDate() {
		HashMap<String, FileInfo> cachedFiles = new HashMap<String, FileInfo>() {{
			put("lwjgl.jar", new FileInfo("lwjgl.jar", 100, 1000L));
		}};
		Cache cache = new Cache(cachedFiles);
		assertThat(cache.isAlreadyDownloaded(new FileInfo("lwjgl.jar", 100, 1000L)), IsEqual.equalTo(true));
	}
	
}
