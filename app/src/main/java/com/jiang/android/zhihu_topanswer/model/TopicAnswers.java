package com.jiang.android.zhihu_topanswer.model;

/**
 * Created by jiang on 2016/12/23.
 */

public class TopicAnswers {
    private String url;
    private String title;
    private String img;

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
        return "TopicAnswers{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
