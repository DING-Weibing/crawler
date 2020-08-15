package com.github.hcsp;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.*;

public class JdbcCrawlerDao implements CrawlerDao {
    private final Connection connection;

    @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
    public JdbcCrawlerDao() {
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:C:\\Users\\dingw\\IdeaProjects\\Crawler\\db", "root", "root");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertNewsIntoDatabase(News news) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO NEWS (TITLE, CONTENT, URL, CREATED_AT, MODIFIED_AT) VALUES ( ?,?, ?, NOW(), NOW())")) {
            preparedStatement.setString(1, news.getTitle());
            preparedStatement.setString(2, news.getContent());
            preparedStatement.setString(3, news.getUrl());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void insertProcessedLink(String link) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO LINKS_ALREADY_PROCESSED (link) VALUES (?)")) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void insertToBeProcessedLink(String link) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO LINKS_TO_BE_PROCESSED (link) VALUES (?)")) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public String getNextLinkThenDelete() throws SQLException {
        try (PreparedStatement linkToBeProcessed = connection.prepareStatement("SELECT link FROM LINKS_TO_BE_PROCESSED LIMIT 1");
             ResultSet resultSet = linkToBeProcessed.executeQuery()
        ) {
            if (resultSet.next()) {
                String result;
                result = resultSet.getString(1);
                deleteToBeProcessedLink(result);
                return result;
            }
        }
        return null;
    }

    @Override
    public boolean isLinkProcessed(String link) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT link FROM LINKS_ALREADY_PROCESSED WHERE link = ?")) {
            preparedStatement.setString(1, link);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void deleteProcessedLink(String link) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM LINKS_ALREADY_PROCESSED WHERE link = ?")) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteToBeProcessedLink(String link) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM LINKS_TO_BE_PROCESSED WHERE link = ?")) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        }
    }
}
