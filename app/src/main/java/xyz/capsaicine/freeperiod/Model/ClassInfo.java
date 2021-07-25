package xyz.capsaicine.freeperiod.Model;

public class ClassInfo {
    public enum Day{
        MON, TUE, WED, THU, FRI
    };

    private Day classDay;
    private int classStartTimeByMinute;
    private int classEndTimeByMinute;
    private String classPlace;

    public ClassInfo(Day day, int startTimeByMinute, int endTimeByMinute, String place){
        classDay = day;
        classStartTimeByMinute = startTimeByMinute;
        classEndTimeByMinute = endTimeByMinute;
        classPlace = place;
    }

    public ClassInfo(Day day, int startHour, int startMinute, int endHour, int endMinute, String place){
        classDay = day;
        classStartTimeByMinute = startHour * 60 + startMinute;
        classEndTimeByMinute = endHour * 60 + endMinute;
        classPlace = place;
    }

    // getters
    public int getStartHour(){
        return classStartTimeByMinute / 60;
    }
    public int getStartMinute(){
        return classStartTimeByMinute % 60;
    }
    public int getEndHour(){
        return classEndTimeByMinute / 60;
    }
    public int getEndMinute(){
        return classEndTimeByMinute % 60;
    }
    public int getDurationMinute() {
        return (classEndTimeByMinute - classStartTimeByMinute);
    }
    public Day getDay(){
        return classDay;
    }
    public String getStartTimeString(){
        return String.format("%02d:%02d", getStartHour(), getStartMinute());
    }
    public String getEndTimeString(){
        return String.format("%02d:%02d", getEndHour(), getEndMinute());
    }
    public String getDayString(){
        switch (classDay){
            case MON:
                return "MON";
            case TUE:
                return "TUE";
            case WED:
                return "WED";
            case THU:
                return "THU";
            case FRI:
                return "FRI";
            default:
                return "MON";
        }
    }
    public int getDayInt(){
        switch (classDay){
            case MON:
                return 0;
            case TUE:
                return 1;
            case WED:
                return 2;
            case THU:
                return 3;
            case FRI:
                return 4;
            default:
                return 0;
        }
    }
    public String getTimeStringWithDay(){
        return getStartTimeString() + "~" + getEndTimeString() + "(" + classDay.toString() + ")";
    }
    public String getClassPlace(){
        return classPlace;
    }

    // setters
    public void setClassDay(String classDay) {
        switch (classDay){
            case "MON":
                this.classDay = Day.MON;
                break;
            case "TUE":
                this.classDay = Day.TUE;
                break;
            case "WED":
                this.classDay = Day.WED;
                break;
            case "THU":
                this.classDay = Day.THU;
                break;
            case "FRI":
                this.classDay = Day.FRI;
                break;
            default:
                this.classDay = Day.MON; // default to monday
        }
    }
    public void setClassStartTimeByMinute(int classStartTimeByMinute) {
        this.classStartTimeByMinute = classStartTimeByMinute;
    }
    public void setClassEndTimeByMinute(int classEndTimeByMinute) {
        this.classEndTimeByMinute = classEndTimeByMinute;
    }
    public void setClassPlace(String classPlace) {
        this.classPlace = classPlace;
    }
}
