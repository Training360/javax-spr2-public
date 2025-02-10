package empapp.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pub-sub.jms")
@Data
public class PubSubJmsProperties {

    private String clientId;
}
