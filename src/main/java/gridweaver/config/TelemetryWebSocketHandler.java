package gridweaver.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class TelemetryWebSocketHandler extends TextWebSocketHandler {

    private final ExecutorService virtualThreadExecutor =
            Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        System.out.println(
                "IoT device connected: " + session.getId()
        );
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message) {

        virtualThreadExecutor.submit(() -> {

            System.out.println(
                    "Telemetry received from "
                    + session.getId()
                    + " : "
                    + message.getPayload()
            );

        });
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            org.springframework.web.socket.CloseStatus status) {

        System.out.println(
                "IoT device disconnected: " + session.getId()
        );
    }
}