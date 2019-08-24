package com.mta.playtube.extractor.services.youtube.extractors;

import com.mta.playtube.extractor.comments.CommentsInfoItemExtractor;
import com.mta.playtube.extractor.utils.JsonUtils;
import com.mta.playtube.extractor.utils.Utils;
import com.mta.playtube.extractor.exceptions.ParsingException;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;

public class YoutubeCommentsInfoItemExtractor implements CommentsInfoItemExtractor {

    private final JsonObject json;
    private final String url;

    public YoutubeCommentsInfoItemExtractor(JsonObject json, String url) {
        this.json = json;
        this.url = url;
    }

    @Override
    public String getUrl() throws ParsingException {
        return url;
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        try {
            JsonArray arr = JsonUtils.getArray(json, "authorThumbnail.thumbnails");
            return JsonUtils.getString(arr.getObject(2), "url");
        } catch (Exception e) {
            throw new ParsingException("Could not get thumbnail url", e);
        }
    }

    @Override
    public String getName() throws ParsingException {
        try {
            return YoutubeCommentsExtractor.getYoutubeText(JsonUtils.getObject(json, "authorText"));
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getPublishedTime() throws ParsingException {
        try {
            return YoutubeCommentsExtractor.getYoutubeText(JsonUtils.getObject(json, "publishedTimeText"));
        } catch (Exception e) {
            throw new ParsingException("Could not get publishedTimeText", e);
        }
    }

    @Override
    public Integer getLikeCount() throws ParsingException {
        try {
            return JsonUtils.getNumber(json, "likeCount").intValue();
        } catch (Exception e) {
            throw new ParsingException("Could not get like count", e);
        }
    }

    @Override
    public String getCommentText() throws ParsingException {
        try {
            String commentText = YoutubeCommentsExtractor.getYoutubeText(JsonUtils.getObject(json, "contentText"));
            // youtube adds U+FEFF in some comments. eg. https://www.youtube.com/watch?v=Nj4F63E59io<feff>
            return Utils.removeUTF8BOM(commentText);
        } catch (Exception e) {
            throw new ParsingException("Could not get comment text", e);
        }
    }

    @Override
    public String getCommentId() throws ParsingException {
        try {
            return JsonUtils.getString(json, "commentId");
        } catch (Exception e) {
            throw new ParsingException("Could not get comment id", e);
        }
    }

    @Override
    public String getAuthorThumbnail() throws ParsingException {
        try {
            JsonArray arr = JsonUtils.getArray(json, "authorThumbnail.thumbnails");
            return JsonUtils.getString(arr.getObject(2), "url");
        } catch (Exception e) {
            throw new ParsingException("Could not get author thumbnail", e);
        }
    }

    @Override
    public String getAuthorName() throws ParsingException {
        try {
            return YoutubeCommentsExtractor.getYoutubeText(JsonUtils.getObject(json, "authorText"));
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String getAuthorEndpoint() throws ParsingException {
        try {
            return "https://youtube.com/channel/" + JsonUtils.getString(json, "authorEndpoint.browseEndpoint.browseId");
        } catch (Exception e) {
            return "";
        }
    }

}