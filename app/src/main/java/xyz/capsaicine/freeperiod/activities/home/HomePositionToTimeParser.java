package xyz.capsaicine.freeperiod.activities.home;

import java.util.Calendar;
import java.util.Date;

public class HomePositionToTimeParser {

    private int x;
    private int y;
    private int timeTableBlocWidth;
    private int timeTableBlockHeight;
    private int locX;
    private int locY;
    final private int[] maxDateForEachMonth = {31, 30, 31, 28, 31, 30, 31, 31, 30, 31, 30, 31};

    public HomePositionToTimeParser(int x, int y, int timeTableBlocWidth, int timeTableBlockHeight) {
        this.x = x;
        this.y = y;
        this.timeTableBlocWidth = timeTableBlocWidth;
        this.timeTableBlockHeight = timeTableBlockHeight;
        this.locX = x / timeTableBlocWidth;
        this.locY = y / timeTableBlockHeight;
    }

    public String dayFromPosition() {
        String dateOfClickedPosition = "";
        switch (locX) {
            case 0:
                dateOfClickedPosition = "월요일";
                break;
            case 1:
                dateOfClickedPosition = "화요일";
                break;
            case 2:
                dateOfClickedPosition = "수요일";
                break;
            case 3:
                dateOfClickedPosition = "목요일";
                break;
            case 4:
                dateOfClickedPosition = "금요일";
                break;
            default:
                break;
        }
        return dateOfClickedPosition;
    }

    public String dayStateFromPosition() {
        String dayState = "오전";
        int hourOfClickedPosition = locY / 2 + 9;
        if (hourOfClickedPosition > 12) {
            dayState = "오후";
        }
        return dayState;
    }

    public int getHourFromPosition() {
        int locY = y / timeTableBlockHeight;
        int hourOfClickedPosition = locY / 2 + 9;
        if (hourOfClickedPosition > 12) {
            hourOfClickedPosition = hourOfClickedPosition - 12;
        }
        return hourOfClickedPosition;
    }

    public int getMinuteFromPosition() {
        return (locY % 2) * 30;
    }

    public String getDateByString() {

        // Thu Nov 29 19:55:28 GMT+09:00 2018
        Date currentTime = Calendar.getInstance().getTime();
        Calendar currentTimeCalendar = Calendar.getInstance();
        currentTimeCalendar.setTime(currentTime);
        int dayOfCurrentTime = currentTimeCalendar.get(Calendar.DAY_OF_WEEK) - 2; // 0 (월)
//        int yearOfCurrentTime = currentTimeCalendar.get(Calendar.YEAR); // 2018
        int dateOfCurrentTime = currentTimeCalendar.get(Calendar.DATE); // 29
//        int monthOfCurrentTime = currentTimeCalendar.get(Calendar.MONTH); // 11월 -> 10
        int dateGap = locX - dayOfCurrentTime;

        int partyPivotDate = dateOfCurrentTime + dateGap;

        currentTimeCalendar.set(Calendar.DATE, partyPivotDate);
        currentTimeCalendar.set(Calendar.HOUR_OF_DAY, getHourFromPosition());
        currentTimeCalendar.set(Calendar.MINUTE, getMinuteFromPosition());
        currentTimeCalendar.set(Calendar.SECOND, 0);

        currentTime = currentTimeCalendar.getTime();

        String partyPivotDateString = currentTime.toString();

        return partyPivotDateString;
    }
}
