package org.schabi.newpipe.extractor.services.youtube;

import org.junit.BeforeClass;
import org.junit.Test;
import org.schabi.newpipe.DownloaderTestImpl;
import org.schabi.newpipe.extractor.ListExtractor.InfoItemsPage;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.comments.CommentsInfo;
import org.schabi.newpipe.extractor.comments.CommentsInfoItem;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.services.DefaultTests;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeCommentsExtractor;
import org.schabi.newpipe.extractor.utils.Utils;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.schabi.newpipe.extractor.ServiceList.YOUTUBE;

public class YoutubeCommentsExtractorTest {
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=D00Au7k3i6o";
    private static final String INVIDIOUS_URL = "https://invidio.us/watch?v=D00Au7k3i6o";
    private static YoutubeCommentsExtractor youtubeExtractor;
    private static YoutubeCommentsExtractor invidiousExtractor;

    @BeforeClass
    public static void setUp() throws Exception {
        NewPipe.init(DownloaderTestImpl.getInstance());
        youtubeExtractor = (YoutubeCommentsExtractor) YOUTUBE
                .getCommentsExtractor(YOUTUBE_URL);
        youtubeExtractor.fetchPage();
        invidiousExtractor = (YoutubeCommentsExtractor) YOUTUBE
                .getCommentsExtractor(INVIDIOUS_URL);
    }

    @Test
    public void testGetComments() throws IOException, ExtractionException {
        assertTrue(getCommentsHelper(youtubeExtractor));
        assertTrue(getCommentsHelper(invidiousExtractor));
    }

    private boolean getCommentsHelper(final YoutubeCommentsExtractor extractor)
            throws IOException, ExtractionException {
        InfoItemsPage<CommentsInfoItem> comments = extractor.getInitialPage();
        boolean result = findInComments(comments, "s1ck m3m3");

        while (comments.hasNextPage() && !result) {
            comments = extractor.getPage(comments.getNextPage());
            result = findInComments(comments, "s1ck m3m3");
        }

        return result;
    }

    @Test
    public void testGetCommentsFromCommentsInfo() throws IOException, ExtractionException {
        assertTrue(getCommentsFromCommentsInfoHelper(YOUTUBE_URL));
        assertTrue(getCommentsFromCommentsInfoHelper(INVIDIOUS_URL));
    }

    private boolean getCommentsFromCommentsInfoHelper(final String url)
            throws IOException, ExtractionException {
        final CommentsInfo commentsInfo = CommentsInfo.getInfo(url);

        assertEquals("Comments", commentsInfo.getName());
        boolean result = findInComments(commentsInfo.getRelatedItems(), "s1ck m3m3");

        Page nextPage = commentsInfo.getNextPage();
        InfoItemsPage<CommentsInfoItem> moreItems = new InfoItemsPage<>(null, nextPage, null);
        while (moreItems.hasNextPage() && !result) {
            moreItems = CommentsInfo.getMoreItems(YOUTUBE, commentsInfo, nextPage);
            result = findInComments(moreItems.getItems(), "s1ck m3m3");
            nextPage = moreItems.getNextPage();
        }
        return result;
    }

    @Test
    public void testGetCommentsAllData() throws IOException, ExtractionException {
        final InfoItemsPage<CommentsInfoItem> comments = youtubeExtractor.getInitialPage();

        DefaultTests.defaultTestListOfItems(YOUTUBE, comments.getItems(), comments.getErrors());
        for (final CommentsInfoItem c : comments.getItems()) {
            assertFalse(Utils.isBlank(c.getUploaderUrl()));
            assertFalse(Utils.isBlank(c.getUploaderName()));
            assertFalse(Utils.isBlank(c.getUploaderAvatarUrl()));
            assertFalse(Utils.isBlank(c.getCommentId()));
            assertFalse(Utils.isBlank(c.getCommentText()));
            assertFalse(Utils.isBlank(c.getName()));
            assertFalse(Utils.isBlank(c.getTextualUploadDate()));
            assertNotNull(c.getUploadDate());
            assertFalse(Utils.isBlank(c.getThumbnailUrl()));
            assertFalse(Utils.isBlank(c.getUrl()));
            assertFalse(c.getLikeCount() < 0);
        }
    }

    private boolean findInComments(final InfoItemsPage<CommentsInfoItem> comments,
                                   final String comment) {
        return findInComments(comments.getItems(), comment);
    }

    private boolean findInComments(final List<CommentsInfoItem> comments, final String comment) {
        for (final CommentsInfoItem c : comments) {
            if (c.getCommentText().contains(comment)) {
                return true;
            }
        }
        return false;
    }
}
