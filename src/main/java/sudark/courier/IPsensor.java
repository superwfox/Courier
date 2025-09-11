package sudark.courier;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IPsensor {

    /**
     * 通过 ip-api.com 获取 IP 归属地
     * 返回格式: 国家|省|市
     */
    public static String getRegion(String ip) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://ip-api.com/json/" + ip + "?lang=zh-CN"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

            if ("success".equalsIgnoreCase(json.get("status").getAsString())) {
                String country = json.get("country").getAsString();
                String region = json.get("regionName").getAsString();
                String city = json.get("city").getAsString();
                return country + "|" + region + "|" + city;
            } else {
                return "未知|未知|未知";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "未知|未知|未知";
        }
    }


    public static void kickByIP(String ip) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getAddress() != null && player.getAddress().getAddress() != null) {
                String playerIP = player.getAddress().getAddress().getHostAddress();
                if (playerIP.equals(ip)) {
                    Bukkit.getScheduler().runTask(Courier.getPlugin(Courier.class), () ->
                            player.kick(Component.text("盗用他人账号"))
                    );
                }
            }
        }
    }
}

