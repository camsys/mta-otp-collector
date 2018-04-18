package com.camsys.shims.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.Map;

/**
 * Created by lcaraballo on 4/6/18.
 */
public class HtmlCleanupUtil {

    private Map<String, String> _htmlCharReplacements;
    private String[] _htmlTagWhiteList;
    private String[] _htmlAttributesWhiteList;

    public void setHtmlTagWhiteList(String[] htmlTagWhiteList){
        _htmlTagWhiteList = htmlTagWhiteList;
    }

    public void setHtmlAttributesWhiteList(String[] htmlAttributesWhiteList){
        _htmlAttributesWhiteList = htmlAttributesWhiteList;
    }

    public void setHtmlCharReplacements(Map<String, String> replacementsMap){
        _htmlCharReplacements = replacementsMap;
    }

    public String filterHtml(final String html){
        Whitelist wl = Whitelist.none();
        wl.addTags(_htmlTagWhiteList);
        addAttributesToAllTags(wl);

        final String unescapedHtml = StringEscapeUtils.unescapeHtml4(html);
        final String cleanedHtml = Jsoup.clean(unescapedHtml, wl);
        final String cleanedHtmlWithReplacements =  replaceCharacters(cleanedHtml);
        return cleanedHtmlWithReplacements;
    }

    private String replaceCharacters(final String html){
        return html.replace("\n", "")
                    .replace("\u2022", "&bull;")
                    .replace("\u00B7", "&middot;");
    }

    private void addAttributesToAllTags(Whitelist wl){
        for(String tag : _htmlTagWhiteList){
            wl.addAttributes(tag, _htmlAttributesWhiteList);
        }
    }
}
