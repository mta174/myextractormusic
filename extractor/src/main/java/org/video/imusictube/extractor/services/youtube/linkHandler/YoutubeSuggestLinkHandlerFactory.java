package org.video.imusictube.extractor.services.youtube.linkHandler;

import org.video.imusictube.extractor.exception.ParsingException;
import org.video.imusictube.extractor.linkhandler.ListLinkHandlerFactory;
import org.video.imusictube.extractor.utils.ExtractorConstant;
import org.video.imusictube.extractor.utils.LogHelper;
import org.video.imusictube.extractor.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class YoutubeSuggestLinkHandlerFactory extends ListLinkHandlerFactory {

    private static final YoutubeSuggestLinkHandlerFactory instance = new YoutubeSuggestLinkHandlerFactory();

    public static YoutubeSuggestLinkHandlerFactory getInstance() {
        return instance;
    }

    private  String key = "";

    @Override
    public String getUrl(String id, List<String> contentFilter, String sortFilter) throws ParsingException {
        return ExtractorConstant.YOUTUBE_URL + id;
    }

    @Override
    public String getId(String url) throws ParsingException {
        String id = "";
        try {
            URL urlObj = Utils.stringToURL(url);
            key = Utils.getQueryValue(urlObj, "key");
            String path = urlObj.getPath();
            String query = urlObj.getQuery();
            if (!path.startsWith("/youtube/") && !path.startsWith("/v3/")) {
                throw new ParsingException("the URL not match");
            }
            id = path.substring(12) + "?" +  query;
        } catch (MalformedURLException e) {
            LogHelper.i("YoutubeSuggestLink", "MalformedURLException", e.getMessage());
        }
        return id;
    }

    @Override
    public boolean onAcceptUrl(String url) throws ParsingException {
        URL urlObj;
        try {
            urlObj = Utils.stringToURL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        String host = urlObj.getHost();
        return host.equalsIgnoreCase("googleapis.com") || host.equalsIgnoreCase("www.googleapis.com");
    }

    public  String getKey() {
        return key;
    }
}
