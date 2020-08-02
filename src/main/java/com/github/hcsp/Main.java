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

            if (link.contains("news.sina.cn")) {
                //if (link.contains("sina.cn") && !link.contains("passport.sina.cn") && link.contains("news.sina.cn") || "https://sina.cn".equals(link)) {
                Document doc = Jsoup.connect(link).get();
                Elements links = doc.select("a");
                for (Element element : links) {
                    linkPool.add(element.attr("href"));
                }

                Elements articles = doc.select("article");
                if (!articles.isEmpty()) {
                    for (Element article : articles) {
                        String title = articles.get(0).child(0).text();
                        System.out.println("title = " + title);
                    }
                }
                processedLinks.add(link);
            }
        }
    }
}
