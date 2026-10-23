package nz.ac.ara.comp713.booking_service.live;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import nz.ac.ara.comp713.booking_service.security.TokenStore;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// the websocket server, same idea as LiveServer in the messaging lab
// REST saves the data, the websocket only tells open browser tabs that something changed
// it listens on its own port (live.ws.port, 8084 by default) next to the normal web server on 8082
@Component
public class LiveServer extends WebSocketServer {

    private static final Gson JSON = new Gson();

    // every browser tab that has subscribed, same as the subscribers set in the lab
    private final Set<WebSocket> subscribers = ConcurrentHashMap.newKeySet();

    // one number that goes up by 1 for every event, the browser uses it to ignore old events
    private final AtomicLong version = new AtomicLong();

    // makes sure events are numbered and sent one at a time, so version 7 never arrives before version 6
    private final Object publishLock = new Object();

    private final TokenStore tokenStore;

    // what is sent to the browsers: ShowingCreated or SeatsChanged, its version, and the showing as it is now
    record Event(String type, long version, ShowingData showing) {}

    public LiveServer(@Value("${live.ws.port:8084}") int port, TokenStore tokenStore) {
        super(new InetSocketAddress(port));
        this.tokenStore = tokenStore;
        setReuseAddr(true);
    }

    // starts listening when the spring app starts, stops when it shuts down
    @PostConstruct
    public void begin() { start(); }

    @PreDestroy
    public void end() {
        try { stop(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    @Override public void onStart() { System.out.println("WEBSOCKET READY ws://localhost:" + getPort()); }
    @Override public void onOpen(WebSocket socket, ClientHandshake handshake) {}
    @Override public void onClose(WebSocket socket, int code, String reason, boolean remote) { subscribers.remove(socket); }
    @Override public void onError(WebSocket socket, Exception error) { System.err.println("WebSocket: " + error.getMessage()); }

    // the browser sends {"type":"subscribe","token":"..."} straight after connecting
    // the login token is checked so only logged in users get live updates
    @Override
    public void onMessage(WebSocket socket, String message) {
        try {
            JsonObject request = JSON.fromJson(message, JsonObject.class);
            if (!request.has("type") || !request.get("type").getAsString().equals("subscribe")) {
                socket.send("{\"type\":\"error\",\"message\":\"Send a subscribe message first\"}");
                return;
            }

            String token = request.has("token") ? request.get("token").getAsString() : "";
            if (tokenStore.find(token).isEmpty()) {
                socket.send("{\"type\":\"error\",\"message\":\"Login required\"}");
                socket.close();
                return;
            }

            // register first, then confirm, so the browser can safely fetch the snapshot after the reply
            // anything published from now on will reach this tab
            subscribers.add(socket);
            socket.send("{\"type\":\"subscribed\",\"version\":" + version.get() + "}");
        } catch (Exception e) {
            socket.send("{\"type\":\"error\",\"message\":\"Invalid request\"}");
        }
    }

    // gives the event the next version number and sends it to every open tab
    // called after the change has been saved, same as LiveTasks.publish in the lab
    public void publish(String type, ShowingData showing) {
        synchronized (publishLock) {
            long next = version.incrementAndGet();
            String json = JSON.toJson(new Event(type, next, showing));
            for (WebSocket socket : subscribers) {
                if (socket.isOpen()) {
                    socket.send(json);
                }
            }
        }
    }

    // the latest version number, sent with the REST snapshot so the browser knows where to carry on from
    public long currentVersion() { return version.get(); }
}