package xyz.capsaicine.freeperiod.activities.meal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.app.DB;
import xyz.capsaicine.freeperiod.app.Utility;

public class TimeSearchingEventDialog extends Dialog implements View.OnClickListener{

    public interface OnTimeSearchingEventListener{
        void timeSearchingEvent(Calendar searchedTimeCalendar);
    }

    private OnTimeSearchingEventListener onTimeSearchingEventListener;

    private Calendar startTimeCalendar;
    private LinearLayout linearDatePick;
    private TextView txtDate;
    private TextView txtDay;
    private CustomTimePicker customtpStartTime;
    private Button btnSearch;

    public TimeSearchingEventDialog(@NonNull Context context, OnTimeSearchingEventListener onTimeSearchingEventListener) {
        super(context);
        this.onTimeSearchingEventListener = onTimeSearchingEventListener;
        startTimeCalendar = Calendar.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_searching);

        startTimeCalendar = Calendar.getInstance();
        customtpStartTime = findViewById(R.id.customtp_starttime);
        linearDatePick = findViewById(R.id.linear_datepick);
        txtDate = findViewById(R.id.txt_search_date);
        txtDay = findViewById(R.id.txt_search_day);
        btnSearch = findViewById(R.id.btn_search);

        customtpStartTime.initCustomOptions(startTimeCalendar, null);
        txtDate.setText(String.format("%d월 %d일", startTimeCalendar.get(Calendar.MONTH), startTimeCalendar.get(Calendar.DAY_OF_MONTH)));
        txtDay.setText(Utility.getDayInKorean(startTimeCalendar.get(Calendar.DAY_OF_WEEK)));

        linearDatePick.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linear_datepick:
                showDatePicker();
                break;
            case R.id.btn_search:
                onTimeSearchingEventListener.timeSearchingEvent(startTimeCalendar);
                dismiss();
                break;
        }
    }

    private void showDatePicker(){
        DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startTimeCalendar.set(Calendar.YEAR, year);
                startTimeCalendar.set(Calendar.MONTH, monthOfYear);
                startTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                txtDate.setText(String.format("%d월 %d일", startTimeCalendar.get(Calendar.MONTH), startTimeCalendar.get(Calendar.DAY_OF_MONTH)));
                txtDay.setText(Utility.getDayInKorean(startTimeCalendar.get(Calendar.DAY_OF_WEEK)));
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                R.style.ThemeOverlay_AppCompat_Dialog,
                datePicker,
                startTimeCalendar.get(Calendar.YEAR),
                startTimeCalendar.get(Calendar.MONTH),
                startTimeCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(DB.getInstance().getTodayCalendar().getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(DB.getInstance().getAfter7dayCalendar().getTimeInMillis());
        datePickerDialog.show();
    }
}
