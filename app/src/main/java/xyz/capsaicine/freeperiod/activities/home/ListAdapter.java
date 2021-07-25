package xyz.capsaicine.freeperiod.activities.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import xyz.capsaicine.freeperiod.Model.ClassInfo;
import xyz.capsaicine.freeperiod.R;

public class ListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Lecture_List_Item> lectureArrayList;

    // constructor
    public ListAdapter(Context context, ArrayList<Lecture_List_Item> lectureArrayList) {
        this.context = context;
        this.lectureArrayList = lectureArrayList;
    }

    @Override
    public int getCount() {
        return this.lectureArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.lectureArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(Lecture_List_Item item){
        lectureArrayList.add(item);
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        view = View.inflate(context, R.layout.lecture_item, null);
        TextView lectureName_textView = (TextView)view.findViewById(R.id.lectureName);
        TextView startTime_textView = (TextView)view.findViewById(R.id.startTime);
        TextView endTime_textView = (TextView)view.findViewById(R.id.endTime);
        TextView placeName_textView = (TextView)view.findViewById(R.id.placeName);
        TextView profName_textView = (TextView)view.findViewById(R.id.profName);

        Lecture_List_Item item = lectureArrayList.get(position);

        lectureName_textView.setText(item.getLectureName());

        HomeActivity classInfoParser = new HomeActivity();
        ArrayList<ClassInfo> classInfoArrayList = classInfoParser.classInfoParser(item);

        startTime_textView.setText(classInfoArrayList.get(0).getStartHour() + ":" + classInfoArrayList.get(0).getStartMinute());
        endTime_textView.setText(classInfoArrayList.get(0).getEndHour() + ":" + classInfoArrayList.get(0).getEndMinute());
        placeName_textView.setText(classInfoArrayList.get(0).getClassPlace());
//
//        try { // TODO 시간을 그릴 경우에 하루만 그려도 되는지? list view 에 나오는 강의의 정보를 2일씩 할것인지?
//            JSONArray jsonArray = new JSONArray(item.getClassInfo());
//            JSONObject jsonObject = jsonArray.getJSONObject(0);
//            // parse the time into integer and set
//            startTime_textView.setText(jsonObject.getString("start_time"));
//            endTime_textView.setText(jsonObject.getString("end_time"));
//            placeName_textView.setText(jsonObject.getString("place"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        profName_textView.setText(item.getProfName());

        view.setTag(lectureArrayList.get(position).getLectureName());

        return view;
    }
}
