package com.redhat.mq.jmx.beans;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFMessage;

public class QueueFactory {
    private static QueueFactory instance;

    public static QueueFactory getInstance() {
        synchronized (QueueFactory.class) {
            if (instance == null) {
                instance = new QueueFactory();
            }
        }

        return instance;
    }

    /**
     * Discover queues.
     * 
     * Demonstrates: MQCMD_INQUIRE_Q_NAMES(QName="*")
     */
    public List<QueueMXBean> discover(AgentConnection connection) throws MQDataException, IOException {
        List<QueueMXBean> result = new ArrayList<>();

        // Prepare request
        PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q_NAMES);
        request.addParameter(MQConstants.MQCA_Q_NAME, "*");
        request.addParameter(MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_LOCAL);

        // Send request - we only care about the first response
        PCFMessage[] responses = connection.send(request);
        String[] queueNames = responses[0].getStringListParameterValue(MQConstants.MQCACF_Q_NAMES);

        // Register each queue with JMX
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        for (String queueName : queueNames) {
            Queue queue = new Queue(queueName.trim(), connection);
            try {
                ObjectName resourceName = new ObjectName(String.format("com.ibm.mq:type=Queue,name=%s", queueName.trim()));
                mbeanServer.registerMBean(queue, resourceName);
                result.add(queue);
            } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
                    | NotCompliantMBeanException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }

    private QueueFactory() {
        
    }
}
