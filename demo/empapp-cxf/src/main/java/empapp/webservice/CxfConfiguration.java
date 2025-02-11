package empapp.webservice;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
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
    public Endpoint employeeWebServiceEndpoint(Bus bus, EmployeeEndpoint employeeEndpoint,
                                               LoggingFeature loggingFeature) {
        EndpointImpl endpoint = new EndpointImpl(bus, employeeEndpoint);
        endpoint.setFeatures(List.of(loggingFeature));
        endpoint.publish("/employees");
        return endpoint;
    }
}
