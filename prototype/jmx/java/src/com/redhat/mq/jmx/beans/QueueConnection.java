package com.redhat.mq.jmx.beans;


public class QueueConnection {
    private String applicationTag;
    private String channelName;
    private String connectionName;
    private int processId;

    public QueueConnection() {
        
    }

    public String getApplicationTag() {
        return applicationTag;
    }

    public void setApplicationTag(String applicationTag) {
        this.applicationTag = applicationTag;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }
}
