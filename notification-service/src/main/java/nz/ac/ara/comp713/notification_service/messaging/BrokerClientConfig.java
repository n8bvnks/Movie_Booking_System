package nz.ac.ara.comp713.notification_service.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Overrides Spring Boot's auto-configured ActiveMQConnectionFactory so a failed message is
// retried a bounded number of times before ActiveMQ gives up on it, instead of either being
// silently dropped or retried forever. This is what "acknowledgement or retry behaviour" in
// the brief actually looks like on the wire: the listener throws, the message is NOT
// acknowledged, and ActiveMQ redelivers it up to maximumRedeliveries times with a short delay
// between attempts before moving it to its dead-letter queue (ActiveMQ.DLQ by default).
@Configuration
public class BrokerClientConfig {

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory(
            @Value("${spring.activemq.broker-url}") String brokerUrl) {

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);

        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(3);
        redeliveryPolicy.setInitialRedeliveryDelay(1000);
        redeliveryPolicy.setRedeliveryDelay(1000);
        redeliveryPolicy.setUseExponentialBackOff(false);
        factory.setRedeliveryPolicy(redeliveryPolicy);

        return factory;
    }
}
