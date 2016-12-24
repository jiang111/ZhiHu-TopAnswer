package com.jiang.android.zhihu_topanswer.model;

/**
 * Created by jiang on 2016/12/23.
 */

public class TopicModel {
    private int topic;
    private String name;


    public TopicModel(int topic, String name) {
        this.topic = topic;
        this.name = name;
    }

    public int getTopic() {
        return topic;
    }

    public void setTopic(int topic) {
        this.topic = topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TopicModel{" +
                "topic=" + topic +
                ", name='" + name + '\'' +
                '}';
    }
}
