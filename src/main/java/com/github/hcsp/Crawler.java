package com.github.hcsp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.*;
import java.util.stream.Collectors;

public class Crawler extends Thread {
    private static final CrawlerDao dao = new MyBatisCrawlerDao();

    public void run() {
        String link;
        // 从数据库拿出下一个待处理链接
        try {
            while ((link = dao.getNextLinkThenDelete()) != null) {
                if (!dao.isLinkProcessed(link)) {
                    if (isInterestingLink(link)) {
                        Document doc = Jsoup.connect(link).get();
                        parseUrlsFromPagesAndStoreIntoDatabase(doc);
                        storeIntoDatabaseIfItIsNewsLink(doc);
                        dao.insertProcessedLink(link);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void parseUrlsFromPagesAndStoreIntoDatabase(Document doc) throws SQLException {
        for (Element aTag : doc.select("a")) {
            String href = aTag.attr("href");
            if (href.startsWith("//")) {
                href = "https" + href;
            }

            if (!href.toLowerCase().startsWith("javascript")) {
                dao.insertToBeProcessedLink(href);
            }
        }
    }

    private void storeIntoDatabaseIfItIsNewsLink(Document doc) throws SQLException {
        Elements articles = doc.select("article");
        if (!articles.isEmpty()) {
            for (Element article : articles) {
                String title = articles.get(0).child(0).text();
                Elements pTags = article.select("p");
                String content = pTags.stream().map(Element::text).collect(Collectors.joining("\n"));
                String url = doc.location();
                News news = new News(title, url, content);
                dao.insertNewsIntoDatabase(news);
            }
        }
    }

    private static boolean isInterestingLink(String link) {
        return (isNewsPage(link) || isIndexPage(link)) && isNotLoginPage(link);
    }

    private static boolean isNewsPage(String link) {
        return link.contains("news.sina.cn");
    }

    private static boolean isIndexPage(String link) {
        return "https://sina.cn".equals(link);
    }

    private static boolean isNotLoginPage(String link) {
        return !link.contains("passport.sina.cn");
    }

}
