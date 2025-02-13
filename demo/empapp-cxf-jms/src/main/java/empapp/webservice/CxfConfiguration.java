package empapp.webservice;

import jakarta.jms.ConnectionFactory;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.jms.JMSConfigFeature;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class CxfConfiguration {

    @Bean
    public LoggingFeature loggingFeature() {
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        return loggingFeature;
    }

    @Bean
    public JMSConfigFeature jmsConfigFeature(ConnectionFactory connectionFactory) {
        JMSConfigFeature jmsConfigFeature = new JMSConfigFeature();

        JMSConfiguration jmsConfiguration = new JMSConfiguration();
        jmsConfiguration.setConnectionFactory(connectionFactory);
        jmsConfiguration.setTargetDestination("employees-ws-request");
        jmsConfiguration.setMessageType("text");

        jmsConfigFeature.setJmsConfig(jmsConfiguration);
        return jmsConfigFeature;
    }

    @Bean
    public Endpoint employeeWebServiceEndpoint(Bus bus, EmployeeEndpoint employeeEndpoint,
                                               LoggingFeature loggingFeature,
                                               JMSConfigFeature jmsConfigFeature) {
        EndpointImpl endpoint = new EndpointImpl(bus, employeeEndpoint);
        endpoint.setFeatures(List.of(loggingFeature, jmsConfigFeature));
        endpoint.publish("jms:queue:employees-ws-request");
        return endpoint;
    }
}
