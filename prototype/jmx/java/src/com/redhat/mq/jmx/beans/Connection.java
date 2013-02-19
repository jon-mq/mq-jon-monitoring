package com.redhat.mq.jmx.beans;

import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFMessage;

public class Connection implements ConnectionMXBean {
    private AgentConnection connection;
    private String applicationTag;
    private ApplicationType applicationType;
    private String channelName;
    private String connectionId;
    private String connectionName;
    private String destinationQueueManagerName;
    private String destinationQueueName;
    private int processId;
    private String subscriptionId;
    private String subscriptionName;
    private String topicString;

    public Connection(String connectionId, AgentConnection connection) {
        setConnectionId(connectionId);
        setConnection(connection);
    }

    private void refreshInternal() {
        try {
            refresh();
        } catch (MQDataException | IOException | DecoderException e) {
        }
    }

    // FIXME refresh() is called numerous times due to the fact that accessors are calling it via refreshInternal()
    private void refresh() throws MQDataException, IOException, DecoderException {
        PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_CONNECTION);
        request.addParameter(MQConstants.MQBACF_CONNECTION_ID, Hex.decodeHex(connectionId.toCharArray()));
        request.addParameter(MQConstants.MQIACF_CONN_INFO_TYPE, MQConstants.MQIACF_CONN_INFO_CONN);
        request.addParameter(MQConstants.MQIACF_CONNECTION_ATTRS, new int[] { MQConstants.MQIACF_ALL });

        PCFMessage response = getConnection().send(request)[0];
        setConnectionId(Hex.encodeHexString(response.getBytesParameterValue(MQConstants.MQBACF_CONNECTION_ID)));
        setApplicationTag(response.getStringParameterValue(MQConstants.MQCACF_APPL_TAG).trim());
        setApplicationType(response.getIntParameterValue(MQConstants.MQIA_APPL_TYPE));
        setChannelName(response.getStringParameterValue(MQConstants.MQCACH_CHANNEL_NAME).trim());
        setConnectionName(response.getStringParameterValue(MQConstants.MQCACH_CONNECTION_NAME).trim());
        setProcessId(response.getIntParameterValue(MQConstants.MQIACF_PROCESS_ID));

        request = new PCFMessage(MQConstants.MQCMD_INQUIRE_CONNECTION);
        request.addParameter(MQConstants.MQBACF_CONNECTION_ID, Hex.decodeHex(connectionId.toCharArray()));
        request.addParameter(MQConstants.MQIACF_CONN_INFO_TYPE, MQConstants.MQIACF_CONN_INFO_HANDLE);
        request.addParameter(MQConstants.MQIACF_CONNECTION_ATTRS, new int[] { MQConstants.MQIACF_ALL });

        // Some attributes in the response are actually null so we must type cast the result instead of using PCF to do it
        response = getConnection().send(request)[0];
        setDestinationQueueName((String) response.getParameterValue(MQConstants.MQCACF_DESTINATION));
        setDestinationQueueManagerName((String) response.getParameterValue(MQConstants.MQCACF_DESTINATION_Q_MGR));
        byte[] subId = (byte[]) response.getParameterValue(MQConstants.MQBACF_SUB_ID);
        setSubscriptionId(subId != null ? Hex.encodeHexString(subId) : null);
        setSubscriptionName((String) response.getParameterValue(MQConstants.MQCACF_SUB_NAME));
        setTopicString((String) response.getParameterValue(MQConstants.MQCA_TOPIC_STRING));
    }

    private AgentConnection getConnection() {
        return connection;
    }

    private void setConnection(AgentConnection connection) {
        this.connection = connection;
    }

    @Override
    public String getApplicationTag() {
        refreshInternal();
        return applicationTag;
    }

    public void setApplicationTag(String applicationTag) {
        this.applicationTag = applicationTag;
    }

    @Override
    public ApplicationType getApplicationType() {
        refreshInternal();
        return applicationType;
    }

    public void setApplicationType(int applicationType) {
        switch (applicationType) {
        case MQConstants.MQAT_QMGR:
            this.applicationType = ApplicationType.QueueManagerProcess;
            break;
        case MQConstants.MQAT_CHANNEL_INITIATOR:
            this.applicationType = ApplicationType.ChannelInitiator;
            break;
        case MQConstants.MQAT_USER:
            this.applicationType = ApplicationType.User;
            break;
        case MQConstants.MQAT_BATCH:
            this.applicationType = ApplicationType.Batch;
            break;
        case MQConstants.MQAT_RRS_BATCH:
            this.applicationType = ApplicationType.RRSBatch;
            break;
        case MQConstants.MQAT_CICS:
            this.applicationType = ApplicationType.CICS;
            break;
        case MQConstants.MQAT_IMS:
            this.applicationType = ApplicationType.IMS;
            break;
        }
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
    public String getConnectionId() {
        refreshInternal();
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
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
    public String getDestinationQueueManagerName() {
        refreshInternal();
        return destinationQueueManagerName;
    }

    public void setDestinationQueueManagerName(String destinationQueueManagerName) {
        this.destinationQueueManagerName = destinationQueueManagerName;
    }

    @Override
    public String getDestinationQueueName() {
        refreshInternal();
        return destinationQueueName;
    }

    public void setDestinationQueueName(String destinationQueueName) {
        this.destinationQueueName = destinationQueueName;
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
    public String getSubscriptionId() {
        refreshInternal();
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Override
    public String getSubscriptionName() {
        refreshInternal();
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    @Override
    public String getTopicString() {
        refreshInternal();
        return topicString;
    }

    public void setTopicString(String topicString) {
        this.topicString = topicString;
    }
}
