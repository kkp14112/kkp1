package org.schabi.newpipe.extractor.services.niconico.extractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.services.niconico.NiconicoService;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.utils.Parser;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public class NiconicoTrendRSSExtractor implements StreamInfoItemExtractor {
    private final Element item;
    private final Document cdata;

    public NiconicoTrendRSSExtractor(Element e) {
        item = e;
        cdata = Jsoup.parse(e.select("description").text());
    }

    @Override
    public String getName() throws ParsingException {
        return item.select("title").text();
    }

    @Override
    public String getUrl() throws ParsingException {
        // idk why cannot use select("link").text()
        final List<TextNode> textNodes = item.textNodes();
        final Optional<TextNode> url = textNodes.stream().filter(
                str -> Parser.isMatch(NiconicoService.SMILEVIDEO, str.text()))
                .findFirst();
        if (url.isPresent())
        {
            return url.get().text();
        }
        throw new ParsingException("could not get video's url.");
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return cdata.getElementsByClass("nico-thumbnail")
                .select("img")
                .attr("src");
    }

    @Override
    public StreamType getStreamType() throws ParsingException {
        return StreamType.VIDEO_STREAM;
    }

    @Override
    public boolean isAd() throws ParsingException {
        return false;
    }

    @Override
    public long getDuration() throws ParsingException {
        return 0;
    }

    @Override
    public long getViewCount() throws ParsingException {
        final String count = cdata.getElementsByClass("nico-info-total-view").text();
        if (count.isEmpty())
        {
            return 0;
        }
        return Long.parseLong(count.replaceAll(",", ""));
    }

    @Override
    public String getUploaderName() throws ParsingException {
        return "";
    }

    @Override
    public String getUploaderUrl() throws ParsingException {
        return "";
    }

    @Nullable
    @Override
    public String getUploaderAvatarUrl() throws ParsingException {
        return null;
    }

    @Override
    public boolean isUploaderVerified() throws ParsingException {
        return false;
    }

    @Nullable
    @Override
    public String getTextualUploadDate() throws ParsingException {
        return null;
    }

    @Nullable
    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        return null;
    }
}
