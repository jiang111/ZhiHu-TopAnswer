package com.jiang.android.zhihu_topanswer.model;

/**
 * Created by jiang on 2016/12/23.
 */

public class TopicAnswers {
    private String url;
    private String title;
    private String img;
    private String vote;
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "url:" + url + " :title:" + title + " :img:" + img + " :vote:" + vote + " :body:" + body;
    }
}
