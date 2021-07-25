package xyz.capsaicine.freeperiod.activities.mypage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import xyz.capsaicine.freeperiod.R;

public class JoinRoomActivity extends AppCompatActivity {

    ArrayList<String> joinRoom;
    ArrayAdapter<String> adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        joinRoom = new ArrayList<String>();
        //adapter = new ArrayAdapter<String>(JoinRoomActivity.this,android.R.Layout.)
    }



    public void addRoom(View view) {

        //joinRoom.add(room);

    }





}
