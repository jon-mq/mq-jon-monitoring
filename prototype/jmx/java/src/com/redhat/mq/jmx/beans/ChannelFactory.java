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

public class ChannelFactory {
    private static ChannelFactory instance;

    public static ChannelFactory getInstance() {
        synchronized (ChannelFactory.class) {
            if (instance == null) {
                instance = new ChannelFactory();
            }
        }

        return instance;
    }

    /**
     * Discover queues.
     * 
     * Demonstrates: MQCMD_INQUIRE_CHANNEL_NAMES(ChannelName="*")
     */
    public List<ChannelMXBean> discover(AgentConnection connection) throws MQDataException, IOException {
        List<ChannelMXBean> result = new ArrayList<>();

        // Prepare request
        PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL_NAMES);
        request.addParameter(MQConstants.MQCACH_CHANNEL_NAME, "*");

        // Send request - we only care about the first response
        PCFMessage response = connection.send(request)[0];
        String[] channelNames = response.getStringListParameterValue(MQConstants.MQCACH_CHANNEL_NAMES);

        // Register each channel with JMX
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        for (String channelName : channelNames) {
            Channel channel = new Channel(channelName.trim(), connection);
            try {
                ObjectName resourceName = new ObjectName(String.format("com.ibm.mq:type=Channel,name=%s", channelName.trim()));
                mbeanServer.registerMBean(channel, resourceName);
                result.add(channel);
            } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
                    | NotCompliantMBeanException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }

    private ChannelFactory() {
        
    }
}
