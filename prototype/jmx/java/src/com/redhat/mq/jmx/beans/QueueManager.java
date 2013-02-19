package com.redhat.mq.jmx.beans;

import java.io.IOException;

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFMessage;

public class QueueManager implements QueueManagerMXBean {
    private AgentConnection connection;
    private String queueManagerName;
    private boolean available;
    private int connectionCount;
    private String deadLetterQueueName;
    private Status status;

    public QueueManager(String queueManagerName, AgentConnection connection) {
        setQueueManagerName(queueManagerName);
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
        PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q_MGR_STATUS);
        request.addParameter(MQConstants.MQIACF_Q_MGR_STATUS_ATTRS, new int[] { MQConstants.MQIACF_ALL });

        PCFMessage response = getConnection().send(request)[0];
        setQueueManagerName(response.getStringParameterValue(MQConstants.MQCA_Q_MGR_NAME).trim());
        setConnectionCount(response.getIntParameterValue(MQConstants.MQIACF_CONNECTION_COUNT));
        setStatus(response.getIntParameterValue(MQConstants.MQIACF_Q_MGR_STATUS));

        request = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q_MGR);
        response = getConnection().send(request)[0];
        setDeadLetterQueueName(response.getStringParameterValue(MQConstants.MQCA_DEAD_LETTER_Q_NAME).trim());
    }

    private AgentConnection getConnection() {
        return connection;
    }

    private void setConnection(AgentConnection agent) {
        this.connection = agent;
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
    public boolean isAvailable() {
        try {
            refresh();
        } catch (MQDataException | IOException e) {
            setAvailable(false);
        }
        
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public int getConnectionCount() {
        refreshInternal();
        return connectionCount;
    }

    public void setConnectionCount(int connectionCount) {
        this.connectionCount = connectionCount;
    }

    @Override
    public String getDeadLetterQueueName() {
        refreshInternal();
        return deadLetterQueueName;
    }

    public void setDeadLetterQueueName(String deadLetterQueueName) {
        this.deadLetterQueueName = deadLetterQueueName;
    }

    @Override
    public Status getStatus() {
        refreshInternal();
        return status;
    }

    public void setStatus(int status) {
        switch (status) {
        case MQConstants.MQQMSTA_STARTING:
            this.status = Status.Starting;
            break;
        case MQConstants.MQQMSTA_RUNNING:
            this.status = Status.Running;
            break;
        case MQConstants.MQQMSTA_QUIESCING:
            this.status = Status.Quiescing;
            break;
        }
        setAvailable(this.status == Status.Running);
    }
}
