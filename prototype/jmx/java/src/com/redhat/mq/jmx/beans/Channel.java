package com.redhat.mq.jmx.beans;

import java.io.IOException;

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFMessage;

public class Channel implements ChannelMXBean {
    private AgentConnection connection;
    private long bytesReceived;
    private long bytesSent;
    private String channelName;
    private ChannelType channelType;
    private String connectionName;
    private String lastMessageDate;
    private String lastMessageTime;
    private String localAddress;
    private long messagesAvailable;
    private String queueManagerName;
    private Status status;
    private long totalMessagesCount;

    public Channel(String channelName, AgentConnection connection) {
        setChannelName(channelName);
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
        PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL_STATUS);
        request.addParameter(MQConstants.MQCACH_CHANNEL_NAME, channelName);
        request.addParameter(MQConstants.MQIACH_CHANNEL_INSTANCE_ATTRS, new int[] { MQConstants.MQIACF_ALL });

        // TODO All of the following will fail unless the channel is user defined
        PCFMessage response = getConnection().send(request)[0];
        setBytesReceived(response.getIntParameterValue(MQConstants.MQIACH_BYTES_RCVD));
        setBytesSent(response.getIntParameterValue(MQConstants.MQIACH_BYTES_SENT));
        setChannelName(response.getStringParameterValue(MQConstants.MQCACH_CHANNEL_NAME).trim());
        setStatus(response.getIntParameterValue(MQConstants.MQIACH_CHANNEL_STATUS));
        setChannelType(response.getIntParameterValue(MQConstants.MQIACH_CHANNEL_TYPE));
        setConnectionName(response.getStringParameterValue(MQConstants.MQCACH_CONNECTION_NAME).trim());
        setLastMessageDate(response.getStringParameterValue(MQConstants.MQCACH_LAST_MSG_DATE).trim());
        setLastMessageTime(response.getStringParameterValue(MQConstants.MQCACH_LAST_MSG_TIME).trim());
        setLocalAddress(response.getStringParameterValue(MQConstants.MQCACH_LOCAL_ADDRESS).trim());
        setMessagesAvailable(response.getIntParameterValue(MQConstants.MQIACH_XMITQ_MSGS_AVAILABLE));
        setTotalMessagesCount(response.getIntParameterValue(MQConstants.MQIACH_MSGS));
        setQueueManagerName(response.getStringParameterValue(MQConstants.MQCA_Q_MGR_NAME).trim());
    }

    private AgentConnection getConnection() {
        return connection;
    }

    private void setConnection(AgentConnection connection) {
        this.connection = connection;
    }

    @Override
    public long getBytesReceived() {
        refreshInternal();
        return bytesReceived;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    @Override
    public long getBytesSent() {
        refreshInternal();
        return bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    @Override
    public String getChannelName() {
        refreshInternal();
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public ChannelType getChannelType() {
        refreshInternal();
        return channelType;
    }

    public void setChannelType(int channelType) {
        switch (channelType) {
        case MQConstants.MQCHT_SENDER:
            this.channelType = ChannelType.Sender;
            break;
        case MQConstants.MQCHT_SERVER:
            this.channelType = ChannelType.Server;
            break;
        case MQConstants.MQCHT_RECEIVER:
            this.channelType = ChannelType.Receiver;
            break;
        case MQConstants.MQCHT_REQUESTER:
            this.channelType = ChannelType.Requester;
            break;
        case MQConstants.MQCHT_SVRCONN:
            this.channelType = ChannelType.ServerConnection;
            break;
        case MQConstants.MQCHT_CLNTCONN:
            this.channelType = ChannelType.ClientConnection;
            break;
        case MQConstants.MQCHT_CLUSRCVR:
            this.channelType = ChannelType.ClusterReceiver;
            break;
        case MQConstants.MQCHT_CLUSSDR:
            this.channelType = ChannelType.ClusterSender;
            break;
        }
    }

    @Override
    public String getConnectionName() {
        refreshInternal();
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    @Override
    public String getLastMessageDate() {
        refreshInternal();
        return lastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    @Override
    public String getLastMessageTime() {
        refreshInternal();
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    @Override
    public String getLocalAddress() {
        refreshInternal();
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    @Override
    public long getMessagesAvailable() {
        refreshInternal();
        return messagesAvailable;
    }

    public void setMessagesAvailable(long messagesAvailable) {
        this.messagesAvailable = messagesAvailable;
    }

    @Override
    public String getQueueManagerName() {
        refreshInternal();
        return queueManagerName;
    }

    public void setQueueManagerName(String queueManagerName) {
        this.queueManagerName = queueManagerName;
    }

    @Override
    public Status getStatus() {
        refreshInternal();
        return status;
    }

    public void setStatus(int status) {
        switch (status) {
        case MQConstants.MQCHS_BINDING:
            this.status = Status.Binding;
            break;
        case MQConstants.MQCHS_STARTING:
            this.status = Status.Starting;
            break;
        case MQConstants.MQCHS_RUNNING:
            this.status = Status.Running;
            break;
        case MQConstants.MQCHS_PAUSED:
            this.status = Status.Paused;
            break;
        case MQConstants.MQCHS_STOPPED:
            this.status = Status.Stopped;
            break;
        case MQConstants.MQCHS_RETRYING:
            this.status = Status.Retrying;
            break;
        case MQConstants.MQCS_STOPPED:
            this.status = Status.Stopped;
            break;
        case MQConstants.MQCHS_REQUESTING:
            this.status = Status.Requesting;
            break;
        case MQConstants.MQCHS_INITIALIZING:
            this.status = Status.Initializaing;
            break;
        }
    }

    @Override
    public long getTotalMessagesCount() {
        refreshInternal();
        return totalMessagesCount;
    }

    public void setTotalMessagesCount(long totalMessagesCount) {
        this.totalMessagesCount = totalMessagesCount;
    }
}
