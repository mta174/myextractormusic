package org.video.imusictube.extractor.services.youtube.extractors;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import org.video.imusictube.extractor.Downloader;
import org.video.imusictube.extractor.StreamingService;
import org.video.imusictube.extractor.alphabet.AlphabetExtractor;
import org.video.imusictube.extractor.alphabet.AlphabetInfoItem;
import org.video.imusictube.extractor.alphabet.AlphabetInfoItemsCollector;
import org.video.imusictube.extractor.exception.ExtractionException;
import org.video.imusictube.extractor.exception.ParsingException;
import org.video.imusictube.extractor.linkhandler.ListLinkHandler;
import org.video.imusictube.extractor.utils.Localization;
import java.io.IOException;

import javax.annotation.Nonnull;

public class MyAlphabetExtractor extends AlphabetExtractor {
    private static final String TAG = MyAlphabetExtractor.class.getSimpleName();
    private JsonObject jsonObject = null;

    public MyAlphabetExtractor(StreamingService service, ListLinkHandler linkHandler, Localization localization) {
        super(service, linkHandler, localization);
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) throws IOException, ExtractionException {
        try {
            jsonObject = JsonParser.object().from(downloader.downloadCustomize(getUrl()));
        } catch (JsonParserException jpe) {
            throw new ExtractionException("Could not parse json returnd by url: " + getUrl());
        }
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return "";
    }

    @Override
    public String getVersion() throws ParsingException {
        return String.valueOf(jsonObject.getInt("Version"));
    }

    @Nonnull
    @Override
    public InfoItemsPage<AlphabetInfoItem> getInitialPage() throws IOException, ExtractionException {
        AlphabetInfoItemsCollector collector = new AlphabetInfoItemsCollector(getServiceId());
        collector.reset();
        if(jsonObject != null) {
            JsonArray jsonArray = jsonObject.getArray("Data");
            int version = jsonObject.getInt("Version");
            if(jsonArray != null) {
                for (int i = 0, len = jsonArray.size(); i < len; i++) {
                    final JsonObject object = jsonArray.getObject(i);
                    collector.commit(new AlphabetItemExtractor(object, version));
                }
            }
        }
        return new InfoItemsPage<>(collector, getNextPageUrl());
    }

    @Override
    public String getNextPageUrl() throws IOException, ExtractionException {
        return null;
    }

    @Override
    public InfoItemsPage<AlphabetInfoItem> getPage(String pageUrl) throws IOException, ExtractionException {
        return null;
    }
}