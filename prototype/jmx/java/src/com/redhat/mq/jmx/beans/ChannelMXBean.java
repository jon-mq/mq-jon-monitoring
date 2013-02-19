package com.redhat.mq.jmx.beans;

import javax.management.MXBean;

@MXBean
public interface ChannelMXBean {
    public enum ChannelType {
        Sender,
        Server,
        Receiver,
        Requester,
        ServerConnection,
        ClientConnection,
        ClusterReceiver,
        ClusterSender,
    };
    
    public enum Status {
        Binding,
        Starting,
        Running,
        Paused,
        Stopping,
        Retrying,
        Stopped,
        Requesting,
        Initializaing,
    };
    
    public long getBytesReceived();
    
    public long getBytesSent();
    
    public String getChannelName();
    
    public ChannelType getChannelType();
    
    public String getConnectionName();
    
    public String getLastMessageDate();
    
    public String getLastMessageTime();
    
    public String getLocalAddress();
    
    public long getMessagesAvailable();
    
    public String getQueueManagerName();
    
    public Status getStatus();
    
    public long getTotalMessagesCount();
}
