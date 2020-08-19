package com.github.hcsp;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MockDataGenerator {
    private final SqlSessionFactory sqlSessionFactory;

    public MockDataGenerator(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void generateData(int number) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        List<News> newsList = sqlSession.selectList("com.github.hcsp.MyMapper.selectAllNews");
        for (int i = 0; i < number; i++) {
            News news = newsList.get(ThreadLocalRandom.current().nextInt(newsList.size()));
            changeNewsToRandomCreatedAtAndModifiedAt(news);
            sqlSession.insert("com.github.hcsp.MyMapper.insertNews", news);
        }
        System.out.println("commit");
        sqlSession.commit();
    }

    public static void changeNewsToRandomCreatedAtAndModifiedAt(News news) {
        Instant createdAt = news.getCreatedAt();
        long randomSeconds = ThreadLocalRandom.current().nextLong(60 * 60 * 24 * 365);
        Instant instant = createdAt.minusSeconds(randomSeconds);
        news.setCreatedAt(instant);
        news.setModifiedAt(instant);
    }

    public static void main(String[] args) {
        MockDataGenerator mockDataGenerator = new MockDataGenerator(CrawlerUtils.getSqlSessionFactory());
        mockDataGenerator.generateData(1_000_000);
    }
}
