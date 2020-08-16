package com.github.hcsp;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MyBatisCrawlerDao implements CrawlerDao {
    private final SqlSessionFactory sqlSessionFactory;

    public MyBatisCrawlerDao() {
        String resource = "db/mybatis/mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    @Override
    public synchronized String getNextLinkThenDelete() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            String url = session.selectOne("com.github.hcsp.MyMapper.selectNextAvailableLink");
            if (url != null) {
                deleteToBeProcessedLink(url);
                return url;
            }
        }
        return null;
    }

    @Override
    public boolean isLinkProcessed(String link) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("tableName", "LINKS_ALREADY_PROCESSED");
            hashMap.put("link", link);
            int count = session.selectOne("com.github.hcsp.MyMapper.countLink", hashMap);
            return count > 0;
        }
    }

    @Override
    public void deleteProcessedLink(String link) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("tableName", "LINKS_ALREADY_PROCESSED");
            hashMap.put("link", link);
            session.delete("com.github.hcsp.MyMapper.deleteLink", hashMap);
        }
    }

    @Override
    public void deleteToBeProcessedLink(String link) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("tableName", "LINKS_TO_BE_PROCESSED");
            hashMap.put("link", link);
            session.delete("com.github.hcsp.MyMapper.deleteLink", hashMap);
        }
    }

    @Override
    public void insertNewsIntoDatabase(News news) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.github.hcsp.MyMapper.insertNews", news);
        }
    }

    @Override
    public void insertProcessedLink(String link) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("tableName", "LINKS_ALREADY_PROCESSED");
            hashMap.put("link", link);
            session.insert("com.github.hcsp.MyMapper.insertLink", hashMap);
        }
    }

    @Override
    public void insertToBeProcessedLink(String link) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("tableName", "LINKS_TO_BE_PROCESSED");
            hashMap.put("link", link);
            session.insert("com.github.hcsp.MyMapper.insertLink", hashMap);
        }
    }
}
