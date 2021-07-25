package xyz.capsaicine.freeperiod.Model;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import xyz.capsaicine.freeperiod.app.Utility;

public class Party{

    public enum Type{ Meal, Play }
    public enum Status{ Recruiting, Closed, Finished, Blocked }

    private int partyId;
    private Type partyType;
    private Status partyStatus;
    private Drawable partyCover;
    private String partyName;
    private ArrayList<String> partyTagList;
    private int capacityMax;
    private int capacityCurrent;
    private Date partyStartTime;
    private Date partyEndTime;
    private Date partyCloseTime;
    private Calendar partyStartTimeInCalendar;
    private Calendar partyEndTimeInCalendar;
    private Calendar partyCloseTimeInCalendar;

    public Party(int partyId, Type partyType, Status partyStatus, Drawable partyCover, String partyName, ArrayList<String> partyTagList, int capacityMax, int capacityCurrent, Date partyStartTime, Date partyEndTime, Date partyCloseTime) {
        this.partyId = partyId;
        this.partyType = partyType;
        this.partyStatus = partyStatus;
        this.partyCover = partyCover;
        this.partyName = partyName;
        this.partyTagList = partyTagList;
        this.capacityMax = capacityMax;
        this.capacityCurrent = capacityCurrent;
        this.partyStartTime = partyStartTime;
        this.partyEndTime = partyEndTime;
        this.partyCloseTime = partyCloseTime;
        partyStartTimeInCalendar = Calendar.getInstance();
        partyStartTimeInCalendar.setTime(partyStartTime);
        partyEndTimeInCalendar = Calendar.getInstance();
        partyEndTimeInCalendar.setTime(partyEndTime);
        partyCloseTimeInCalendar = Calendar.getInstance();
        partyCloseTimeInCalendar.setTime(partyCloseTime);
    }

    public Party(PartyInDatabase partyInDatabase){
        partyId = partyInDatabase.getPartyId();
        partyType = Type.valueOf(partyInDatabase.getPartyType());
        partyStatus = Status.valueOf(partyInDatabase.getPartyStatus());
        partyCover = Utility.convertByteArrayToDrawable(partyInDatabase.getPartyCover());
        partyName = partyInDatabase.getPartyName();
        partyTagList = Utility.parseTagList(partyInDatabase.getPartyTag());
        capacityMax = partyInDatabase.getCapacityMax();
        capacityCurrent = partyInDatabase.getCapacityCurrent();
        partyStartTime = Utility.parseDateFromString(partyInDatabase.getPartyStartTime());
        partyEndTime = Utility.parseDateFromString(partyInDatabase.getPartyEndTime());
        partyCloseTime = Utility.parseDateFromString(partyInDatabase.getPartyCloseTime());
        partyStartTimeInCalendar = Calendar.getInstance();
        partyStartTimeInCalendar.setTime(partyStartTime);
        partyEndTimeInCalendar = Calendar.getInstance();
        partyEndTimeInCalendar.setTime(partyEndTime);
        partyCloseTimeInCalendar = Calendar.getInstance();
        partyCloseTimeInCalendar.setTime(partyCloseTime);
    }

    @Override
    public boolean equals(Object obj){
        return partyId == ((Party)obj).getPartyId();
    }

    public boolean containTag(String targetTag){
        for(String tag : partyTagList){
            if(tag.toLowerCase().contains(targetTag)){
                return true;
            }
        }
        return false;
    }

    public String getCapacityString(){
        return "(" + capacityCurrent + "/" + capacityMax + ")";
    }

    // 추가
    public int getStartHour() {
        return partyStartTimeInCalendar.get(Calendar.HOUR_OF_DAY);
    }
    public int getStartMinute() {
        return partyStartTimeInCalendar.get(Calendar.MINUTE);
    }
    public int getEndHour() {
        return partyEndTimeInCalendar.get(Calendar.HOUR_OF_DAY);
    }
    public int getEndMinute() {
        return partyEndTimeInCalendar.get(Calendar.MINUTE);
    }
    public int getDurationMunute() {
        return (getEndHour() * 60 + getEndMinute() - (getStartHour() * 60 + getStartMinute()));
    }

    public int getDayInt() {
        return partyStartTimeInCalendar.get(Calendar.DAY_OF_WEEK);
    }
    // 추가 끝

    public String getPartyTimeString(){
        return String.format("%02d:%02d-%02d:%02d(%s)",
                partyStartTimeInCalendar.get(Calendar.HOUR_OF_DAY),
                partyStartTimeInCalendar.get(Calendar.MINUTE),
                partyEndTimeInCalendar.get(Calendar.HOUR_OF_DAY),
                partyEndTimeInCalendar.get(Calendar.MINUTE),
//                partyStartTimeInCalendar.get(Calendar.DAY_OF_WEEK));
                getDayOfWeek(partyStartTimeInCalendar.get(Calendar.DAY_OF_WEEK)));
    }


    public String getDayOfWeek(int dayOfWeek){
        String day = "";
        switch(dayOfWeek){
            case 1:
                day += "SUN";
                break;
            case 2:
                day += "MON";
                break;
            case 3:
                day += "TUE";
                break;
            case 4:
                day += "WED";
                break;
            case 5:
                day += "THU";
                break;
            case 6:
                day += "FRI";
                break;
            case 7:
                day += "SAT";
                break;
        }
        return day;
    }

    public String getRemainTimeString(){
        long remainTime = partyCloseTime.getTime() - Calendar.getInstance().getTime().getTime();
        if(remainTime / (1000 * 60 * 60 * 24) >= 1) return remainTime / (1000 * 60 * 60 * 24) + "day";
        else if(remainTime / (1000 * 60 * 60) >= 1) return remainTime / (1000* 60 * 60) + "hour";
        return remainTime / (1000 * 60) + "min";
    }

    public int getPartyId() {
        return partyId;
    }

    public Type getPartyType() {
        return partyType;
    }

    public Status getPartyStatus() {
        return partyStatus;
    }

    public Drawable getPartyCover() {
        return partyCover;
    }

    public String getPartyName() {
        return partyName;
    }

    public ArrayList<String> getPartyTagList() {
        return partyTagList;
    }

    public int getCapacityMax() {
        return capacityMax;
    }

    public int getCapacityCurrent() {
        return capacityCurrent;
    }

    public Date getPartyStartTime() {
        return partyStartTime;
    }

    public Date getPartyEndTime() {
        return partyEndTime;
    }

    public Date getPartyCloseTime() {
        return partyCloseTime;
    }

    public void setPartyId(int partyId) {
        this.partyId = partyId;
    }

    public void setPartyType(Type partyType) {
        this.partyType = partyType;
    }

    public void setPartyStatus(Status partyStatus) {
        this.partyStatus = partyStatus;
    }

    public void setPartyCover(Drawable partyCover) {
        this.partyCover = partyCover;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setPartyTagList(ArrayList<String> partyTagList) {
        this.partyTagList = partyTagList;
    }

    public void setCapacityMax(int capacityMax) {
        this.capacityMax = capacityMax;
    }

    public void setCapacityCurrent(int capacityCurrent) {
        this.capacityCurrent = capacityCurrent;
    }

    public void setPartyStartTime(Date partyStartTime) {
        this.partyStartTime = partyStartTime;
        partyStartTimeInCalendar = Calendar.getInstance();
        partyStartTimeInCalendar.setTime(partyStartTime);
    }

    public void setPartyEndTime(Date partyEndTime) {
        this.partyEndTime = partyEndTime;
        partyEndTimeInCalendar = Calendar.getInstance();
        partyEndTimeInCalendar.setTime(partyEndTime);
    }

    public void setPartyCloseTime(Date partyCloseTime) {
        this.partyCloseTime = partyCloseTime;
    }

}
