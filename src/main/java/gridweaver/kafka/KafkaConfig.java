package gridweaver.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic telemetryTopic() {

        return new NewTopic(
                "gridweaver-telemetry",
                1,
                (short) 1
        );
    }
}