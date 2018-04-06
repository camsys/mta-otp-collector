package com.camsys.shims.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Created by lcaraballo on 4/6/18.
 */
public class HtmlCleanupUtil {

    private String[] _htmlTagWhiteList;
    private String[] _htmlAttributesWhiteList;

    public void setHtmlTagWhiteList(String[] htmlTagWhiteList){
        _htmlTagWhiteList = htmlTagWhiteList;
    }

    public void setHtmlAttributesWhiteList(String[] htmlAttributesWhiteList){
        _htmlAttributesWhiteList = htmlAttributesWhiteList;
    }

    public String filterHtml(final String html){
        Whitelist wl = Whitelist.none();
        wl.addTags(_htmlTagWhiteList);
        addAttributesToAllTags(wl);

        final String unescapedHtml = StringEscapeUtils.unescapeHtml4(html);
        final String cleanedHtml = Jsoup.clean(unescapedHtml, wl);
        final String cleanedHtmlWithReplacements = cleanedHtml.replace("\n", "").replace("\u2022", "&bull;"); // bullet point
        return cleanedHtmlWithReplacements;
    }

    private void addAttributesToAllTags(Whitelist wl){
        for(String tag : _htmlTagWhiteList){
            wl.addAttributes(tag, _htmlAttributesWhiteList);
        }
    }
}
