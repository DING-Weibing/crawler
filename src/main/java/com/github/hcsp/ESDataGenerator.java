package com.github.hcsp;


import org.apache.http.HttpHost;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ESDataGenerator {

    public static void main(String[] args) throws IOException {
        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")))) {
            SqlSessionFactory sqlSessionFactory = CrawlerUtils.getSqlSessionFactory();
            SqlSession sqlSession = sqlSessionFactory.openSession();
            List<News> newsList = sqlSession.selectList("com.github.hcsp.MyMapper.selectOneMillionNews");
            BulkRequest bulkRequest = new BulkRequest();
            for (int i = 0; i < 50_000; i++) {
                for (News news :
                        newsList) {
                    IndexRequest request = new IndexRequest("news");

                    Map<String, Object> data = new HashMap<>();
                    data.put("title", news.getTitle());
                    data.put("url", news.getUrl());
                    data.put("content", news.getContent().length() > 10 ? news.getContent().substring(0, 10) : news.getContent());
                    data.put("createdAt", news.getCreatedAt());
                    data.put("modifiedAt", news.getModifiedAt());

                    request.source(data, XContentType.JSON);
                    bulkRequest.add(request);
                }
                client.bulk(bulkRequest, RequestOptions.DEFAULT);
                System.out.println("2_000");
            }
        }
    }
}
