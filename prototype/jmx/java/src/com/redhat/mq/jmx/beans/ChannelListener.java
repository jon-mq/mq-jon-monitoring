package com.redhat.mq.jmx.beans;

import java.io.IOException;

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFMessage;

public class ChannelListener implements ChannelListenerMXBean {
    private AgentConnection connection;
    private String channelListenerName;
    private String ipAddress;
    private int port;
    private int processId;
    private Status status;

    public ChannelListener(String channelListenerName, AgentConnection connection) {
        setChannelListenerName(channelListenerName);
        setConnection(connection);
    }

    private void refreshInternal() {
        try {
            refresh();
        } catch (MQDataException | IOException e) {
        }
    }

    // FIXME refresh() is called numerous times due to the fact that accessors are calling it via refreshInternal()
    private void refresh() throws MQDataException, IOException {
        PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_LISTENER_STATUS);
        request.addParameter(MQConstants.MQCACH_LISTENER_NAME, channelListenerName);
        request.addParameter(MQConstants.MQIACF_LISTENER_STATUS_ATTRS, new int [] { MQConstants.MQIACF_ALL });

        // TODO All of the following fails if the channel listener is not running
        PCFMessage response = getConnection().send(request)[0];
        setChannelListenerName(response.getStringParameterValue(MQConstants.MQCACH_LISTENER_NAME).trim());
        setIpAddress(response.getStringParameterValue(MQConstants.MQCACH_IP_ADDRESS).trim());
        setPort(response.getIntParameterValue(MQConstants.MQIACH_PORT));
        setProcessId(response.getIntParameterValue(MQConstants.MQIACF_PROCESS_ID));
        setStatus(response.getIntParameterValue(MQConstants.MQIACH_LISTENER_STATUS));
    }

    private AgentConnection getConnection() {
        return connection;
    }

    private void setConnection(AgentConnection connection) {
        this.connection = connection;
    }

    @Override
    public String getChannelListenerName() {
        refreshInternal();
        return channelListenerName;
    }

    public void setChannelListenerName(String channelListenerName) {
        this.channelListenerName = channelListenerName;
    }

    @Override
    public String getIpAddress() {
        refreshInternal();
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public int getPort() {
        refreshInternal();
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getProcessId() {
        refreshInternal();
    return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    @Override
    public Status getStatus() {
        refreshInternal();
        return status;
    }

    public void setStatus(int status) {
        switch (status) {
        case MQConstants.MQSVC_STATUS_STARTING:
            this.status = Status.Starting;
            break;
        case MQConstants.MQSVC_STATUS_RUNNING:
            this.status = Status.Running;
            break;
        case MQConstants.MQSVC_STATUS_STOPPING:
            this.status = Status.Stopping;
            break;
        }
    }
}
