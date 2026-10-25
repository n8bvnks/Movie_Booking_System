package nz.ac.ara.comp713.booking_service.messaging;

import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Hosts the ActiveMQ broker inside this process (Advanced Path B: message broker).
// Spring Boot's default "in-memory" ActiveMQ mode only exposes a vm:// transport, which is
// reachable from this same JVM only. notification-service runs as its own separate process,
// so this config instead starts the broker with a real TCP connector that a remote JVM can
// dial in to, same as talking to any standalone broker.
//
// persistent=true + a local data directory means a message that is queued while
// notification-service happens to be offline is still delivered once it reconnects -
// the broker does not forget it just because nothing was listening at that moment.
@Configuration
public class EmbeddedBrokerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public BrokerService brokerService(@Value("${activemq.embedded.tcp-port:61616}") int tcpPort) throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("movie-booking-broker");
        broker.setPersistent(true);
        broker.setDataDirectory("activemq-data");
        broker.setUseJmx(false);
        broker.addConnector("tcp://0.0.0.0:" + tcpPort);
        return broker;
    }
}
