package gridweaver;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IoTLoadTest {

    private static final int TOTAL_CONNECTIONS = 10_000;

    private static final String WEBSOCKET_URL =
            "ws://localhost:8080/ws/telemetry";

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        // Virtual threads for handling many WebSocket connection tasks
        ExecutorService virtualThreadExecutor =
                Executors.newVirtualThreadPerTaskExecutor();

        List<Future<WebSocket>> connections =
                new ArrayList<>();

        System.out.println(
                "Starting " + TOTAL_CONNECTIONS +
                " concurrent IoT WebSocket connections..."
        );

        for (int i = 1; i <= TOTAL_CONNECTIONS; i++) {

            int deviceId = i;

            Future<WebSocket> connection =
                    virtualThreadExecutor.submit(() -> {

                        return client.newWebSocketBuilder()
                                .buildAsync(
                                        URI.create(WEBSOCKET_URL),

                                        new WebSocket.Listener() {

                                            @Override
                                            public void onOpen(
                                                    WebSocket webSocket) {

                                                System.out.println(
                                                        "Device " +
                                                        deviceId +
                                                        " connected"
                                                );

                                                webSocket.sendText(
                                                        "{\"deviceId\":\"device-"
                                                                + deviceId
                                                                + "\",\"power\":100}",
                                                        true
                                                );

                                                WebSocket.Listener.super
                                                        .onOpen(webSocket);
                                            }

                                            @Override
                                            public void onError(
                                                    WebSocket webSocket,
                                                    Throwable error) {

                                                System.out.println(
                                                        "Device " +
                                                        deviceId +
                                                        " error: " +
                                                        error.getMessage()
                                                );
                                            }
                                        }
                                )
                                .join();
                    });

            connections.add(connection);

            if (i % 1000 == 0) {
                System.out.println(
                        i + " connection requests started..."
                );
            }
        }

        // Wait for all connection attempts
        for (Future<WebSocket> connection : connections) {
            try {
                connection.get();
            } catch (Exception e) {
                System.out.println(
                        "Connection failed: " +
                        e.getMessage()
                );
            }
        }

        System.out.println(
                "All WebSocket connection attempts completed."
        );

        // Keep connections alive for 30 seconds
        Thread.sleep(30_000);

        System.out.println(
                "Load test finished."
        );

        virtualThreadExecutor.shutdown();
    }
}