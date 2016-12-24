package com.jiang.android.zhihu_topanswer.model;

/**
 * Created by jiang on 2016/12/24.
 */

public class AnswersModel {
    private String vote;
    private String content;
    private String author;
    private String url;

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
