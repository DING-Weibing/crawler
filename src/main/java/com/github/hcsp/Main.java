package com.github.hcsp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {

        // 待处理的连接池
        List<String> linkPool = new ArrayList<>();

        // 已处理的连接池
        Set<String> processedLinks = new HashSet<>();
        linkPool.add("https://news.sina.cn");
        while (!linkPool.isEmpty()) {
            String link = linkPool.remove(linkPool.size() - 1);
            if (processedLinks.contains(link)) {
                continue;
            }
            if (isNewsLink(link)) {
                Document doc = Jsoup.connect(link).get();
                doc.select("a").stream().map(aTag -> aTag.attr("href")).forEach(linkPool::add);
                storeIntoDatabaseIfItIsNewsLink(doc);
                processedLinks.add(link);
            }
        }
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

    private static boolean isNewsLink(String link) {
        return link.contains("news.sina.cn");
    }
}
