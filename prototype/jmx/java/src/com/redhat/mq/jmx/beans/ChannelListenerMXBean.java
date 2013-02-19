package com.redhat.mq.jmx.beans;

import javax.management.MXBean;

@MXBean
public interface ChannelListenerMXBean {
    public enum Status {
        Starting,
        Running,
        Stopping
    };
    
    public String getChannelListenerName();

    public String getIpAddress();

    public int getPort();

    public int getProcessId();

    public Status getStatus();
}
