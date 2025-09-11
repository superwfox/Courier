package sudark.courier;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

public final class Courier extends JavaPlugin {

    public static WebSocketClient client ;
    public static URI uri;

    static {
        try {
            uri = new URI("ws://127.0.0.1:3001");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {

        AllowList al = new AllowList();
        al.checkFile();

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        client = new OneBotWebsocket(uri);
        try {
            client.connectBlocking();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
