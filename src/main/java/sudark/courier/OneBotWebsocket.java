package sudark.courier;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static sudark.courier.AllowList.*;
import static sudark.courier.EventListener.IPS;
import static sudark.courier.EventListener.bannedIP;
import static sudark.courier.IPsensor.kickByIP;

public class OneBotWebsocket extends WebSocketClient {

    AllowList al = new AllowList();

    public OneBotWebsocket(URI serverUri) {
        super(serverUri);
    }

    public static void sendG(String message) {
        JSONObject connected = new JSONObject();
        JSONObject connectedi = new JSONObject();
        connectedi.put("group_id", QQGroup);
        connectedi.put("message", message);
        connectedi.put("auto_escape", "false");
        connected.put("action", "send_group_msg");
        connected.put("params", connectedi);

        try {
            Courier.client.send(connected.toString());
        } catch (Exception var5) {
            Exception e = var5;
            e.printStackTrace();
        }

    }

    public static void sendP(String user, String message) {
        JSONObject connected = new JSONObject();
        JSONObject connectedi = new JSONObject();
        connectedi.put("user_id", user);
        connectedi.put("message", message);
        connectedi.put("auto_escape", "false");
        connected.put("action", "send_private_msg");
        connected.put("params", connectedi);

        try {
            Courier.client.send(connected.toString());
        } catch (Exception var6) {
            Exception e = var6;
            e.printStackTrace();
        }

    }

    public void sendF(String content, String content2) {
        JSONObject json = new JSONObject();
        JSONObject inner = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject data2 = new JSONObject();
        JSONObject type = new JSONObject();
        JSONObject type2 = new JSONObject();
        JSONObject typeo = new JSONObject();
        JSONObject typeo2 = new JSONObject();
        JSONObject datai = new JSONObject();
        JSONObject datai2 = new JSONObject();
        JSONArray msg = new JSONArray();
        JSONArray contents = new JSONArray();
        JSONArray contents2 = new JSONArray();
        data.put("text", content);
        type.put("type", "text");
        type.put("data", data);
        contents.add(type);
        datai.put("nickname", "DOFES");
        datai.put("user_id", "2963502563");
        datai.put("content", contents);
        typeo.put("type", "node");
        typeo.put("data", datai);
        data2.put("text", content2);
        type2.put("type", "text");
        type2.put("data", data2);
        contents2.add(type2);
        datai2.put("nickname", "DEKUSE");
        datai2.put("user_id", "2963502563");
        datai2.put("content", contents2);
        typeo2.put("type", "node");
        typeo2.put("data", datai2);
        msg.add(typeo);
        msg.add(typeo2);
        inner.put("messages", msg);
        inner.put("group_id", QQGroup);
        inner.put("auto_escape", "false");
        json.put("action", "send_group_forward_msg");
        json.put("params", inner);

        try {
            this.send(json.toString());
        } catch (Exception var17) {
            Exception e = var17;
            e.printStackTrace();
        }

    }

    public void setb() {
        JSONObject json = new JSONObject();
        JSONObject inner = new JSONObject();
        json.put("action", "set_group_whole_ban");
        inner.put("group_id", QQGroup);
        inner.put("enable", "true");
        json.put("params", inner);

        try {
            this.send(json.toString());
        } catch (Exception var4) {
            Exception e = var4;
            e.printStackTrace();
        }

    }

    public void cancellb() {
        JSONObject json = new JSONObject();
        JSONObject inner = new JSONObject();
        json.put("action", "set_group_whole_ban");
        inner.put("group_id", QQGroup);
        inner.put("enable", "false");
        json.put("params", inner);

        try {
            this.send(json.toString());
        } catch (Exception var4) {
            Exception e = var4;
            e.printStackTrace();
        }

    }

    public void like(String qq) {
        JSONObject json = new JSONObject();
        JSONObject inner = new JSONObject();
        json.put("action", "send_like");
        inner.put("user_id", qq);
        inner.put("times", "10");
        json.put("params", inner);

        try {
            this.send(json.toString());
        } catch (Exception var5) {
            Exception e = var5;
            e.printStackTrace();
        }

    }

    public void at(String id) {
        JSONObject json = new JSONObject();
        JSONObject inner = new JSONObject();
        JSONArray msg = new JSONArray();
        JSONObject qq = new JSONObject();
        JSONObject type = new JSONObject();
        json.put("action", "send_group_msg");
        qq.put("qq", id);
        type.put("data", qq);
        type.put("type", "at");
        msg.add(type);
        inner.put("group_id", QQGroup);
        inner.put("message", msg);
        json.put("params", inner);

        try {
            this.send(json.toString());
        } catch (Exception var8) {
            Exception e = var8;
            e.printStackTrace();
        }

    }

    public static void sendD(String base) {
        JSONObject json = new JSONObject();
        JSONObject inner = new JSONObject();
        JSONArray msg = new JSONArray();
        JSONObject qq = new JSONObject();
        JSONObject type = new JSONObject();
        qq.put("file", "base64://" + base);
        type.put("data", qq);
        type.put("type", "image");
        msg.add(type);
        inner.put("group_id", QQGroup);
        inner.put("message", msg);
        json.put("params", inner);
        json.put("action", "send_group_msg");

        try {
            Courier.client.send(json.toString());
        } catch (Exception var7) {
            Exception e = var7;
            e.printStackTrace();
        }

    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

        String Conneted = ChatColor.YELLOW + "" + ChatColor.BOLD + "已连接机器人 | Bot Connected";
        Bukkit.getLogger().info(Conneted);

    }

    @Override
    public void onMessage(String s) {

        JSONObject json = JSONObject.fromObject(s);

        //确认为群聊
        if (json.containsKey("group_id") && json.getString("group_id").equals(QQGroup)) {
            //讨论通知和消息
            switch (json.getString("post_type")) {
                case "message":

                    //确定发件人 qq card
                    JSONObject sender = json.getJSONObject("sender");
                    String card = sender.optString("card", null);
                    if (card == null || card.equals("")) {
                        card = sender.getString("nickname");
                    }
                    String qq = JSONObject.fromObject(sender).getString("user_id");

                    //确定消息内容 msg
                    String msg = "";
                    JSONArray message = json.getJSONArray("message");
                    for (int i = 0; i < message.size(); i++) {
                        JSONObject obj = message.getJSONObject(i);
                        String type = obj.optString("type");
                        switch (type) {
                            case "text":
                                msg += obj.getJSONObject("data").getString("text");
                                break;
                            case "face":
                                msg += "[§b表情§f]";
                                break;
                            case "image":
                                msg += "[§b图片§f]";
                                break;
                            case "at":
                                String nickname = obj.getJSONObject("data").getString("name");
                                msg += "§6§l@" + nickname + "§r§f";
                                break;
                            case "reply":
                                msg += "[§b回复§f]";
                                break;
                            case "video":
                                msg += "[§b视频§f]";
                                break;
                            default:
                                msg += "%";
                        }
                    }

                    if (msg.startsWith("c/") && qq.equals(superUser)) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), msg.substring(2));
                        return;
                    }

                    if (msg.startsWith("绑定 ")) {
                        try {
                            al.checkList(qq, msg.substring(3), this);
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }

                    if (msg.equals("是")) {
                        try {
                            al.checkList(qq, "-1", this);
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }

                    if (msg.equals("查服")) {
                        String result = "";

                        String name;
                        for (Player pl : Bukkit.getOnlinePlayers()) {
                            name = pl.getName();

                            if (pl.getScoreboardTags().contains("vip")) {
                                if (name.contains("+")) {
                                    name = name.replace("+", "■");
                                } else {
                                    name = "■" + name;
                                }
                            } else {
                                name = "□" + name;
                            }

                            result += name + "\n";
                        }

                        result = result + "\n_________________\n以上玩家列表 共" + n2c(Bukkit.getOnlinePlayers().size()) + "人";
                        Runtime runtime = Runtime.getRuntime();
                        long totalMemory = runtime.totalMemory() / 1048576L;
                        long freeMemory = runtime.freeMemory() / 1048576L;
                        long usedMemory = totalMemory - freeMemory;
                        double mspt = Bukkit.getServer().getAverageTickTime();
                        String tps = String.format("%.2f", Math.min(1000.0 / mspt, 20.0));
                        String smspt = String.format("%.3f", mspt);
                        String list2 = "服务器 用内存 " + usedMemory + " MB";
                        list2 = list2 + "\n服务器 余内存 " + freeMemory + " MB";
                        list2 = list2 + "\n\nTPS    " + tps + " / 20.00";
                        list2 = list2 + "\nMSPT " + smspt;
                        if (1000.0 / mspt > 19.5) {
                            list2 = list2 + "\n·\ud83c\udf40 服务器 状态极佳";
                        } else if (1000.0 / mspt < 14.0) {
                            list2 = list2 + "\n·\ud83d\udd25 服务器 状态较差";
                        }

                        this.sendF(result, list2);
                    }

                    if (msg.equals("cf")) {
                        if (Bukkit.getOnlinePlayers().isEmpty()) {
                            this.sendG("服里没人");
                        } else {
                            sendG("服里有" + n2c(Bukkit.getOnlinePlayers().size()) + "个人");
                            Picture.createPic();
                        }

                    }

                    if (qq.equals(superUser) && msg.equals("停一下")) {
                        setb();
                        return;
                    }

                    if (qq.equals(superUser) && msg.equals("好")) {
                        cancellb();
                        return;
                    }

                    if (msg.equals("查账")) {
                        sendG(LevelManager.loadLevel(qq));
                    }

                    if (msg.length() > 25) {
                        msg = msg.substring(0, 25) + "§7...";
                        Bukkit.broadcastMessage("[§e" + card + "§f] " + msg);
                    } else {
                        Bukkit.broadcastMessage("[§e" + card + "§f] " + msg);
                    }

                    break;

                case "notice":

                    if (json.containsKey("notice_type") && json.get("notice_type").equals("group_decrease")) {
                        qq = json.get("user_id").toString();

                        List<List<String>> data = FileManager.readCSV(AllowList.file);

                        for (List<String> row : data) {
                            if (row.get(2).equals(qq)) {
                                data.remove(row);
                                FileManager.writeCSV(AllowList.file, data);
                                sendG("已将 " + row.get(1) + "[" + row.get(2) + "] 从白名单中移除");
                            }
                        }
                    }

                    break;
            }
        }

        //确认为私聊
        if (!(json.containsKey("sender") && json.containsKey("user_id"))) return;

        if (json.getString("message_type").equals("private")) {
            if (json.getString("raw_message").equals("BAN")) {

                JSONObject sender = json.getJSONObject("sender");
                String qq = JSONObject.fromObject(sender).getString("user_id");

                if (IPS.get(qq) != null) {
                    String ip = IPS.get(qq);
                    bannedIP.add(ip);
                    kickByIP(ip);
                    sendP(qq, "已封禁该IP");
                }

            }
        }

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }

    public static String n2c(int n) {
        String[] digits = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        if (n == 0) {
            return "零";
        }

        int ten = n / 10;
        int one = n % 10;

        StringBuilder sb = new StringBuilder();

        if (ten == 1) {
            sb.append("十"); // 10~19
        } else if (ten > 1) {
            sb.append(digits[ten]).append("十");
        }

        if (one > 0) {
            sb.append(digits[one]);
        }

        return sb.toString();
    }

}
