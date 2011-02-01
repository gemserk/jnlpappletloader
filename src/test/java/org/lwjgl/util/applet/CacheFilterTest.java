package org.lwjgl.util.applet;

import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.util.List;

import org.hamcrest.core.IsNot;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.matchers.IsCollectionContaining;
import org.junit.runner.RunWith;

import com.google.inject.internal.Lists;

@RunWith(JMock.class)
public class CacheFilterTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void shouldCheckForCachedFiles() throws MalformedURLException {

		final Cache cache = mockery.mock(Cache.class);

		final FileInfo notCachedFile = new FileInfo("lwjgl.jar", 200, 100L);
		final FileInfo cachedFile = new FileInfo("jinput.jar", 400, 300L);

		mockery.checking(new Expectations() {
			{
				oneOf(cache).isAlreadyDownloaded(notCachedFile);
				will(returnValue(false));
				oneOf(cache).isAlreadyDownloaded(cachedFile);
				will(returnValue(true));
			}
		});

		List<FileInfo> files = Lists.newArrayList(notCachedFile, cachedFile);

		CacheFilter cacheFilter = new CacheFilter(cache);

		List<FileInfo> notCachedFiles = cacheFilter.removeCachedFiles(files);

		assertThat(notCachedFiles, IsCollectionContaining.hasItem(notCachedFile));
		assertThat(notCachedFiles, IsNot.not(IsCollectionContaining.hasItem(cachedFile)));
	}

}
