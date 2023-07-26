package com.project.quotes;

public class Model {
    String topic,shayari;

    public Model() {
    }

    public Model(String topic, String shayari) {
        this.topic = topic;
        this.shayari = shayari;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getShayari() {
        return shayari;
    }

    public void setShayari(String shayari) {
        this.shayari = shayari;
    }
}
