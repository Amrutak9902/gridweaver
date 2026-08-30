package gridweaver.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gridweaver.config.TelemetryWebSocketHandler;
import gridweaver.statemachine.BatteryStateMachine;

@Service
public class TelemetryKafkaConsumer {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    private final BatteryStateMachine batteryStateMachine =
            new BatteryStateMachine();

    private final TelemetryWebSocketHandler webSocketHandler;

    public TelemetryKafkaConsumer(
            TelemetryWebSocketHandler webSocketHandler) {

        this.webSocketHandler = webSocketHandler;
    }

    @KafkaListener(
            topics = "gridweaver-telemetry",
            groupId = "gridweaver-consumer"
    )
    public void consumeTelemetry(String message) {

        try {

            System.out.println(
                    "Telemetry received from Kafka: "
                    + message
            );

            // Convert Kafka message to JSON
            JsonNode data =
                    objectMapper.readTree(message);

            // Read telemetry values
            String deviceId =
                    data.get("deviceId").asText();

            double power =
                    data.get("power").asDouble();

            // Determine charging status
            boolean charging =
                    power <= 80;

            // Update battery state machine
            batteryStateMachine.updateState(
                    power,
                    charging
            );

            // Get current battery state
            BatteryStateMachine.State currentState =
                    batteryStateMachine.getCurrentState();

            System.out.println(
                    "Kafka Consumer | Device: "
                    + deviceId
                    + " | Power: "
                    + power
                    + " | Charging: "
                    + charging
                    + " | State: "
                    + currentState
            );

            // Send state update back to WebSocket clients
            webSocketHandler.broadcastStateUpdate(
                    deviceId,
                    power,
                    charging,
                    currentState
            );

        } catch (Exception e) {

            System.out.println(
                    "Error consuming telemetry from Kafka: "
                    + e.getMessage()
            );
        }
    }
}