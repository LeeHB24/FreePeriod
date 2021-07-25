package xyz.capsaicine.freeperiod.activities.home;

public class TimeTableCellColor {

    private final String[] ColorValues = {
            "#ffdddd", "#ddddff", "#ffffdd", "#ddffff", "#ddffdd", "#ddffdd", "#ffddff", "#6699ff", "#cc66ff",
            "#ff66cc", "#ff6666", "#ffcc66", "#339933", "#669999"
    };

    public TimeTableCellColor() {
    }

    public String getColorValues(int index) {
        return ColorValues[index % ColorValues.length];
    }

}
