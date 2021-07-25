package xyz.capsaicine.freeperiod.activities.meal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import mabbas007.tagsedittext.TagsEditText;
import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.app.DB;
import xyz.capsaicine.freeperiod.app.Utility;


public class PartyRegistrationActivity extends AppCompatActivity implements Button.OnClickListener, View.OnFocusChangeListener {

    final private int PICK_IMAGE_REQUEST = 100;

    private Party.Type partyType;

    private ImageButton ibtnPartyCover;
    private TextView txtPartyName;
    private EditText editPartyName;
    private EditText editPartyCapacity;
    private TextView txtPartyDate;
    private TextView txtStartTime;
    private TextView txtEndTime;
    private TextView txtCloseTime;
    private TagsEditText editPartyTag;
    private Button btnRegistParty;

    private LinearLayout linearStartTimePicker;
    private LinearLayout linearEndTimePicker;
    private LinearLayout linearCloseTimePicker;
    private CustomTimePicker timepickerStart;
    private CustomTimePicker timepickerEnd;
    private CustomTimePicker timepickerClose;

    private Calendar startTimeCalendar;
    private Calendar endTimeCalendar;
    private Calendar closeTimeCalendar;

    private Bitmap bitmapCoverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_registration);

        partyType = (Party.Type) getIntent().getSerializableExtra("partyType");

        ibtnPartyCover = findViewById(R.id.ibtn_partycover);
        txtPartyName = findViewById(R.id.txt_partyname);
        txtPartyName.setText(partyType == Party.Type.Meal ? "밥 모임 이름" : "활동 모임 이름");
        editPartyName = findViewById(R.id.edit_partyname);
        editPartyCapacity = findViewById(R.id.edit_partycapacity);
        txtPartyDate = findViewById(R.id.txt_partydate);
        txtStartTime = findViewById(R.id.txt_starttime);
        txtEndTime = findViewById(R.id.txt_endtime);
        txtCloseTime = findViewById(R.id.txt_closetime);
        editPartyTag = findViewById(R.id.tagedit_partytag);
        btnRegistParty = findViewById(R.id.btn_regist_party);

        linearStartTimePicker = findViewById(R.id.linear_start_timepicker);
        linearEndTimePicker = findViewById(R.id.linear_end_timepicker);
        linearCloseTimePicker = findViewById(R.id.linear_close_timepicker);
        timepickerStart = findViewById(R.id.timepicker_starttime);
        timepickerEnd = findViewById(R.id.timepicker_endtime);
        timepickerClose = findViewById(R.id.timepicker_closetime);

        startTimeCalendar = Calendar.getInstance();
        endTimeCalendar = Calendar.getInstance();
        closeTimeCalendar = Calendar.getInstance();
        endTimeCalendar.add(Calendar.HOUR_OF_DAY, 1);
        closeTimeCalendar.add(Calendar.HOUR_OF_DAY, 1);
        closeTimeCalendar.add(Calendar.MINUTE, -10);
        timepickerStart.initCustomOptions(startTimeCalendar, txtStartTime);
        timepickerEnd.initCustomOptions(endTimeCalendar, txtEndTime);
        timepickerClose.initCustomOptions(closeTimeCalendar, txtCloseTime);

        txtPartyDate.setText(Utility.getDateStringFullVersion(startTimeCalendar.getTime()));
        txtStartTime.setText(Utility.getTimeString(startTimeCalendar.getTime()));
        txtEndTime.setText(Utility.getTimeString(endTimeCalendar.getTime()));
        txtCloseTime.setText(Utility.getTimeString(closeTimeCalendar.getTime()));

        ibtnPartyCover.setOnClickListener(this);
        txtPartyDate.setOnClickListener(this);
        txtStartTime.setOnClickListener(this);
        txtEndTime.setOnClickListener(this);
        txtCloseTime.setOnClickListener(this);
        btnRegistParty.setOnClickListener(this);

        editPartyName.setOnFocusChangeListener(this);
        editPartyCapacity.setOnFocusChangeListener(this);
        editPartyTag.setOnFocusChangeListener(this);

        editPartyName.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap src = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                int width = src.getWidth();
                int height = src.getHeight();
                float ratio = 300.0f / Math.max(width, height);
                bitmapCoverImage = Bitmap.createScaledBitmap(src, (int)(width * ratio), (int)(height * ratio), true);
                ibtnPartyCover.setImageBitmap(bitmapCoverImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibtn_partycover:
                loadImageFromGallery();
                break;
            case R.id.txt_partydate:
                showDatePicker();
                break;
            case R.id.txt_starttime:
                showTimePicker(linearStartTimePicker);
                break;
            case R.id.txt_endtime:
                showTimePicker(linearEndTimePicker);
                break;
            case R.id.txt_closetime:
                showTimePicker(linearCloseTimePicker);
                break;
            case R.id.btn_regist_party:
                if(checkRegistValid()) {
                    registNewParty();
                }
                break;
        }
    }

    private void loadImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void showDatePicker(){
        DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startTimeCalendar.set(Calendar.YEAR, year);
                startTimeCalendar.set(Calendar.MONTH, monthOfYear);
                startTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endTimeCalendar.set(Calendar.YEAR, year);
                endTimeCalendar.set(Calendar.MONTH, monthOfYear);
                endTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                closeTimeCalendar.set(Calendar.YEAR, year);
                closeTimeCalendar.set(Calendar.MONTH, monthOfYear);
                closeTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                txtPartyDate.setText(Utility.getDateStringFullVersion(startTimeCalendar.getTime()));
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.ThemeOverlay_AppCompat_Dialog,
                datePicker,
                startTimeCalendar.get(Calendar.YEAR),
                startTimeCalendar.get(Calendar.MONTH),
                startTimeCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(DB.getInstance().getTodayCalendar().getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(DB.getInstance().getAfter7dayCalendar().getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePicker(LinearLayout layoutTimePicker){
        linearStartTimePicker.setVisibility(View.GONE);
        linearEndTimePicker.setVisibility(View.GONE);
        linearCloseTimePicker.setVisibility(View.GONE);
        layoutTimePicker.setVisibility(View.VISIBLE);
        layoutTimePicker.requestFocus();
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        anim.setStartOffset(20);
        layoutTimePicker.startAnimation(anim);
    }

    private boolean checkRegistValid(){
        if(editPartyName.getText().toString().length() == 0){
            showNoticeDialog("모임 이름을 입력해주세요.", editPartyName);
            return false;
        }
        else if(editPartyCapacity.getText().toString().length() == 0){
            showNoticeDialog("모임 인원을 입력해주세요.", editPartyCapacity);
            return false;
        }
        else if(Integer.parseInt(editPartyCapacity.getText().toString()) <= 1){
            showNoticeDialog("모임 인원이 너무 적습니다.", editPartyCapacity);
            return false;
        }
        else if(startTimeCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || startTimeCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            showNoticeDialog("주말에는 모임을 개설할 수 없습니다.", null);
            return false;
        }
        else if(!startTimeCalendar.after(Calendar.getInstance())){
            showTimePicker(linearStartTimePicker);
            showNoticeDialog("모임 시작 시간이 너무 빠릅니다.", timepickerStart);
            return false;
        }
        else if(!endTimeCalendar.after(startTimeCalendar)){
            showTimePicker(linearEndTimePicker);
            showNoticeDialog("모임 종료 시간이 너무 빠릅니다.", timepickerEnd);
            return false;
        }
        else if(!closeTimeCalendar.after(startTimeCalendar)){
            showTimePicker(linearCloseTimePicker);
            showNoticeDialog("모집 마감 시간이 너무 빠릅니다.", timepickerClose);
            return false;
        }
        else if(closeTimeCalendar.after(endTimeCalendar)){
            closeTimeCalendar.setTime(endTimeCalendar.getTime());
        }
        return true;
    }

    private void registNewParty(){
        DB.getInstance().postNewParty(new Party(
                -1,
                partyType,
                Party.Status.Recruiting,
                new BitmapDrawable(bitmapCoverImage),
                editPartyName.getText().toString(),
                new ArrayList<>(editPartyTag.getTags()),
                Integer.parseInt(editPartyCapacity.getText().toString()),
                1,
                startTimeCalendar.getTime(),
                endTimeCalendar.getTime(),
                closeTimeCalendar.getTime()
        ));
        finish();
    }

    private void showNoticeDialog(String msg, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(view != null) {
                    view.requestFocus();
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus){
            Utility.hideKeyboard(v);
        }
    }
}
