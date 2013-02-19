package com.redhat.mq.jmx.beans;

import java.io.IOException;

import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFException;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ibm.mq.headers.pcf.PCFMessageAgent;

public class AgentConnection {
    private String host;
    private int port;
    private String channel;

    private PCFMessageAgent agent;

    public AgentConnection(String host, int port, String channel) throws MQDataException, IOException {
        this.host = host;
        this.port = port;
        this.channel = channel;
        connect();
    }
    
    public void connect() throws MQDataException {
        agent = new PCFMessageAgent(host, port, channel);
    }
    
    public void disconnect() throws MQDataException {
        getAgent().disconnect();
    }
    
    public PCFMessage[] send(PCFMessage request) throws PCFException, MQDataException, IOException {
        return getAgent().send(request);
    }
    
    public String getQueueManagerName() {
        return getAgent().getQManagerName();
    }
    
    protected PCFMessageAgent getAgent() {
        return agent;
    }
}
