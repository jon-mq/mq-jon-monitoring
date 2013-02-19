package com.redhat.mq.jmx;

import com.redhat.mq.jmx.beans.AgentConnection;
import com.redhat.mq.jmx.beans.ChannelFactory;
import com.redhat.mq.jmx.beans.ChannelListenerFactory;
import com.redhat.mq.jmx.beans.ConnectionFactory;
import com.redhat.mq.jmx.beans.QueueFactory;
import com.redhat.mq.jmx.beans.QueueManagerFactory;

public class Agent {
    private static String DEFAULT_HOST = "192.168.56.102";
    private static int DEFAULT_PORT = 1415;
    private static String DEFAULT_CHANNEL = "CALCULATION_CHANNEL";

    public static void main(String[] args) throws Throwable {
        // Process arguments
        int argi = 0;
        String host = (argi < args.length ? args[argi++] : DEFAULT_HOST);
        int port = (argi < args.length ? Integer.parseInt(args[argi++]) : DEFAULT_PORT);
        String channel = (argi < args.length ? args[argi++] : DEFAULT_CHANNEL);

        // Connect to MQ and discover queues.
        System.out.println(String.format("Connecting to MQ at %s:%d using channel %s", host, port, channel));
        AgentConnection connection = new AgentConnection(host, port, channel);

        System.out.println("Discovering queue managers");
        QueueManagerFactory.getInstance().discover(connection);
        
        System.out.println("Discovering queues");
        QueueFactory.getInstance().discover(connection);
        
        System.out.println("Discovering active connections");
        ConnectionFactory.getInstance().discover(connection);

        System.out.println("Discovering channels");
        ChannelFactory.getInstance().discover(connection);

        System.out.println("Discovering channel listeners");
        ChannelListenerFactory.getInstance().discover(connection);

        System.out.println("Indefinite sleep, browse results via jconsole");
        Thread.sleep(Long.MAX_VALUE);
    }
}
