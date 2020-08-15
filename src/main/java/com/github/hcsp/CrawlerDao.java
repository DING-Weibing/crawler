package com.github.hcsp;

import java.sql.SQLException;

public interface CrawlerDao {
    String getNextLinkThenDelete() throws SQLException;

    boolean isLinkProcessed(String link) throws SQLException;

    void deleteProcessedLink(String link) throws SQLException;

    void deleteToBeProcessedLink(String link) throws SQLException;

    void insertNewsIntoDatabase(News news) throws SQLException;

    void insertProcessedLink(String link) throws SQLException;

    void insertToBeProcessedLink(String link) throws SQLException;
}
