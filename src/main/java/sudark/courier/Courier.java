package sudark.courier;

import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

public final class Courier extends JavaPlugin {

    public WebSocketClient client ;

    @Override
    public void onEnable() {

        try {
            client = new OneBotWebsocket(new URI("ws://127.0.0.1:3001"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
