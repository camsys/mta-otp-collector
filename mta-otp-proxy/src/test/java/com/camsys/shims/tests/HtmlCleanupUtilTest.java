package com.camsys.shims.tests;

import com.camsys.shims.util.HtmlCleanupUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlCleanupUtilTest {

    private HtmlCleanupUtil htmlCleanupUtil;

    @Before
    public void setup(){
        String htmlTagWhiteList[] = new String[]{"p","br", "ol", "ul","b","a","span", "div"};
        String htmlAttributesWhiteList[] = new String[]{"class"};

        htmlCleanupUtil = new HtmlCleanupUtil();
        htmlCleanupUtil.setHtmlTagWhiteList(htmlTagWhiteList);
        htmlCleanupUtil.setHtmlAttributesWhiteList(htmlAttributesWhiteList);
    }

    @Test
    public void testHtmlTagWhiteList(){
        // Allow <a>, <b>, and <br> tags, strip everything else
        String htmlTagWhiteList[] = new String[]{"a", "b", "br"};
        htmlCleanupUtil.setHtmlTagWhiteList(htmlTagWhiteList);

        String html = "<a class=\"plannedWorkDetailLink\" onclick=ShowHide(184199);><b><i>STRUCTURAL IMPROVEMENTS</i>" +
                "<br clear=left>[F] No trains between Church Av and Stillwell Av</a><br/><br/>" +
                "<div id= 184199 class=\"plannedWorkDetail\" >[SB] Free shuttle buses provide alternate service<br></b>" +
                "Weekends, 9:45 PM Fri to 5 AM Mon, until May 14<br><br></font>[F] service operates between <b>179 St</b> " +
                "and <b>Church Av</b>.<br><br>[SB] Buses operate between <b>Church Av</b> and <b>Stillwell Av*</b>";

        String actual = htmlCleanupUtil.filterHtml(html);

        String expected = "<a class=\"plannedWorkDetailLink\"><b>STRUCTURAL IMPROVEMENTS<br>[F] No trains between Church " +
                "Av and Stillwell Av</b></a><b><br><br></b><b>[SB] Free shuttle buses provide alternate service<br></b>" +
                "Weekends, 9:45 PM Fri to 5 AM Mon, until May 14<br><br>[F] service operates between <b>179 St</b> and " +
                "<b>Church Av</b>.<br><br>[SB] Buses operate between <b>Church Av</b> and <b>Stillwell Av*</b>";

        assertEquals(expected, actual);
    }

    @Test
    public void testHtmlAttributesWhiteList(){
        // Allow onclick, class, and id attributes, strip everything else
        String htmlAttributesWhiteList[] = new String[]{"onclick", "class", "id"};
        htmlCleanupUtil.setHtmlAttributesWhiteList(htmlAttributesWhiteList);

        String html = "<a class=\"plannedWorkDetailLink\" onclick=ShowHide(184199);><b><i>STRUCTURAL IMPROVEMENTS</i>" +
                "<br clear=left>[F] No trains between Church Av and Stillwell Av</a><br/><br/>" +
                "<div id= 184199 class=\"plannedWorkDetail\" >[SB] Free shuttle buses provide alternate service<br></b>" +
                "Weekends, 9:45 PM Fri to 5 AM Mon, until May 14<br><br></font>[F] service operates between <b>179 St</b> " +
                "and <b>Church Av</b>.<br><br>[SB] Buses operate between <b>Church Av</b> and <b>Stillwell Av*</b>";

        String actual = htmlCleanupUtil.filterHtml(html);

        String expected = "<a class=\"plannedWorkDetailLink\" onclick=\"ShowHide(184199);\"><b>STRUCTURAL IMPROVEMENTS" +
                "<br>[F] No trains between Church Av and Stillwell Av</b></a><b><br><br></b><div id=\"184199\" " +
                "class=\"plannedWorkDetail\"><b>[SB] Free shuttle buses provide alternate service<br></b>Weekends, " +
                "9:45 PM Fri to 5 AM Mon, until May 14<br><br>[F] service operates between <b>179 St</b> and " +
                "<b>Church Av</b>.<br><br>[SB] Buses operate between <b>Church Av</b> and <b>Stillwell Av*</b></div>";

        assertEquals(expected, actual);
    }

    @Test
    public void testBullet(){
        String html = "<span class=\"TitlePlannedWork\">Planned Work &bull;</span>";

        String actual = htmlCleanupUtil.filterHtml(html);
        String expected = "<span class=\"TitlePlannedWork\">Planned Work &#x2022;</span>";
        assertEquals(expected, actual);

        actual = StringEscapeUtils.unescapeHtml4(actual);
        expected = "<span class=\"TitlePlannedWork\">Planned Work •</span>";
        assertEquals(expected, actual);
    }

    @Test
    public void testMidDot(){
        String html = "<span class=\"TitlePlannedWork\">Planned Work &middot;</span>";

        String actual = htmlCleanupUtil.filterHtml(html);
        String expected = "<span class=\"TitlePlannedWork\">Planned Work &middot;</span>";
        assertEquals(expected, actual);

        actual = StringEscapeUtils.unescapeHtml4(actual);
        expected = "<span class=\"TitlePlannedWork\">Planned Work ·</span>";
        assertEquals(expected, actual);
    }

    @Test
    public void testMidDash(){
        String html = "<span class=\"TitlePlannedWork\">Planned Work &mdash;</span>";

        String actual = htmlCleanupUtil.filterHtml(html);
        String expected = "<span class=\"TitlePlannedWork\">Planned Work &#x2014;</span>";
        assertEquals(expected, actual);

        actual = StringEscapeUtils.unescapeHtml4(actual);
        expected = "<span class=\"TitlePlannedWork\">Planned Work —</span>";
        assertEquals(expected, actual);
    }

    @Test
    public void testEnsureSpacedContent() {
        String html = "<a class=\"plannedWorkDetailLink\" onclick=ShowHide(261842);><b>Until further notice<br clear=left>Eastbound B25 stop on Water St at Main St is closed due to road work</a><br/><br/><div id= 261842 class=\"plannedWorkDetail\" ><br></b>Please use the nearby stop <b>on</b> <b>Front St </b>at York St.<br><b><br></div></b><br/>";
        String actual = htmlCleanupUtil.ensureSpacedContent(html);
        String expected = "<a class=\"plannedWorkDetailLink\" onclick=ShowHide(261842);><b>Until further notice<br clear=left> Eastbound B25 stop on Water St at Main St is closed due to road work</a><br/> <br/> <div id= 261842 class=\"plannedWorkDetail\" ><br> </b>Please use the nearby stop <b>on</b> <b>Front St </b>at York St.<br> <b><br> </div></b><br/> ";
        assertEquals(expected, actual);

    }
}
