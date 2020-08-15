package com.github.hcsp;

public class News {
    private final String title;
    private final String url;
    private final String content;

    public News(String title, String url, String content) {
        this.title = title;
        this.url = url;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}
