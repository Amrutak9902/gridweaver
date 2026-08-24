package gridweaver.config;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gridweaver.statemachine.BatteryStateMachine;

@Component
public class TelemetryWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BatteryStateMachine batteryStateMachine =
            new BatteryStateMachine();

    private final ExecutorService virtualThreadExecutor =
            Executors.newVirtualThreadPerTaskExecutor();

    // Store all connected WebSocket clients
    private final Set<WebSocketSession> sessions =
            new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        sessions.add(session);

        System.out.println(
                "IoT device connected: " + session.getId()
        );
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message) {

        virtualThreadExecutor.submit(() -> {

            try {

                // Read incoming JSON
                JsonNode data =
                        objectMapper.readTree(message.getPayload());

                // Get device ID
                String deviceId =
                        data.get("deviceId").asText();

                // Get power value
                double power =
                        data.get("power").asDouble();

                // Week 2 state logic
                boolean charging = power <= 80;

                // Update state machine
                batteryStateMachine.updateState(
                        power,
                        charging
                );

                // Get current battery state
                BatteryStateMachine.State currentState =
                        batteryStateMachine.getCurrentState();

                System.out.println(
                        "Device: " + deviceId
                        + " | Power: " + power
                        + " | Charging: " + charging
                        + " | State: " + currentState
                );

                // Create response for frontend
                String response = objectMapper.writeValueAsString(
                        java.util.Map.of(
                                "deviceId", deviceId,
                                "power", power,
                                "charging", charging,
                                "state", currentState.toString()
                        )
                );

                // Send state update to all connected clients
                for (WebSocketSession client : sessions) {

                    if (client.isOpen()) {

                        client.sendMessage(
                                new TextMessage(response)
                        );
                    }
                }

            } catch (Exception e) {

                System.out.println(
                        "Error processing telemetry: "
                        + e.getMessage()
                );
            }
        });
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            org.springframework.web.socket.CloseStatus status) {

        sessions.remove(session);

        System.out.println(
                "IoT device disconnected: " + session.getId()
        );
    }
}