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
     * 通过宝塔API获取 IP 归属地
     * 返回格式: 国家|省|市|区|运营商
     */
    public static String getRegion(String ip) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.bt.cn/api/panel/get_ip_info?ip=" + ip))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

            if (root.has(ip)) {
                JsonObject data = root.getAsJsonObject(ip);

                String country = data.get("country").getAsString();
                String province = data.get("province").getAsString();
                String city = data.get("city").getAsString();
                String region = data.get("region").getAsString();
                String carrier = data.get("carrier").getAsString();

                return country + "|" + province + "|" + city + "|" + region + "|" + carrier;
            } else {
                return "未知|未知|未知|未知|未知";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "未知|未知|未知|未知|未知";
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

