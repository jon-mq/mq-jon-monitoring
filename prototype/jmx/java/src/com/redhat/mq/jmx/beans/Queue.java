package com.redhat.mq.jmx.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFMessage;

public class Queue implements QueueMXBean {
    private AgentConnection connection;
    private long currentqueueDepth;
    private String lastGetDate;
    private String lastGetTime;
    private String lastPutDate;
    private String lastPutTime;
    private String queueName;
    private int openInputCount;
    private int openOutputCount;
    private long oldestMessageAge;
    private long onQueueTime;
    private long uncommittedMessagesCount;
    private String queueManagerName;
    private List<QueueConnection> connections = new ArrayList<>();
    private boolean available;

    public Queue(String queueName, AgentConnection connection) {
        setQueueName(queueName);
        setConnection(connection);
    }

    private void refreshInternal() {
        try {
            refresh();
            setAvailable(true);
        } catch (MQDataException | IOException e) {
            setAvailable(false);
        }
    }

    // FIXME refresh() is called numerous times due to the fact that accessors are calling it via refreshInternal()
    private void refresh() throws MQDataException, IOException {
        PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q_STATUS);
        request.addParameter(MQConstants.MQCA_Q_NAME, queueName);
        request.addParameter(MQConstants.MQIACF_Q_STATUS_TYPE, MQConstants.MQIACF_Q_STATUS);
        request.addParameter(MQConstants.MQIACF_Q_STATUS_ATTRS, new int[] { MQConstants.MQIACF_ALL });

        PCFMessage response = getConnection().send(request)[0];
        setQueueName(response.getStringParameterValue(MQConstants.MQCA_Q_NAME).trim());
        setCurrentQueueDepth(response.getIntParameterValue(MQConstants.MQIA_CURRENT_Q_DEPTH));
        setLastGetDate(response.getStringParameterValue(MQConstants.MQCACF_LAST_GET_DATE).trim());
        setLastGetTime(response.getStringParameterValue(MQConstants.MQCACF_LAST_GET_TIME).trim());
        setLastPutDate(response.getStringParameterValue(MQConstants.MQCACF_LAST_PUT_DATE).trim());
        setLastPutTime(response.getStringParameterValue(MQConstants.MQCACF_LAST_PUT_TIME).trim());
        setOpenInputCount(response.getIntParameterValue(MQConstants.MQIA_OPEN_INPUT_COUNT));
        setOpenOutputCount(response.getIntParameterValue(MQConstants.MQIA_OPEN_OUTPUT_COUNT));
        setOldestMessageAge(response.getIntParameterValue(MQConstants.MQIACF_OLDEST_MSG_AGE));
        setOnQueueTime(response.getIntListParameterValue(MQConstants.MQIACF_Q_TIME_INDICATOR)[1]);
        setUncommittedMessages(response.getIntParameterValue(MQConstants.MQIACF_UNCOMMITTED_MSGS));
        setQueueManagerName(connection.getQueueManagerName());

        // To obtain status on the queue handle the queue must be opened by something, otherwise the inquiry fails
        if (openInputCount > 0 || openOutputCount > 0) {
            connections.clear();
            request = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q_STATUS);
            request.addParameter(MQConstants.MQCA_Q_NAME, queueName);
            request.addParameter(MQConstants.MQIACF_Q_STATUS_TYPE, MQConstants.MQIACF_Q_HANDLE);
            request.addParameter(MQConstants.MQIACF_Q_STATUS_ATTRS, new int[] { MQConstants.MQIACF_ALL });
            
            PCFMessage[] responses = getConnection().send(request);
            for (PCFMessage resp : responses) {
                QueueConnection connection = new QueueConnection();
                connection.setApplicationTag(resp.getStringParameterValue(MQConstants.MQCACF_APPL_TAG).trim());
                connection.setChannelName(resp.getStringParameterValue(MQConstants.MQCACH_CHANNEL_NAME).trim());
                connection.setConnectionName(resp.getStringParameterValue(MQConstants.MQCACH_CONNECTION_NAME).trim());
                connection.setProcessId(resp.getIntParameterValue(MQConstants.MQIACF_PROCESS_ID));
                connections.add(connection);
            }
        }
    }

    private AgentConnection getConnection() {
        return connection;
    }

    private void setConnection(AgentConnection connection) {
        this.connection = connection;
    }

    @Override
    public String getQueueName() {
        refreshInternal();
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    @Override
    public long getCurrentQueueDepth() {
        refreshInternal();
        return currentqueueDepth;
    }

    public void setCurrentQueueDepth(long currentQueueDepth) {
        this.currentqueueDepth = currentQueueDepth;
    }

    @Override
    public String getLastGetDate() {
        refreshInternal();
        return lastGetDate;
    }

    public void setLastGetDate(String lastGetDate) {
        this.lastGetDate = lastGetDate;
    }

    @Override
    public String getLastGetTime() {
        refreshInternal();
        return lastGetTime;
    }

    public void setLastGetTime(String lastGetTime) {
        this.lastGetTime = lastGetTime;
    }

    @Override
    public String getLastPutDate() {
        refreshInternal();
        return lastPutDate;
    }

    public void setLastPutDate(String lastPutDate) {
        this.lastPutDate = lastPutDate;
    }

    @Override
    public String getLastPutTime() {
        refreshInternal();
        return lastPutTime;
    }

    public void setLastPutTime(String lastPutTime) {
        this.lastPutTime = lastPutTime;
    }

    @Override
    public int getOpenInputCount() {
        refreshInternal();
        return openInputCount;
    }

    public void setOpenInputCount(int openInputCount) {
        this.openInputCount = openInputCount;
    }

    @Override
    public int getOpenOutputCount() {
        refreshInternal();
        return openOutputCount;
    }

    public void setOpenOutputCount(int openOutputCount) {
        this.openOutputCount = openOutputCount;
    }

    @Override
    public long getOldestMessageAge() {
        refreshInternal();
        return oldestMessageAge;
    }

    public void setOldestMessageAge(long oldestMessageAge) {
        this.oldestMessageAge = oldestMessageAge;
    }

    @Override
    public long getOnQueueTime() {
        refreshInternal();
        return onQueueTime;
    }

    public void setOnQueueTime(long onQueueTime) {
        this.onQueueTime = onQueueTime;
    }

    @Override
    public long getUncommittedMessagesCount() {
        refreshInternal();
        return uncommittedMessagesCount;
    }

    public void setUncommittedMessages(long uncommittedMessagesCount) {
        this.uncommittedMessagesCount = uncommittedMessagesCount;
    }

    @Override
    public String getQueueManagerName() {
        return queueManagerName;
    }

    public void setQueueManagerName(String queueManagerName) {
        this.queueManagerName = queueManagerName;
    }

    @Override
    public List<QueueConnection> getConnections() {
        refreshInternal();
        return connections;
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
}
