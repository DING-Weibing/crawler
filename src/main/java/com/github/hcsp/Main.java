package com.github.hcsp;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Main {
    @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
    public static void main(String[] args) throws IOException, SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:C:\\Users\\dingw\\IdeaProjects\\Crawler\\db", "root", "root");

        while (true) {
            // 待处理的连接池
            List<String> linkPool = loadUrlsFromDatabase(connection, "SELECT link FROM LINKS_TO_BE_PROCESSED");

            if (linkPool.isEmpty()) {
                break;
            }

            String link = linkPool.remove(linkPool.size() - 1);
            insertLinkIntoDatabase(connection, link, "DELETE FROM LINKS_TO_BE_PROCESSED WHERE link = ?");

            if (!isLinkProcessed(connection, link)) {
                if (isInterestingLink(link)) {
                    Document doc = Jsoup.connect(link).get();
                    parseUrlsFromPagesAndStoreIntoDatabase(connection, doc);
                    storeIntoDatabaseIfItIsNewsLink(doc);
                    insertLinkIntoDatabase(connection, link, "INSERT INTO LINKS_ALREADY_PROCESSED (link) VALUES (?)");

                }
            }
        }

    }

    private static void parseUrlsFromPagesAndStoreIntoDatabase(Connection connection, Document doc) throws SQLException {
        for (Element aTag : doc.select("a")) {
            String href = aTag.attr("href");
            insertLinkIntoDatabase(connection, href, "INSERT INTO LINKS_TO_BE_PROCESSED (link) VALUES (?)");
        }
    }

    private static boolean isLinkProcessed(Connection connection, String link) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT link FROM LINKS_ALREADY_PROCESSED WHERE link = ?");
             ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            preparedStatement.setString(1, link);
            if (resultSet.next()) {
                return true;
            }
        }
        return false;
    }

    private static void insertLinkIntoDatabase(Connection connection, String link, String sql) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        }
    }

    private static List<String> loadUrlsFromDatabase(Connection connection, String sql) throws SQLException {
        ArrayList<String> result = new ArrayList<>();
        try (PreparedStatement linkToBeProcessed = connection.prepareStatement(sql);
             ResultSet resultSet = linkToBeProcessed.executeQuery()
        ) {
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
        }
        return result;
    }

    private static void storeIntoDatabaseIfItIsNewsLink(Document doc) {
        Elements articles = doc.select("article");
        if (!articles.isEmpty()) {
            for (Element article : articles) {
                String title = articles.get(0).child(0).text();
                System.out.println("title = " + title);
            }
        }
    }

    private static boolean isInterestingLink(String link) {
        return (isNewsPage(link) || isIndexPage(link) && isNotLoginPage(link));
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
