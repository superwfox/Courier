package sudark.courier;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.URISyntaxException;
import java.util.List;

import static sudark.courier.AllowList.QQGroup;

public class EventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerPreLoginEvent e) throws URISyntaxException {

        String plName = e.getName();

        List<List<String>> data = FileManager.readCSV(AllowList.file);
        for (List<String> row : data) {
            if (row.get(1).equals(plName)) {
                if (!row.get(0).equals(e.getUniqueId().toString())) {
                    row.set(0, e.getUniqueId().toString());
                    FileManager.writeCSV(AllowList.file, data);
                }
                return;
            }

            if (row.get(0).equals(e.getUniqueId().toString())) {
                row.set(1, plName);
                OneBotWebsocket.sendG(plName + " |检测到你的ID变动 已自动矫正");
                FileManager.writeCSV(AllowList.file, data);
                return;
            }
        }

        e.setResult(PlayerPreLoginEvent.Result.KICK_WHITELIST);
        e.setKickMessage("你的游戏ID与数据库不匹\n\n请在群聊使用 “绑定” + 空格 + 游戏ID\n\nQQ群： §e§l"+QQGroup);

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {

        Player pl = e.getPlayer();

        List<List<String>> data = FileManager.readCSV(AllowList.file);

        for (List<String> row : data) {
            if (row.get(0).equals(pl.getUniqueId().toString())) {
                if (row.get(3).equals("null")) {
                    row.set(3,pl.getLevel() + "");
                    row.add(pl.getTotalExperience() + "");
                    break;
                }

                row.set(3, pl.getLevel() + "");
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
