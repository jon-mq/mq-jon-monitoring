package com.redhat.mq.jmx.beans;

public interface ConnectionMXBean {
    public enum ApplicationType {
        QueueManagerProcess,
        ChannelInitiator,
        User,
        Batch,
        RRSBatch,
        CICS,
        IMS,
    };
    
    public String getApplicationTag();
    
    public ApplicationType getApplicationType();
    
    public String getChannelName();
    
    public String getConnectionId();
    
    public String getConnectionName();
    
    public String getDestinationQueueManagerName();
    
    public String getDestinationQueueName();
    
    public int getProcessId();
    
    public String getSubscriptionId();
    
    public String getSubscriptionName();
    
    public String getTopicString();
}
