package com.mta.musictube.extractor;

import java.util.Collections;
import java.util.List;

import com.mta.musictube.extractor.alphabet.AlphabetExtractor;
import com.mta.musictube.extractor.artist.ArtistExtractor;
import com.mta.musictube.extractor.channel.ChannelExtractor;
import com.mta.musictube.extractor.comments.CommentsExtractor;
import com.mta.musictube.extractor.exceptions.ExtractionException;
import com.mta.musictube.extractor.exceptions.ParsingException;
import com.mta.musictube.extractor.genre.GenreExtractor;
import com.mta.musictube.extractor.kiosk.KioskList;
import com.mta.musictube.extractor.linkhandler.LinkHandler;
import com.mta.musictube.extractor.linkhandler.LinkHandlerFactory;
import com.mta.musictube.extractor.linkhandler.ListLinkHandler;
import com.mta.musictube.extractor.linkhandler.ListLinkHandlerFactory;
import com.mta.musictube.extractor.linkhandler.SearchQueryHandler;
import com.mta.musictube.extractor.linkhandler.SearchQueryHandlerFactory;
import com.mta.musictube.extractor.playlist.PlaylistExtractor;
import com.mta.musictube.extractor.listdata.ListDataExtractor;
import com.mta.musictube.extractor.search.SearchExtractor;
import com.mta.musictube.extractor.stream.StreamExtractor;
import com.mta.musictube.extractor.subscription.SubscriptionExtractor;
import com.mta.musictube.extractor.suggest.SuggestExtractor;
import com.mta.musictube.extractor.utils.Localization;

/*
 * Copyright (C) Christian Schabesberger 2018 <chris.schabesberger@mailbox.org>
 * StreamingService.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public abstract class StreamingService {

    /**
     * This class holds meta information about the service implementation.
     */
    public static class ServiceInfo {
        private final String name;
        private final List<MediaCapability> mediaCapabilities;

        /**
         * Creates a new instance of a ServiceInfo
         * @param name the name of the service
         * @param mediaCapabilities the type of media this service can handle
         */
        public ServiceInfo(String name, List<MediaCapability> mediaCapabilities) {
            this.name = name;
            this.mediaCapabilities = Collections.unmodifiableList(mediaCapabilities);
        }

        public String getName() {
            return name;
        }

        public List<MediaCapability> getMediaCapabilities() {
            return mediaCapabilities;
        }

        public enum MediaCapability {
            AUDIO, VIDEO, LIVE, COMMENTS
        }
    }

    /**
     * LinkType will be used to determine which type of URL you are handling, and therefore which part
     * of NewPipe should handle a certain URL.
     */
    public enum LinkType {
        NONE,
        STREAM,
        CHANNEL,
        PLAYLIST,
        ARTIST,
        GENRE,
        LISTDATA,
        ALPHABET,
        SUGGEST,
    }

    private final int serviceId;
    private final ServiceInfo serviceInfo;

    /**
     * Creates a new Streaming service.
     * If you Implement one do not set id within your implementation of this extractor, instead
     * set the id when you put the extractor into
     * <a href="https://teamnewpipe.github.io/NewPipeExtractor/javadoc/org/schabi/newpipe/extractor/ServiceList.html">ServiceList</a>.
     * All other parameters can be set directly from the overriding constructor.
     * @param id the number of the service to identify him within the NewPipe frontend
     * @param name the name of the service
     * @param capabilities the type of media this service can handle
     */
    public StreamingService(int id, String name, List<ServiceInfo.MediaCapability> capabilities) {
        this.serviceId = id;
        this.serviceInfo = new ServiceInfo(name, capabilities);
    }

    public final int getServiceId() {
        return serviceId;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    @Override
    public String toString() {
        return serviceId + ":" + serviceInfo.getName();
    }

    ////////////////////////////////////////////
    // Url Id handler
    ////////////////////////////////////////////

    /**
     * Must return a new instance of an implementation of LinkHandlerFactory for streams.
     * @return an instance of a LinkHandlerFactory for streams
     */
    public abstract LinkHandlerFactory getStreamLHFactory();

    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for channels.
     * If support for channels is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for channels or null
     */
    public abstract ListLinkHandlerFactory getChannelLHFactory();

    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for channels.
     * If support for channels is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for channels or null
     */
    public abstract ListLinkHandlerFactory getGenreLHFactory();

    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for playlists.
     * If support for playlists is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for playlists or null
     */
    public abstract ListLinkHandlerFactory getPlaylistLHFactory();


    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for artists.
     * If support for channels is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for artists or null
     */
    public abstract ListLinkHandlerFactory getArtistLHFactory();

    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for artists.
     * If support for channels is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for artists or null
     */
    public abstract ListLinkHandlerFactory getAlphabetLHFactory();


    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for artists.
     * If support for channels is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for artists or null
     */
    public abstract ListLinkHandlerFactory getSuggestLHFactory();

    /**
     * Must return a new instance of an implementation of ListLinkHandlerFactory for artists.
     * If support for channels is not given null must be returned.
     * @return an instance of a ListLinkHandlerFactory for artists or null
     */
    public abstract ListLinkHandlerFactory getListDataLHFactory();

    /**
     * Must return an instance of an implementation of SearchQueryHandlerFactory.
     * @return an instance of a SearchQueryHandlerFactory
     */
    public abstract SearchQueryHandlerFactory getSearchQHFactory();
    public abstract ListLinkHandlerFactory getCommentsLHFactory();


    ////////////////////////////////////////////
    // Extractor
    ////////////////////////////////////////////

    /**
     * Must create a new instance of a SearchExtractor implementation.
     * @param queryHandler specifies the keyword lock for, and the filters which should be applied.
     * @param localization specifies the language/country for the extractor.
     * @return a new SearchExtractor instance
     */
    public abstract SearchExtractor getSearchExtractor(SearchQueryHandler queryHandler, Localization localization);

    /**
     * Must create a new instance of a SuggestionExtractor implementation.
     * @param localization specifies the language/country for the extractor.
     * @return a new SuggestionExtractor instance
     */
    public abstract SuggestionExtractor getSuggestionExtractor(Localization localization);

    /**
     * Outdated or obsolete. null can be returned.
     * @return just null
     */
    public abstract SubscriptionExtractor getSubscriptionExtractor();

    /**
     * Must create a new instance of a KioskList implementation.
     * @return a new KioskList instance
     * @throws ExtractionException
     */
    public abstract KioskList getKioskList() throws ExtractionException;

    /**
     * Must create a new instance of a ChannelExtractor implementation.
     * @param linkHandler is pointing to the channel which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new ChannelExtractor
     * @throws ExtractionException
     */
    public abstract ChannelExtractor getChannelExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;


    /**
     * Must create a new instance of a GenreExtractor implementation.
     * @param linkHandler is pointing to the channel which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new GenreExtractor
     * @throws ExtractionException
     */
    public abstract GenreExtractor getGenreExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;

    /**
     * Must create a new instance of a ArtistExtractor implementation.
     * @param linkHandler is pointing to the artist which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new MyArtistExtractor
     * @throws ExtractionException
     */
    public abstract ArtistExtractor getArtistExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;


    /**
     * Must create a new instance of a AlphabetExtractor implementation.
     * @param linkHandler is pointing to the artist which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new AlphabetExtractor
     * @throws ExtractionException
     */
    public abstract AlphabetExtractor getAlphabetExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;

    /**
     * Must create a new instance of a MyArtistExtractor implementation.
     * @param linkHandler is pointing to the artist which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new MyArtistExtractor
     * @throws ExtractionException
     */
    public abstract SuggestExtractor getSuggestExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;

    /**
     * Must create a new instance of a ListDataExtractor implementation.
     * @param linkHandler is pointing to the artist which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new ListDataExtractor
     * @throws ExtractionException
     */
    public abstract ListDataExtractor getListDataExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;


    /**
     * Must crete a new instance of a PlaylistExtractor implementation.
     * @param linkHandler is pointing to the playlist which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new PlaylistExtractor
     * @throws ExtractionException
     */
    public abstract PlaylistExtractor getPlaylistExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;

    /**
     * Must create a new instance of a StreamExtractor implementation.
     * @param linkHandler is pointing to the stream which should be handled by this new instance.
     * @param localization specifies the language used for the request.
     * @return a new StreamExtractor
     * @throws ExtractionException
     */
    public abstract StreamExtractor getStreamExtractor(LinkHandler linkHandler, Localization localization) throws ExtractionException;
    public abstract CommentsExtractor getCommentsExtractor(ListLinkHandler linkHandler, Localization localization) throws ExtractionException;
    ////////////////////////////////////////////
    // Extractor with default localization
    ////////////////////////////////////////////

    public SearchExtractor getSearchExtractor(SearchQueryHandler queryHandler) {
        return getSearchExtractor(queryHandler, NewPipe.getPreferredLocalization());
    }

    public SuggestionExtractor getSuggestionExtractor() {
        return getSuggestionExtractor(NewPipe.getPreferredLocalization());
    }

    ////////////////////////////////////////////
    // Extractor without link handler
    ////////////////////////////////////////////

    ////////////////////////////////////////////
    // Short extractor without localization
    ////////////////////////////////////////////

    public ChannelExtractor getChannelExtractor(String url) throws ExtractionException {
        return getChannelExtractor(getChannelLHFactory().fromUrl(url), NewPipe.getPreferredLocalization());
    }

    public GenreExtractor getGenreExtractor(String url) throws ExtractionException {
        return getGenreExtractor(getGenreLHFactory().fromUrl(url), NewPipe.getPreferredLocalization());
    }

    public ArtistExtractor getArtistExtractor(String url) throws ExtractionException {
        return getArtistExtractor(getArtistLHFactory().fromUrl(url), NewPipe.getPreferredLocalization());
    }

    public AlphabetExtractor getAlphabetExtractor(String url) throws ExtractionException {
        return getAlphabetExtractor(getAlphabetLHFactory().fromUrl(url), NewPipe.getPreferredLocalization());
    }

    public SuggestExtractor getSuggestExtractor(String url) throws ExtractionException {
        return getSuggestExtractor(getSuggestLHFactory().fromUrl(url), NewPipe.getPreferredLocalization());
    }

    public ListDataExtractor getListDataExtractor(String url) throws ExtractionException {
        return getListDataExtractor(getListDataLHFactory().fromUrl(url), NewPipe.getPreferredLocalization());
    }

    public PlaylistExtractor getPlaylistExtractor(String url) throws ExtractionException {
        return getPlaylistExtractor(getPlaylistLHFactory().fromUrl(url), NewPipe.getPreferredLocalization());
    }

    public StreamExtractor getStreamExtractor(String url) throws ExtractionException {
        return getStreamExtractor(getStreamLHFactory().fromUrl(url), NewPipe.getPreferredLocalization());
    }
    
    public CommentsExtractor getCommentsExtractor(String url) throws ExtractionException {
        ListLinkHandlerFactory llhf = getCommentsLHFactory();
        if(null == llhf) {
            return null;
        }
        return getCommentsExtractor(llhf.fromUrl(url), NewPipe.getPreferredLocalization());
    }


    /**
     * Figures out where the link is pointing to (a channel, a video, a playlist, etc.)
     * @param url the url on which it should be decided of which link type it is
     * @return the link type of url
     * @throws ParsingException
     */
    public final LinkType getLinkTypeByUrl(String url) throws ParsingException {
        LinkHandlerFactory sH = getStreamLHFactory();
        LinkHandlerFactory cH = getChannelLHFactory();
        LinkHandlerFactory pH = getPlaylistLHFactory();
        LinkHandlerFactory aH = getArtistLHFactory();
        LinkHandlerFactory alpha = getAlphabetLHFactory();
        LinkHandlerFactory aG = getGenreLHFactory();
        LinkHandlerFactory listData = getListDataLHFactory();
        LinkHandlerFactory rH = getSuggestLHFactory();

        if (sH != null && sH.acceptUrl(url)) {
            return LinkType.STREAM;
        } else if (cH != null && cH.acceptUrl(url)) {
            return LinkType.CHANNEL;
        } else if (pH != null && pH.acceptUrl(url)) {
            return LinkType.PLAYLIST;
        } else if (aH != null && aH.acceptUrl(url)) {
            return LinkType.ARTIST;
        } else if (alpha != null && alpha.acceptUrl(url)) {
            return LinkType.ALPHABET;
        } else if (listData != null && listData.acceptUrl(url)) {
            return LinkType.LISTDATA;
        } else if (aG != null && aG.acceptUrl(url)) {
            return LinkType.GENRE;
        } else if (rH != null && rH.acceptUrl(url)) {
            return LinkType.SUGGEST;
        } else {
            return LinkType.NONE;
        }
    }
}