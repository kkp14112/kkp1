// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package org.schabi.newpipe.extractor.services.bandcamp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderFactory;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.bandcamp.linkHandler.BandcampPlaylistLinkHandlerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for {@link BandcampPlaylistLinkHandlerFactory}
 */
public class BandcampPlaylistLinkHandlerFactoryTest {

    private static final String RESOURCE_PATH = DownloaderFactory.RESOURCE_PATH + "services/bandcamp/extractor/linkHandler/playlist";
    private static BandcampPlaylistLinkHandlerFactory linkHandler;

    @BeforeAll
    public static void setUp() throws IOException {
        // BandcampPlaylistLinkHandlerFactory needs a Downloader to check if the domain is supported
        NewPipe.init(DownloaderFactory.getDownloader(RESOURCE_PATH));
        linkHandler = BandcampPlaylistLinkHandlerFactory.getInstance();
    }

    @Test
    public void testAcceptUrl() throws ParsingException {
        assertFalse(linkHandler.acceptUrl("http://interovgm.com/releases/"));
        assertFalse(linkHandler.acceptUrl("https://interovgm.com/releases"));
        assertFalse(linkHandler.acceptUrl("http://zachbenson.bandcamp.com"));
        assertFalse(linkHandler.acceptUrl("https://bandcamp.com"));
        assertFalse(linkHandler.acceptUrl("https://zachbenson.bandcamp.com/"));
        assertFalse(linkHandler.acceptUrl("https://zachbenson.bandcamp.com/track/kitchen"));
        assertFalse(linkHandler.acceptUrl("https://interovgm.com/track/title"));
        assertFalse(linkHandler.acceptUrl("https://example.com/album/samplealbum"));

        assertTrue(linkHandler.acceptUrl("https://powertothequeerkids.bandcamp.com/album/power-to-the-queer-kids"));
        assertTrue(linkHandler.acceptUrl("https://zachbenson.bandcamp.com/album/prom"));
        assertTrue(linkHandler.acceptUrl("https://MACBENSON.BANDCAMP.COM/ALBUM/COMING-OF-AGE"));
    }

}
