package sudark.courier;

import org.bukkit.Bukkit;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sudark.courier.FileManager.readCSV;

public class AllowList {

    static File file = new File(Bukkit.getPluginsFolder(), "allowlist.csv");

    public static void checkFile() {
        File fileFolder = Bukkit.getPluginsFolder();
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

    public static void checkList(String qq, String name) throws URISyntaxException {
        List<List<String>> data = readCSV(file);
        OneBotWebsocket obw = new OneBotWebsocket(new URI("ws://127.0.0.1:3001"));
        ConcurrentHashMap<String, Boolean> ChangeName = new ConcurrentHashMap<>();

        if (name.equals("null")) {
            ChangeName.put(qq, false);
            return;
        }

        ChangeName.putIfAbsent(qq, false);

        if (ChangeName.get(qq)) {
            for (List<String> list : data) {
                if (list.get(2).equals(qq)) {
                    ChangeName.put(qq, false);
                    data.remove(list);
                    return;
                }
            }
        } else {
            for (List<String> list : data) {
                if (list.get(2).equals(qq)) {
                    obw.sendG("当前账号已绑定\n\n   [" + list.get(1) + "]");
                    obw.sendG(" 若要更换请发送 “是” ");
                    ChangeName.put(qq, true);
                    return;
                }
            }
        }
        data.add(List.of("null", name, qq,"null"));
        obw.sendG(" 绑定成功 \n\n   [" + name + "]");
        FileManager.writeCSV(file, data);
    }

}
