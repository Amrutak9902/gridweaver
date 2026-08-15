package gridweaver;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class IoTLoadTest {

    private static final int TOTAL_CONNECTIONS = 100;
    private static final String WEBSOCKET_URL =
            "ws://localhost:8080/ws/telemetry";

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        List<CompletableFuture<WebSocket>> connections =
                new ArrayList<>();

        System.out.println(
                "Starting " + TOTAL_CONNECTIONS +
                " concurrent IoT WebSocket connections..."
        );

        for (int i = 1; i <= TOTAL_CONNECTIONS; i++) {

            int deviceId = i;

            CompletableFuture<WebSocket> connection =
                    client.newWebSocketBuilder()
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
                                                    "{\"deviceId\":\"device-" +
                                                    deviceId +
                                                    "\",\"power\":100}",
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
                            );

            connections.add(connection);

            if (i % 1000 == 0) {
                System.out.println(
                        i + " connection requests started..."
                );
            }
        }

        CompletableFuture.allOf(
                connections.toArray(new CompletableFuture[0])
        ).join();

        System.out.println(
                "All WebSocket connection attempts completed."
        );

        Thread.sleep(30_000);

        System.out.println(
                "Load test finished."
        );
    }
}