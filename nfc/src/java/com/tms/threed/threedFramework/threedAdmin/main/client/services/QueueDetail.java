package com.tms.threed.threedFramework.threedAdmin.main.client.services;

public class QueueDetail {

    public String queueName;
    public int queued;
    public int running;
    public int canceled;
    public int complete;
    public int dead;

    public QueueDetail(String queueName) {
        this.queueName = queueName;
    }

    @Override public String toString() {
        return "QueueDetail{" +
                "queueName='" + queueName + '\'' +
                ", queued=" + queued +
                ", running=" + running +
                ", canceled=" + canceled +
                ", complete=" + complete +
                ", dead=" + dead +
                '}';
    }
}
