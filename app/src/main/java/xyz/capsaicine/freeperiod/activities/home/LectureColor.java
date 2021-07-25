package xyz.capsaicine.freeperiod.activities.home;

public class LectureColor {

    private final String[] ColorValues = {
            "#ff3333", "#ff6633", "#ff9933", "#ffcc33", "#ffff33", "#ccff33", "#99ff33", "#66ff33",
            "#33ff33", "#33ff66", "#33ff99", "#33ffcc", "#33ffff", "#33ccff", "#3399ff", "#3366ff",
            "#3333ff", "#6633ff", "#9933ff", "#cc33ff", "#ff33ff", "#ff33cc", "#ff3399", "#ff3366",
            "#ff3333"
    };
    private boolean[] isColorUsedIn = {
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false
    };

    private int lectureId;
    private String lectureColorValue;

    public LectureColor() {
    }

    public LectureColor(int lectureId, String lectureColorValue) {
        this.lectureId = lectureId;
        this.lectureColorValue = lectureColorValue;
    }

    public String[] getColorValues() {
        return ColorValues;
    }

    public boolean[] getIsColorUsedIn() {
        return isColorUsedIn;
    }

    public int getLectureId() {
        return lectureId;
    }

    public void setLectureId(int lectureId) {
        this.lectureId = lectureId;
    }

    public String getLectureColorValue() {
        return lectureColorValue;
    }

    public void setLectureColorValue(String lectureColorValue) {
        this.lectureColorValue = lectureColorValue;
    }
}
