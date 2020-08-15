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
    public String getNextLink(String sql) throws SQLException {
        try (PreparedStatement linkToBeProcessed = connection.prepareStatement(sql);
             ResultSet resultSet = linkToBeProcessed.executeQuery()
        ) {
            if (resultSet.next()) {
                String result;
                result = resultSet.getString(1);
                updateDatabase(result, "DELETE FROM LINKS_TO_BE_PROCESSED WHERE link = ?");
                return result;
            }
        }
        return null;
    }

    @Override
    public void updateDatabase(String link, String sql) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void insertNewsIntoDatabase(String url, String title, String content) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO NEWS (TITLE, CONTENT, URL, CREATED_AT, MODIFIED_AT) VALUES ( ?,?, ?, NOW(), NOW())")) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, content);
            preparedStatement.setString(3, url);
            preparedStatement.executeUpdate();
        }
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
}
