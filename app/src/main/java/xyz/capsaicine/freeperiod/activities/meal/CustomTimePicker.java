package xyz.capsaicine.freeperiod.activities.meal;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DecimalFormat;
import java.util.Calendar;

import xyz.capsaicine.freeperiod.app.Utility;

public class CustomTimePicker extends TimePicker {
    private static final int MINUTE_INTERVAL = 5;
    private final static int MIN_HOUR = 9;
    private final static int MAX_HOUR = 20;

    private static final DecimalFormat FORMATTER = new DecimalFormat("00");
    private NumberPicker minutePicker;
    private NumberPicker hourPicker;

    public TextView txtTime;
    public Calendar timeCalendar;

    public CustomTimePicker(Context context) {
        super(context);
    }

    public CustomTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomTimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void initCustomOptions(Calendar timeCalendar, TextView txtTime){
        this.setIs24HourView(true);
        this.txtTime = txtTime;
        this.timeCalendar = timeCalendar;
        initHourPicker();
        initMinutePicker();
        setCurrentTime();
        if(txtTime != null) {
            txtTime.setText(Utility.getTimeString(timeCalendar.getTime()));
        }
        setOnTimeChangedListener(new OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setCurrentTime();
                if(txtTime != null) {
                    txtTime.setText(Utility.getTimeString(timeCalendar.getTime()));
                }
            }
        });
    }

    private void setCurrentTime(){
        timeCalendar.set(Calendar.HOUR_OF_DAY, getHour());
        timeCalendar.set(Calendar.MINUTE, getMinute());
    }

    private void initHourPicker() {
        int hourNumValues = MAX_HOUR - MIN_HOUR + 1;
        String[] hourDisplayedValues = new String[hourNumValues];
        for(int i = 0; i < hourNumValues; i++) {
            hourDisplayedValues[i] = FORMATTER.format(MIN_HOUR + i);
        }
        View hour = this.findViewById(Resources.getSystem().getIdentifier("hour", "id", "android"));
        if((hour != null) && (hour instanceof NumberPicker)){
            hourPicker = (NumberPicker) hour;
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(hourNumValues - 1);
            hourPicker.setWrapSelectorWheel(false);
            hourPicker.setDisplayedValues(hourDisplayedValues);
            hourPicker.setValue(Math.min(timeCalendar.get(Calendar.HOUR_OF_DAY) - MIN_HOUR, hourNumValues - 1));
        }
    }

    private void initMinutePicker() {
        int minuteNumValues = 60 / MINUTE_INTERVAL;
        String[] minuteDisplayedValues = new String[minuteNumValues];
        for (int i = 0; i < minuteNumValues; i++) {
            minuteDisplayedValues[i] = FORMATTER.format(i * MINUTE_INTERVAL);
        }
        View minute = this.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"));
        if ((minute != null) && (minute instanceof NumberPicker)) {
            minutePicker = (NumberPicker) minute;
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(minuteNumValues - 1);
            minutePicker.setWrapSelectorWheel(false);
            minutePicker.setDisplayedValues(minuteDisplayedValues);
            minutePicker.setValue(timeCalendar.get(Calendar.MINUTE) / MINUTE_INTERVAL);
        }
    }

    public int getMinute() {
        if (minutePicker != null) {
            return (minutePicker.getValue() * MINUTE_INTERVAL);
        } else {
            return this.getCurrentMinute();
        }
    }

    public int getHour(){
        if(hourPicker != null){
            return MIN_HOUR + hourPicker.getValue();
        }
        else{
            return this.getCurrentHour();
        }
    }
}
