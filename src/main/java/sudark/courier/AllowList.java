package sudark.courier;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sudark.courier.FileManager.readCSV;

public class AllowList {

    static String QQGroup = "";
    static String superUser = "";
    static File file = new File(Bukkit.getPluginManager().getPlugin("Courier").getDataFolder(), "allowlist.csv");
    static ConcurrentHashMap<String, String> ChangeName = new ConcurrentHashMap<>();

    public void checkFile() {
        File fileFolder = Bukkit.getPluginManager().getPlugin("Courier").getDataFolder();
        if (!fileFolder.exists()) {
            fileFolder.mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void checkList(String qq, String name, OneBotWebsocket obw, boolean GroupMsg) throws URISyntaxException {
        List<List<String>> data = readCSV(file);

        String pendingName = ChangeName.get(qq);

        for (List<String> list : data) {
            String n = list.get(1);
            String q = list.get(2);

            if (q.equals(qq)) { // 找到当前QQ
                if (!name.equals("-1")) { // 有新名字要替换
                    sendMsg(obw, "当前账号已被绑定\n\n   [" + n + "]", GroupMsg, qq);
                    sendMsg(obw, "若要更换 请发送：是", GroupMsg, qq);
                    ChangeName.put(qq, name);
                } else if (pendingName != null) { // 确认更换
                    for (List<String> l : data) {
                        if (l.get(1).equals(pendingName)) {
                            sendMsg(obw, "当前账号已被绑定\n\n   [" + l.get(2) + "]", GroupMsg, qq);
                            return;
                        }
                    }
                    sendMsg(obw, "已经删除\n[" + n + "]\n并替换为\n[" + pendingName + "]", GroupMsg, qq);
                    Player p = Bukkit.getPlayer(n);
                    if (p != null && p.isOnline()) {
                        p.kick(Component.text("为了保证数据安全 自动为您退出游戏"));
                    }
                    list.set(1, pendingName);
                    ChangeName.remove(qq);
                    FileManager.writeCSV(file, data);
                }
                return;
            }
        }

        // 没找到QQ记录
        if (!name.equals("-1")) {
            data.add(List.of("null", name, qq, "null"));
            sendMsg(obw, " 绑定成功\n\n[" + name + "]", GroupMsg, qq);
            FileManager.writeCSV(file, data);
        }
    }

    public static void sendMsg(OneBotWebsocket obw, String msg, boolean GroupMsg, String qq) {
        if (GroupMsg) {
            obw.sendG(msg);
        } else {
            obw.sendP(qq, msg);
        }
    }


}
