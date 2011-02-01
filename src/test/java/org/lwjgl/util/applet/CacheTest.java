package org.lwjgl.util.applet;

import static org.junit.Assert.assertThat;

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
		Cache cache = new Cache();
		assertThat(cache.isAlreadyDownloaded(new FileInfo("lwjgl.jar", 100, 1000L)), IsEqual.equalTo(false));
	}
	
	@Test
	public void shouldFailCacheWhenFoundButDifferentModifiedDate() {
		Cache cache = new Cache();
		cache.add(new FileInfo("lwjgl.jar", 100, 500L));
		assertThat(cache.isAlreadyDownloaded(new FileInfo("lwjgl.jar", 100, 1000L)), IsEqual.equalTo(false));
		assertThat(cache.isAlreadyDownloaded(new FileInfo("lwjgl.jar", 100, 100L)), IsEqual.equalTo(false));
	}

	@Test
	public void shouldPassCacheWhenFoundWithSameDate() {
		Cache cache = new Cache();
		cache.add(new FileInfo("lwjgl.jar", 100, 1000L));
		assertThat(cache.isAlreadyDownloaded(new FileInfo("lwjgl.jar", 100, 1000L)), IsEqual.equalTo(true));
	}
	
}
