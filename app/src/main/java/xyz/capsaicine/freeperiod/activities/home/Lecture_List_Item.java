package xyz.capsaicine.freeperiod.activities.home;

public class Lecture_List_Item {

    public String[] lectureColorValues = {
            "#ff3333", "#ff6633", "#ff9933", "#ffcc33", "#ffff33", "#ccff33", "#99ff33", "#66ff33",
            "#33ff33", "#33ff66", "#33ff99", "#33ffcc", "#33ffff", "#33ccff", "#3399ff", "#3366ff",
            "#3333ff", "#6633ff", "#9933ff", "#cc33ff", "#ff33ff", "#ff33cc", "#ff3399", "#ff3366",
            "#ff3333"
    };

    private int lectureId;
    private String major;
    private String profName;
    private String classInfo;
    private String lectureName;

    public Lecture_List_Item(int lectureId, String major, String profName, String classInfo, String lectureName) {
        this.lectureId = lectureId;
        this.major = major;
        this.profName = profName;
        this.classInfo = classInfo;
        this.lectureName = lectureName;
    }

    public int getLectureId() {
        return lectureId;
    }

    public String getMajor() {
        return major;
    }

    public String getProfName() {
        return profName;
    }

    public String getClassInfo() {
        return classInfo;
    }

    public String getLectureName() {
        return lectureName;
    }

    public String getColorValue(int lectureId) {
        return lectureColorValues[lectureId];
    }

    public void setLectureId(int lectureId) {
        this.lectureId = lectureId;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public void setClassInfo(String classInfo) {
        this.classInfo = classInfo;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }


//    public void setRandomColor() {
//        Random generator = new Random();
//        int count = 0;
//        for (int index = 0; index < lectureColorList.length; index++) {
//            if (lectureColorList[index]) {
//                count++;
//            }
//        }
//        if (count > 6){
//            // 색깔을 더 지정할 수 없다.
//            return;
//        } else {
//            while(true) {
//                int randomColorIndex = generator.nextInt(7);
//                if (!lectureColorList[randomColorIndex]) {
//                    switch (randomColorIndex) {
//                        case 0:
//                            randomLectureColorValue = "#FFDDDD";
//                            break;
//                        case 1:
//                            randomLectureColorValue = "#DDDDFF";
//                            break;
//                        case 2:
//                            randomLectureColorValue = "#FFFFDD";
//                            break;
//                        case 3:
//                            randomLectureColorValue = "#DDFFFF";
//                            break;
//                        case 4:
//                            randomLectureColorValue = "#DDFFDD";
//                            break;
//                        case 5:
//                            randomLectureColorValue = "#EEEEEE";
//                            break;
//                        case 6:
//                            randomLectureColorValue = "#FFDDFF";
//                            break;
//                        default:
//                            break;
//                    }
//                    return;
//                }
//            }
//        }
//
//    }

}
