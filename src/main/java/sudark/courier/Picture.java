package sudark.courier;

import org.bukkit.Bukkit;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;

public class Picture {

    public static void createPic() {

        String title = "BEEF-DUNE";
        double mspt = Bukkit.getAverageTickTime();
        String footerText = "[TPS: " + String.format("%.1f", Math.min(1000 / mspt, 20)) + " MSPT: " + String.format("%.1f", mspt) + "]";
        int length = Bukkit.getOnlinePlayers().size();
        String[] players = Bukkit.getOnlinePlayers().stream()
                .map(p -> {
                    String name = p.getName();
                    return length > 6 ? (name.length() > 11 ? name.substring(0, 11) + ".." : name) : name;
                })
                .toArray(String[]::new);

        int rectY = 100;
        int rectX = 50;
        int width = Math.max(600, ((players.length - 1) / 6) * 280 + 340);
        int rectWidth = width - 100;
        int baseHeight = 400;

        BufferedImage image = new BufferedImage(width, baseHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 背景深棕
        g.setColor(new Color(40, 26, 13));
        g.fillRect(0, 0, width, baseHeight);

        // 中间棕色圆角层次背景
        g.setColor(new Color(60, 38, 20));
        g.fill(new RoundRectangle2D.Float(rectX - 10, 40, rectWidth + 20, baseHeight - 35, 60, 60));

        try {
            InputStream fontStream = Courier.class.getResourceAsStream("/pixel.ttf");
            Font pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, 24f);
            Font titleFont = pixelFont.deriveFont(Font.BOLD, 36f);

            // 标题
            g.setFont(titleFont);
            g.setColor(Color.WHITE);
            g.drawString(title, 60, 90);

            // 白色圆角矩形
            g.setColor(Color.WHITE);
            g.fill(new RoundRectangle2D.Float(rectX, rectY, rectWidth, baseHeight - rectY - 5, 50, 50));

            // 玩家列表分两列
            g.setFont(pixelFont);
            g.setColor(Color.BLACK);
            for (int i = 0; i < players.length; i++) {
                int row = i / 6;
                int column = i % 6;
                int x = 70 + row * 320;
                int y = 140 + column * 40;
                g.drawString(players[i], x, y);
            }

            //底部小框
            g.setColor(new Color(60, 38, 20));
            FontMetrics fm = g.getFontMetrics();
            int footerWidth = fm.stringWidth(footerText);
            g.fill(new RoundRectangle2D.Float((float) (width - footerWidth) / 2 - 10, baseHeight - 50, footerWidth + 20, 60, 30, 30));
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(footerText, (width - footerWidth) / 2, baseHeight - 10);

            g.dispose();

            // 转为 Base64 而不写入文件
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            // 发送 Base64 到 OneBot
            OneBotWebsocket.sendD(base64);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
