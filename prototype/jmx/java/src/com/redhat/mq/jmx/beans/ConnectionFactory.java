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

import org.apache.commons.codec.binary.Hex;

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFMessage;

public class ConnectionFactory {
    private static ConnectionFactory instance;

    public static ConnectionFactory getInstance() {
        synchronized (ConnectionFactory.class) {
            if (instance == null) {
                instance = new ConnectionFactory();
            }
        }

        return instance;
    }

    /**
     * Discover queues.
     * 
     * Demonstrates: MQCMD_INQUIRE_CONNECTION(GenericConnectionId="*")
     */
    public List<ConnectionMXBean> discover(AgentConnection connection) throws MQDataException, IOException {
        List<ConnectionMXBean> result = new ArrayList<>();

        // Prepare request
        PCFMessage request = new PCFMessage(MQConstants.MQCMD_INQUIRE_CONNECTION);
        request.addParameter(MQConstants.MQBACF_GENERIC_CONNECTION_ID, new byte[0]);
        request.addParameter(MQConstants.MQIACF_CONN_INFO_TYPE, MQConstants.MQIACF_CONN_INFO_CONN);
        request.addParameter(MQConstants.MQIACF_CONNECTION_ATTRS, new int[] { MQConstants.MQBACF_CONNECTION_ID });

        // Send request
        PCFMessage[] responses = connection.send(request);

        // Register each connection with JMX
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        for (PCFMessage response : responses) {
            String id = Hex.encodeHexString(response.getBytesParameterValue(MQConstants.MQBACF_CONNECTION_ID));
            Connection conn = new Connection(id, connection);
            try {
                ObjectName resourceName = new ObjectName(String.format("com.ibm.mq:type=Connection,id=%s", id));
                mbeanServer.registerMBean(conn, resourceName);
                result.add(conn);
            } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
                    | NotCompliantMBeanException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }

    private ConnectionFactory() {

    }
}
