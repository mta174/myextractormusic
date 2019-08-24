package com.mta.musictube.extractor.services.media_ccc.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.mta.musictube.extractor.Downloader;
import com.mta.musictube.extractor.ListExtractor;
import com.mta.musictube.extractor.StreamingService;
import com.mta.musictube.extractor.channel.ChannelInfoItem;
import com.mta.musictube.extractor.channel.ChannelInfoItemsCollector;
import com.mta.musictube.extractor.kiosk.KioskExtractor;
import com.mta.musictube.extractor.utils.Localization;

import com.mta.musictube.extractor.exceptions.ExtractionException;
import com.mta.musictube.extractor.exceptions.ParsingException;
import com.mta.musictube.extractor.linkhandler.ListLinkHandler;
import com.mta.musictube.extractor.services.media_ccc.extractors.infoItems.MediaCCCConferenceInfoItemExtractor;

import javax.annotation.Nonnull;
import java.io.IOException;

public class MediaCCCConferenceKiosk extends KioskExtractor<ChannelInfoItem> {

    private JsonObject doc;

    public MediaCCCConferenceKiosk(StreamingService streamingService,
                                   ListLinkHandler linkHandler,
                                   String kioskId,
                                   Localization localization) {
        super(streamingService, linkHandler, kioskId, localization);
    }

    @Nonnull
    @Override
    public ListExtractor.InfoItemsPage<ChannelInfoItem> getInitialPage() throws IOException, ExtractionException {
        JsonArray conferences = doc.getArray("conferences");
        ChannelInfoItemsCollector collector = new ChannelInfoItemsCollector(getServiceId());
        for(int i = 0; i < conferences.size(); i++) {
            collector.commit(new MediaCCCConferenceInfoItemExtractor(conferences.getObject(i)));
        }

        return new ListExtractor.InfoItemsPage<>(collector, "");
    }

    @Override
    public String getNextPageUrl() throws IOException, ExtractionException {
        return "";
    }

    @Override
    public ListExtractor.InfoItemsPage<ChannelInfoItem> getPage(String pageUrl) throws IOException, ExtractionException {
        return ListExtractor.InfoItemsPage.emptyPage();
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        String site = downloader.download(getLinkHandler().getUrl());
        try {
            doc = JsonParser.object().from(site);
        } catch (JsonParserException jpe) {
            throw new ExtractionException("Could not parse json.", jpe);
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return doc.getString("Conferences");
    }
}