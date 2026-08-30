package gridweaver.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gridweaver.config.TelemetryWebSocketHandler;
import gridweaver.statemachine.BatteryStateMachine;

@Service
public class TelemetryKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final BatteryStateMachine batteryStateMachine;
    private final TelemetryWebSocketHandler webSocketHandler;

    public TelemetryKafkaConsumer(
            BatteryStateMachine batteryStateMachine,
            TelemetryWebSocketHandler webSocketHandler) {

        this.objectMapper = new ObjectMapper();
        this.batteryStateMachine = batteryStateMachine;
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

            // Read device ID
            String deviceId =
                    data.get("deviceId").asText();

            // Read power value
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

            // Send update to WebSocket clients
            webSocketHandler.broadcastStateUpdate(
                    deviceId,
                    power,
                    charging,
                    currentState
            );

        } catch (Exception e) {

            System.err.println(
                    "Error consuming telemetry from Kafka: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}