package com.it.service;

import com.it.entity.Article;
import com.it.util.CommonUtils;
import com.it.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.Resource;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 从配置的 RSS 地址拉取财经快讯并写入 sys_article（按 source_url 去重）。
 * 不写入封面图：爬取仅同步标题、摘要、链接与时间，图片由后台手动维护或留空。
 */
@Slf4j
@Service
public class FinanceRssSyncService {

    private static final String RSS_TYPE = "RSS聚合";
    private static final int MAX_ITEMS = 40;

    @Resource
    private ArticleService articleService;

    @Value("${finance.rss.url:}")
    private String rssUrl;

    @Value("${finance.rss.userAgent:Mozilla/5.0}")
    private String userAgent;

    public int sync() {
        if (!CommonUtils.stringIsNotBlack(rssUrl)) {
            return 0;
        }
        int imported = 0;
        try {
            FetchedXml fetched = fetchXml(rssUrl.trim());
            if (fetched == null || fetched.raw.length == 0) {
                return 0;
            }
            Document doc = parseXml(fetched.raw, fetched.charset);
            NodeList items = doc.getElementsByTagName("item");
            int n = Math.min(items.getLength(), MAX_ITEMS);
            for (int i = 0; i < n; i++) {
                Element item = (Element) items.item(i);
                String title = textOf(item, "title");
                String link = textOf(item, "link");
                String description = textOf(item, "description");
                String pubDate = textOf(item, "pubDate");
                if (!CommonUtils.stringIsNotBlack(link)) {
                    continue;
                }
                if (articleService.countBySourceUrl(link.trim()) > 0) {
                    continue;
                }
                Article a = new Article();
                a.setName(trimTitle(title));
                a.setType(RSS_TYPE);
                a.setSource_url(link.trim());
                a.setContent(CommonUtils.stringIsNotBlack(description) ? description.trim() : "<p></p>");
                a.setCreate_time(normalizePubDate(pubDate));
                if (articleService.insertFeedArticle(a)) {
                    imported++;
                }
            }
        } catch (Exception e) {
            log.warn("RSS sync failed: {}", e.getMessage(), e);
        }
        return imported;
    }

    private static String textOf(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if (nl.getLength() == 0) {
            return "";
        }
        return nl.item(0).getTextContent() != null ? nl.item(0).getTextContent().trim() : "";
    }

    private static String trimTitle(String title) {
        if (title == null) {
            return "无标题";
        }
        String t = title.trim();
        if (t.length() > 200) {
            return t.substring(0, 197) + "...";
        }
        return t.isEmpty() ? "无标题" : t;
    }

    private String normalizePubDate(String pubDate) {
        if (!CommonUtils.stringIsNotBlack(pubDate)) {
            return DateUtil.getNowDateSS();
        }
        String s = pubDate.trim();
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            SimpleDateFormat in = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
            return out.format(in.parse(s));
        } catch (Exception e1) {
            try {
                SimpleDateFormat in2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                return out.format(in2.parse(s));
            } catch (Exception e2) {
                return DateUtil.getNowDateSS();
            }
        }
    }

    private static final class FetchedXml {
        final byte[] raw;
        final String charset;

        FetchedXml(byte[] raw, String charset) {
            this.raw = raw;
            this.charset = charset;
        }
    }

    private FetchedXml fetchXml(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setRequestProperty("Accept", "application/rss+xml, application/xml, text/xml, */*");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(20000);
        conn.setInstanceFollowRedirects(true);
        int code = conn.getResponseCode();
        String charset = "UTF-8";
        String ct = conn.getContentType();
        if (ct != null) {
            int idx = ct.toLowerCase(Locale.ROOT).indexOf("charset=");
            if (idx >= 0) {
                String part = ct.substring(idx + 8).trim();
                int semi = part.indexOf(';');
                if (semi > 0) {
                    part = part.substring(0, semi).trim();
                }
                if (!part.isEmpty()) {
                    charset = part;
                }
            }
        }
        try {
            if (code >= 400) {
                String errBody = readStreamAsString(conn.getErrorStream(), charset);
                throw new IOException("HTTP " + code + " " + conn.getResponseMessage()
                        + (errBody.isEmpty() ? "" : " — " + errBody.substring(0, Math.min(200, errBody.length()))));
            }
            try (InputStream in = conn.getInputStream()) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                byte[] b = new byte[8192];
                int n;
                while ((n = in.read(b)) > 0) {
                    buf.write(b, 0, n);
                }
                return new FetchedXml(buf.toByteArray(), charset);
            }
        } finally {
            conn.disconnect();
        }
    }

    private static String readStreamAsString(InputStream in, String charsetHint) {
        if (in == null) {
            return "";
        }
        try {
            Charset cs = StandardCharsets.UTF_8;
            try {
                if (CommonUtils.stringIsNotBlack(charsetHint)) {
                    cs = Charset.forName(charsetHint.trim());
                }
            } catch (Exception ignored) {
                // UTF-8
            }
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) > 0) {
                buf.write(b, 0, n);
            }
            return buf.toString(cs.name());
        } catch (Exception e) {
            return "";
        }
    }

    private Document parseXml(byte[] raw, String charset) throws Exception {
        Charset cs = StandardCharsets.UTF_8;
        try {
            if (CommonUtils.stringIsNotBlack(charset)) {
                cs = Charset.forName(charset.trim());
            }
        } catch (Exception e) {
            cs = StandardCharsets.UTF_8;
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setNamespaceAware(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new InputStreamReader(new ByteArrayInputStream(raw), cs));
        return db.parse(is);
    }
}
