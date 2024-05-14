package org.schabi.newpipe.extractor.services.bandcamp.stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.schabi.newpipe.extractor.ServiceList.Bandcamp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.downloader.DownloaderFactory;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.PaidContentException;

import java.io.IOException;

public class BandcampPaidStreamExtractorTest {

    private static final String RESOURCE_PATH = DownloaderFactory.RESOURCE_PATH + "services/bandcamp/extractor/stream/paid";

    @BeforeAll
    public static void setUp() throws IOException {
        NewPipe.init(DownloaderFactory.getDownloader(RESOURCE_PATH));
    }

    @Test
    public void testPaidTrack() throws ExtractionException {
        final var extractor = Bandcamp.getStreamExtractor("https://radicaldreamland.bandcamp.com/track/hackmud-continuous-mix");
        assertThrows(PaidContentException.class, extractor::fetchPage);
    }
}
