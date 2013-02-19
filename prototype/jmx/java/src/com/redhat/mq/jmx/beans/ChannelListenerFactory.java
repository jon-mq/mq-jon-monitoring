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

public class ChannelListenerFactory {
    private static ChannelListenerFactory instance;

    public static ChannelListenerFactory getInstance() {
        synchronized (ChannelListenerFactory.class) {
            if (instance == null) {
                instance = new ChannelListenerFactory();
            }
        }

        return instance;
    }

    /**
     * Discover queues.
     * 
     * Demonstrates: MQCMD_INQUIRE_LISTENER(ListenerName="*")
     */
    public List<ChannelListenerMXBean> discover(AgentConnection connection) throws MQDataException, IOException {
        List<ChannelListenerMXBean> result = new ArrayList<>();

        // Prepare request
        PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_LISTENER);
        request.addParameter(MQConstants.MQCACH_LISTENER_NAME, "*");

        // Send request - we only care about the first response
        PCFMessage[] responses = connection.send(request);
        for (PCFMessage response : responses) {
            String listenerName = response.getStringParameterValue(MQConstants.MQCACH_LISTENER_NAME).trim();

            // Register each channel with JMX
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ChannelListener channelListener = new ChannelListener(listenerName.trim(), connection);
            try {
                ObjectName resourceName = new ObjectName(String.format("com.ibm.mq:type=ChannelListener,name=%s", listenerName.trim()));
                mbeanServer.registerMBean(channelListener, resourceName);
                result.add(channelListener);
            } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
                    | NotCompliantMBeanException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }

    private ChannelListenerFactory() {

    }
}
