package org.schabi.newpipe.extractor.services.youtube.extractors;

import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getTextFromObject;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getThumbnailUrlFromInfoItem;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.isYoutubeChannelMixId;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.isYoutubeMusicMixId;
import static org.schabi.newpipe.extractor.utils.Utils.isNullOrEmpty;

import com.grack.nanojson.JsonObject;

import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.playlist.PlaylistInfo;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItemExtractor;
import org.schabi.newpipe.extractor.utils.Utils;

import java.net.MalformedURLException;

import javax.annotation.Nonnull;

public class YoutubeMixPlaylistInfoItemExtractor implements PlaylistInfoItemExtractor {
    private final JsonObject mixInfoItem;

    public YoutubeMixPlaylistInfoItemExtractor(final JsonObject mixInfoItem) {
        this.mixInfoItem = mixInfoItem;
    }

    @Override
    public String getName() throws ParsingException {
        final String name = getTextFromObject(mixInfoItem.getObject("title"));
        if (isNullOrEmpty(name)) {
            throw new ParsingException("Could not get name");
        }
        return name;
    }

    @Override
    public String getUrl() throws ParsingException {
        final String url = mixInfoItem.getString("shareUrl");
        if (isNullOrEmpty(url)) {
            throw new ParsingException("Could not get url");
        }
        return url;
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return getThumbnailUrlFromInfoItem(mixInfoItem);
    }

    @Override
    public String getUploaderName() throws ParsingException {
        // YouTube mixes are auto-generated by YouTube
        return "YouTube";
    }

    @Override
    public long getStreamCount() throws ParsingException {
        // Auto-generated playlists always start with 25 videos and are endless
        return ListExtractor.ITEM_COUNT_INFINITE;
    }

    @Nonnull
    @Override
    public PlaylistInfo.PlaylistType getPlaylistType() throws ParsingException {
        try {
            final String url = getUrl();
            final String mixPlaylistId = Utils.getQueryValue(Utils.stringToURL(url), "list");
            if (isNullOrEmpty(mixPlaylistId)) {
                throw new ParsingException("Mix playlist id was null or empty for url " + url);
            }

            if (isYoutubeMusicMixId(mixPlaylistId)) {
                return PlaylistInfo.PlaylistType.MIX_MUSIC;
            } else if (isYoutubeChannelMixId(mixPlaylistId)) {
                return PlaylistInfo.PlaylistType.MIX_CHANNEL;
            } else {
                // either a normal mix based on a stream, or a "my mix" (still based on a stream)
                return PlaylistInfo.PlaylistType.MIX_STREAM;
            }
        } catch (final MalformedURLException e) {
            throw new ParsingException("Could not obtain mix playlist id", e);
        }
    }
}