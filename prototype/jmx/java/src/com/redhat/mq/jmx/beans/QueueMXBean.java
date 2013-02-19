package com.redhat.mq.jmx.beans;

import java.util.List;

import javax.management.MXBean;

@MXBean
public interface QueueMXBean {
    public String getQueueName();
    
    public long getCurrentQueueDepth();
    
    public String getLastGetDate();
    
    public String getLastGetTime();
    
    public String getLastPutDate();
    
    public String getLastPutTime();
    
    public int getOpenInputCount();
    
    public int getOpenOutputCount();
    
    public long getOldestMessageAge();
    
    public long getOnQueueTime();
    
    public long getUncommittedMessagesCount();

    public String getQueueManagerName();
    
    public List<QueueConnection> getConnections();
    
    public boolean isAvailable();
}
