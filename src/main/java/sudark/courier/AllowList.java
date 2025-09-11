package sudark.courier;

import org.bukkit.Bukkit;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static sudark.courier.FileManager.readCSV;

public class AllowList {

    static String QQGroup = "1007142639";
    static String superUser = "2963502563";
    static File file = new File(Bukkit.getPluginManager().getPlugin("Courier").getDataFolder(), "allowlist.csv");
    static ConcurrentHashMap<String, String> ChangeName = new ConcurrentHashMap<>();

    public static File getFile(){
        return file;
    }

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

    public void checkList(String qq, String name, OneBotWebsocket obw) throws URISyntaxException {
        List<List<String>> data = readCSV(file);

        if (!name.equals("-1")) {
            for (List<String> list : data) {
                if (list.get(2).equals(qq)) {
                    obw.sendG("当前账号已绑定\n\n   [" + list.get(1) + "]");
                    obw.sendG(" 若要更换请发送 “是” ");
                    ChangeName.put(qq, name);
                    return;
                }
            }
        }

        for(List<String> list : data){
            if(list.get(1).equals(name)){
                obw.sendG("当前账号已被绑定\n\n   [" + list.get(2) + "]");
                return;
            }
        }

        if (ChangeName.containsKey(qq) && name.equals("-1")) {
            for (List<String> list : data) {
                if (list.get(2).equals(qq)) {
                    obw.sendG("已经删除\n[" + list.get(1) + "]\n并替换为\n[" + ChangeName.get(qq) + "]");
                    list.set(1, ChangeName.get(qq));
                    ChangeName.remove(qq);
                    FileManager.writeCSV(file, data);
                    return;
                }
            }
        }

        if (name.equals("-1")) return;
        data.add(List.of("null", name, qq, "null"));
        obw.sendG(" 绑定成功\n\n[" + name + "]");
        FileManager.writeCSV(file, data);
    }

}
