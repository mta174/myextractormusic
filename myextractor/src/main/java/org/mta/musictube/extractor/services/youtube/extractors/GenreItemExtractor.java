package org.mta.musictube.extractor.services.youtube.extractors;

import com.grack.nanojson.JsonObject;

import org.mta.musictube.extractor.genre.GenreInfoItemExtractor;
import org.mta.musictube.extractor.exceptions.ParsingException;

public class GenreItemExtractor implements GenreInfoItemExtractor {
    protected static final String TAG = GenreItemExtractor.class.getSimpleName();
    private final JsonObject object;
    private final int version;

    public GenreItemExtractor(JsonObject object, int version) {
        this.object = object;
        this.version = version;
    }

    @Override
    public String getName() throws ParsingException {
        return getGenreName();
    }

    @Override
    public String getUrl() throws ParsingException {
        return null;
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return getThumbnail();
    }

    @Override
    public String getGenreId() throws ParsingException {
        return object.getString("Id");
    }

    @Override
    public String getGenreName() throws ParsingException {
        return object.getString("Name");
    }

    @Override
    public String getThumbnail() throws ParsingException {
        return object.getString("Thumbnail");
    }
}