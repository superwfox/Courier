package sudark.courier;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.*;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;
import static sudark.courier.AllowList.QQGroup;
import static sudark.courier.IPsensor.getRegion;

public class EventListener implements Listener {

    static Map<String, String> IPS = new HashMap<>();
    static Set<String> bannedIP = new HashSet<>();

    @EventHandler
    public void onPlayerJoin(PlayerPreLoginEvent e) {
        String address = e.getAddress().getHostAddress();
        if (bannedIP.contains(address)) {
            e.setResult(PlayerPreLoginEvent.Result.KICK_WHITELIST);
            e.setKickMessage("你的IP已被封禁\n\n由于§e登录其他玩家账号");
            return;
        }

        String plName = e.getName();
        UUID plUUID = e.getUniqueId();

        List<List<String>> data = FileManager.readCSV(AllowList.file);

        for (List<String> row : data) {
            if (row.get(1).equals(plName)) {
                if (!row.get(0).equals(plUUID.toString())) {
                    row.set(0, plUUID.toString());
                    OneBotWebsocket.sendG(plName + " |检测到你的UUID变动 已自动矫正");
                    FileManager.writeCSV(AllowList.file, data);
                }
                changeLevel(plUUID, Float.parseFloat(row.get(3)));
                return;
            }
        }

        e.setResult(PlayerPreLoginEvent.Result.KICK_WHITELIST);
        e.setKickMessage("你的游戏ID与数据库不匹\n\n请在群聊使用 “绑定” + 空格 + 游戏ID\n\nQQ群： §e§l" + QQGroup);
    }

    public static void changeLevel(UUID plUUID, float lvl) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player pl = Bukkit.getPlayer(plUUID);
                if (pl != null) {
                    pl.setExp(lvl - (int) lvl);
                    pl.setLevel((int) lvl);
                    cancel();
                }
            }
        }.runTaskTimer(getPlugin(Courier.class), 10L, 10L);
    }

    @EventHandler
    public void checkPlayerIdentity(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        if (pl.hasMetadata("ONLINE")) return;
        NamespacedKey key = new NamespacedKey("sudark", "qq");
        String qq = pl.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (IPS.containsKey("qq")) return;

        String ip = "";
        InetSocketAddress socketAddress = pl.getAddress();
        if (socketAddress != null && socketAddress.getAddress() != null) {
            ip = socketAddress.getAddress().getHostAddress();
        } else {
            System.out.println("无法获取玩家 IP");
        }

        String ipMsg = pl.getName() + "[" + getRegion(ip) + "] ";
        System.out.println(ipMsg);

        OneBotWebsocket.sendP(qq, "您的账号 " + ipMsg + "已上线\n\n " +
                "如果这不是你本人操作请发送“BAN”来封禁该IP\n5分钟后无回应将视为正常情况");
        IPS.put(qq, ip);
        Bukkit.getScheduler().runTaskLater(Courier.getPlugin(Courier.class), () -> IPS.remove(qq), 300 * 20L);

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {

        Player pl = e.getPlayer();

        List<List<String>> data = FileManager.readCSV(AllowList.file);
        float lvl = pl.getLevel() + pl.getExp();

        for (List<String> row : data) {
            if (row.get(0).equals(pl.getUniqueId().toString())) {
                row.set(3, String.format("%.3f", lvl));
                if (row.size() < 5) {
                    row.add(pl.getTotalExperience() + "");
                } else {
                    row.set(4, pl.getTotalExperience() + "");
                }
            }
        }

        FileManager.writeCSV(AllowList.file, data);

    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) throws URISyntaxException {
        String msg = e.getMessage();
        String pl = e.getPlayer().getName().replaceAll("\\.", "");

        if (msg.startsWith("#")) {
            OneBotWebsocket.sendG(" [" + pl + "]\n   " + msg.substring(1));
        }
    }
}
