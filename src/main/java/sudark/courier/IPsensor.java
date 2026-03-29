package sudark.courier;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IPsensor {

    private static final String UNKNOWN = "未知";
    private static final String DEFAULT_REGION =
            UNKNOWN + "|" + UNKNOWN + "|" + UNKNOWN + "|" + UNKNOWN + "|" + UNKNOWN;

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /**
     * 通过 ip9 获取 IP 归属地
     * 返回格式: 国家|省|市|区|运营商
     */
    public static String getRegion(String ip) {
        if (ip == null || ip.isBlank()) {
            return DEFAULT_REGION;
        }

        try {
            String rawIp = ip.trim();
            String encodedIp = URLEncoder.encode(rawIp, StandardCharsets.UTF_8);
            String url = "https://ip9.com.cn/get?ip=" + encodedIp;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Courier/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 || response.body() == null || response.body().isBlank()) {
                return DEFAULT_REGION;
            }

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

            // 官方文档示例：ret == 200 表示正常，data 里有归属地字段
            if (!root.has("ret") || root.get("ret").getAsInt() != 200 || !root.has("data") || !root.get("data").isJsonObject()) {
                System.out.println("§7IP 归属地查询返回异常: " + response.body());
                return DEFAULT_REGION;
            }

            JsonObject data = root.getAsJsonObject("data");

            String country = getSafeString(data, "country");
            String province = getSafeString(data, "prov");
            String city = getSafeString(data, "city");
            String area = getSafeString(data, "area");
            String isp = getSafeString(data, "isp");

            return String.join("|", country, province, city, area, isp);

        } catch (Exception e) {
            System.out.println("§7IP 归属地查询失败: " + e.getMessage());
            return DEFAULT_REGION;
        }
    }

    private static String getSafeString(JsonObject obj, String key) {
        if (obj == null || key == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return UNKNOWN;
        }
        try {
            String value = obj.get(key).getAsString();
            return (value == null || value.isBlank()) ? UNKNOWN : value;
        } catch (Exception e) {
            return UNKNOWN;
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

