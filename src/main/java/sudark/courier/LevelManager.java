package sudark.courier;

import java.util.List;

public class LevelManager {
    public static String loadLevel(String qq) {
        List<List<String>> data = FileManager.readCSV(AllowList.file);

        for (List<String> row : data) {
            if (row.get(2).equals(qq)) {
                return " 当前游戏等级 \n\n   [" + row.get(3) + "]";
            }
        }

        return "你还没有玩过游戏";

    }
}
