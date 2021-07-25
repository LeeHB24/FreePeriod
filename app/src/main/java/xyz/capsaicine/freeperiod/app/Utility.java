package xyz.capsaicine.freeperiod.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import xyz.capsaicine.freeperiod.R;

import static xyz.capsaicine.freeperiod.app.App.getAppSystemService;

public class Utility {
    public static Bitmap bitmap;

    public static String getDateStringFullVersion(Date date){
        return new SimpleDateFormat("yyyy년 MM월 dd일").format(date);
    }

    public static String getTimeString(Date date){
        return new SimpleDateFormat("HH:mm").format(date);
    }
    public static String getChatRoomTimeString(Date date){
        return new SimpleDateFormat("MM월 dd일 HH:mm").format(date);
    }

    public static ArrayList<String> parseTagList(String tags){
        if(tags == null) return new ArrayList<String>(Arrays.asList("null"));
        ArrayList<String> tagList = new ArrayList<>(Arrays.asList(tags.split("\\s")));
        return tagList;
    }

    public static void getBitmapFromUrl(String url) {
        Thread mThread = new Thread(){
            @Override
            public void run(){
                try{
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                }catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start(); // Thread 실행
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Date parseDateFromString(String dateString) {
        // dateString example: Wed Dec 05 10:20:27 GMT+00:00 2018
        Date date = null;
        try {
            date = new SimpleDateFormat( "EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getTagsInOneString(ArrayList<String> tags, boolean withHash){
        if(tags == null){
            return "";
        }
        String tagsInOneString = "";
        for(String tag : tags){
            if(tag.length() == 0){
                continue;
            }
            if(tagsInOneString.length() != 0){
                tagsInOneString += " ";
            }
            tagsInOneString += (withHash ? "#" + tag : tag);
        }
        return tagsInOneString;
    }

    public static Drawable convertByteArrayToDrawable(byte[] imageInByteArray){
        if(imageInByteArray == null || imageInByteArray.length == 0){
            return App.getAppContext().getResources().getDrawable(R.drawable.yellow);
        }
        return new BitmapDrawable(App.getAppContext().getResources(),
                BitmapFactory.decodeByteArray(imageInByteArray, 0, imageInByteArray.length));
    }

    public static byte[] convertDrawableToByteArray(Drawable drawable){
        if(drawable == null || ((BitmapDrawable)drawable).getBitmap() == null){
            return new byte[0];
        }
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
        return stream.toByteArray();
    }

    public static String getDayInKorean(int dayInWeek){
        switch (dayInWeek){
            case Calendar.SUNDAY:
                return "일요일";
            case Calendar.MONDAY:
                return "월요일";
            case Calendar.TUESDAY:
                return "화요일";
            case Calendar.WEDNESDAY:
                return "수요일";
            case Calendar.THURSDAY:
                return "목요일";
            case Calendar.FRIDAY:
                return "금요일";
            case Calendar.SATURDAY:
                return "토요일";
        }
        return "뭔요일";
    }

    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getAppSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
