package gridweaver.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TelemetryKafkaProducer {

    private static final String TOPIC = "gridweaver-telemetry";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TelemetryKafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTelemetry(String telemetry) {

        kafkaTemplate.send(TOPIC, telemetry);

        System.out.println(
                "Telemetry sent to Kafka topic: " + TOPIC
        );
    }
}