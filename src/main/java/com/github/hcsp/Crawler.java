package com.github.hcsp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.stream.Collectors;

public class Crawler {
    CrawlerDao dao = new JdbcCrawlerDao();

    public void run() throws SQLException, IOException {
        String link;
        // 从数据库拿出下一个待处理链接
        while ((link = dao.getNextLink("SELECT link FROM LINKS_TO_BE_PROCESSED LIMIT 1")) != null) {
            if (!dao.isLinkProcessed(link)) {
                if (isInterestingLink(link)) {
                    Document doc = Jsoup.connect(link).get();
                    parseUrlsFromPagesAndStoreIntoDatabase(doc);
                    storeIntoDatabaseIfItIsNewsLink(doc);
                    dao.updateDatabase(link, "INSERT INTO LINKS_ALREADY_PROCESSED (link) VALUES (?)");
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        Crawler crawler = new Crawler();
        crawler.run();
    }

    private void parseUrlsFromPagesAndStoreIntoDatabase(Document doc) throws SQLException {
        for (Element aTag : doc.select("a")) {
            String href = aTag.attr("href");
            if (href.startsWith("//")) {
                href = "https" + href;
            }

            if (!href.toLowerCase().startsWith("javascript")) {
                dao.updateDatabase(href, "INSERT INTO LINKS_TO_BE_PROCESSED (link) VALUES (?)");
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
                dao.insertNewsIntoDatabase(url, title, content);

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
