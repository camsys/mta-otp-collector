package com.camsys.shims.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

/**
 * Created by lcaraballo on 4/6/18.
 */
public class HtmlCleanupUtil {

    private String[] _htmlTagWhiteList;
    private String[] _htmlAttributesWhiteList;
    private String[] _htmlCharacterBlackList;

    public void setHtmlTagWhiteList(String[] htmlTagWhiteList){
        _htmlTagWhiteList = htmlTagWhiteList;
    }

    public void setHtmlAttributesWhiteList(String[] htmlAttributesWhiteList){
        _htmlAttributesWhiteList = htmlAttributesWhiteList;
    }

    public void setHtmlCharacterBlackList(String [] htmlCharacterBlackList) { _htmlCharacterBlackList = htmlCharacterBlackList; }

    public String filterHtml(final String html){
        Whitelist wl = Whitelist.none();
        wl.addTags(_htmlTagWhiteList);
        addAttributesToAllTags(wl);

        Document doc = Jsoup.parse(html);
        doc = new Cleaner(wl).clean(doc);
        doc.outputSettings().charset("ASCII");
        doc.outputSettings().prettyPrint(false);
        return doc.body().html();
    }

    public String filterAndSpaceHtml(final String html) {
        String spacedHtml = ensureSpacedContent(html);
        return filterHtml(spacedHtml);
    }

    public String filterCharsFromHtml(final String html) {
        if (html == null) return html;
        if (_htmlCharacterBlackList == null) return html;
        String filtered = html;
        for (String filter : _htmlCharacterBlackList) {
            filtered = filtered.replaceAll(filter, "");
        }
        return filtered;
    }

    public String filterAndBlacklist(final String html) {
        String spacedHtml = ensureSpacedContent(html);
        String whiteList = filterHtml(spacedHtml);
        String blackList = filterCharsFromHtml(whiteList);
        return blackList;
    }


    public String ensureSpacedContent(String html) {
        // certain content should be replaced with whitespace so that
        // it can be easily read
        String spacedContent = html.replaceAll("(<br>|<br/>|<br clear=left>)", "$1 ");
        return spacedContent;
    }

    private void addAttributesToAllTags(Whitelist wl){
        for(String tag : _htmlTagWhiteList){
            wl.addAttributes(tag, _htmlAttributesWhiteList);
        }
    }
}
