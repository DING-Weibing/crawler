package com.github.hcsp;

import java.time.Instant;

public class News {
    private final String title;
    private final String url;
    private final String content;
    private Instant createdAt;
    private Instant ModifiedAt;

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getModifiedAt() {
        return ModifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        ModifiedAt = modifiedAt;
    }
}
