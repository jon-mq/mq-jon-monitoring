package com.redhat.mq.jmx.beans;

import javax.management.MXBean;

@MXBean
public interface QueueManagerMXBean {
    public enum Status {
        Starting,
        Running,
        Quiescing,
    };
    
    public String getQueueManagerName();
    
    public int getConnectionCount();
    
    public String getDeadLetterQueueName();
    
    public Status getStatus();

    public boolean isAvailable();
}
