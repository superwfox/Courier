package sudark.courier;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class OneBotWebsocket extends WebSocketClient {
    public OneBotWebsocket(URI serverUri) {
        super(serverUri);
    }

    public void sendG(String message) {
        JSONObject connected = new JSONObject();
        JSONObject connectedi = new JSONObject();
        connectedi.put("group_id", "571591801");
        connectedi.put("message", message);
        connectedi.put("auto_escape", "false");
        connected.put("action", "send_group_msg");
        connected.put("params", connectedi);

        try {
            this.send(connected.toString());
        } catch (Exception var5) {
            Exception e = var5;
            e.printStackTrace();
        }

    }

    public void sendP(String user, String message) {
        JSONObject connected = new JSONObject();
        JSONObject connectedi = new JSONObject();
        connectedi.put("user_id", user);
        connectedi.put("message", message);
        connectedi.put("auto_escape", "false");
        connected.put("action", "send_private_msg");
        connected.put("params", connectedi);

        try {
            this.send(connected.toString());
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
        inner.put("group_id", "571591801");
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

    public void sendD() {
        JSONObject json = new JSONObject();
        JSONObject inner = new JSONObject();
        JSONArray msg = new JSONArray();
        JSONObject qq = new JSONObject();
        JSONObject type = new JSONObject();
        qq.put("file", "");
        type.put("data", qq);
        type.put("type", "image");
        msg.add(type);
        inner.put("group_id", "571591801");
        inner.put("message", msg);
        json.put("params", inner);
        json.put("action", "send_group_msg");

        try {
            this.send(json.toString());
        } catch (Exception var7) {
            Exception e = var7;
            e.printStackTrace();
        }

    }

    public void setb() {
        JSONObject json = new JSONObject();
        JSONObject inner = new JSONObject();
        json.put("action", "set_group_whole_ban");
        inner.put("group_id", "571591801");
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
        inner.put("group_id", "571591801");
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
        inner.put("group_id", "571591801");
        inner.put("message", msg);
        json.put("params", inner);

        try {
            this.send(json.toString());
        } catch (Exception var8) {
            Exception e = var8;
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
        if (json.containsKey("group_id") && json.getString("group_id").equals("571591801")) {
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
                    JSONArray message = json.getJSONArray("raw_message");
                    for (int i = 0; i < message.size(); i++) {
                        JSONObject obj = message.getJSONObject(i);
                        String type = obj.optString("type");
                        switch (type) {
                            case "text":
                                msg += obj.getJSONObject("data").getString("text");
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

                    if(qq.equals("2963502563") && msg.equals("停一下")){
                        setb();
                        return;
                    }

                    if(qq.equals("2963502563") && msg.equals("好")){
                        cancellb();
                        return;
                    }





                    if (msg.startsWith("绑定 ")) {

                    }


                    break;
                case "notice":

                    break;
            }
        }

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
}
