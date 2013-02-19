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

import com.ibm.mq.headers.MQDataException;

public class QueueManagerFactory {
    private static QueueManagerFactory instance;

    public static QueueManagerFactory getInstance() {
        synchronized (QueueManagerFactory.class) {
            if (instance == null) {
                instance = new QueueManagerFactory();
            }
        }

        return instance;
    }

    public List<QueueManagerMXBean> discover(AgentConnection connection) throws MQDataException, IOException {
        List<QueueManagerMXBean> result = new ArrayList<>();

        // Register each queue manager with JMX
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        QueueManager queueManager = new QueueManager(connection.getQueueManagerName(), connection);
        try {
            ObjectName resourceName = new ObjectName(String.format("com.ibm.mq:type=QueueManager,name=%s", connection.getQueueManagerName()));
            mbeanServer.registerMBean(queueManager, resourceName);
            result.add(queueManager);
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
                | NotCompliantMBeanException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    private QueueManagerFactory() {

    }
}
