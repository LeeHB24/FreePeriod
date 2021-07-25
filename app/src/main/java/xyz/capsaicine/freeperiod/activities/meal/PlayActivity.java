package xyz.capsaicine.freeperiod.activities.meal;

import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.PivotActivity;
import xyz.capsaicine.freeperiod.app.DB;
import xyz.capsaicine.freeperiod.app.Utility;
import xyz.capsaicine.freeperiod.views.RecyclerPartyCardViewAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PlayActivity extends PivotActivity implements Button.OnClickListener{
    private BottomNavigationView navBottom;

    private ArrayList<Party> partyList = new ArrayList<>();

    private RecyclerView recyclerPartyCard;
    private RecyclerView.Adapter adapterRecyclerPartyCard;
    private RecyclerView.LayoutManager managerRecyclerPartyCard;

    private Button btnCreateParty;
    private ImageButton ibtnSearchTime;
    private ImageButton ibtnSearchTag;

    private Calendar pivotStartTimeCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        navBottom = findViewById(R.id.nav_bottom);
        setPivotActivity(PlayActivity.this, navBottom);

        recyclerPartyCard = findViewById(R.id.recycler_partycard);
        managerRecyclerPartyCard = new LinearLayoutManager(this);
        recyclerPartyCard.setLayoutManager(managerRecyclerPartyCard);

        btnCreateParty = findViewById(R.id.btn_create_play_party);
        ibtnSearchTime = findViewById(R.id.ibtn_search_time);
        ibtnSearchTag = findViewById(R.id.ibtn_search_tag);

        btnCreateParty.setOnClickListener(this);
        ibtnSearchTime.setOnClickListener(this);
        ibtnSearchTag.setOnClickListener(this);

        pivotStartTimeCalendar = Calendar.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DB.getInstance().updatePartyList();
        if(!partyList.equals(DB.getInstance().getRecruitPlayPartyList())) {
            partyList = DB.getInstance().getRecruitPlayPartyList();
            adapterRecyclerPartyCard = new RecyclerPartyCardViewAdapter(partyList, this);
            recyclerPartyCard.setAdapter(adapterRecyclerPartyCard);
        }
        String partyPivotDateString = getIntent().getStringExtra("partyPivotDateString");
        if(partyPivotDateString != null){
            Date searchDate = Utility.parseDateFromString(partyPivotDateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(searchDate);
            movePartyListPivot(calendar);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_create_play_party:
                moveToActivity(PartyRegistrationActivity.class);
                break;
            case R.id.ibtn_search_time:
                showSearchTimeDialog();
                break;
            case R.id.ibtn_search_tag:
                moveToActivity(PartySearchingActivity.class);
                break;
        }
    }

    private void moveToActivity(Class nextActivityClass){
        Intent intent = new Intent(PlayActivity.this, nextActivityClass);
        intent.putExtra("partyType", Party.Type.Play);
        startActivity(intent);
    }

    private void movePartyListPivot(Calendar startTimeCalendar){
        if(partyList == null || partyList.size() == 0){
            return ;
        }
        Date startDate = startTimeCalendar.getTime();
        int movePartyPosition = partyList.size() - 1;
        for(int i = 0; i < partyList.size(); i++){
            if(!partyList.get(i).getPartyStartTime().before(startDate)){
                movePartyPosition = i;
                break;
            }
        }
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(movePartyPosition);
        managerRecyclerPartyCard.startSmoothScroll(smoothScroller);
    }

    private void showSearchTimeDialog(){
        TimeSearchingEventDialog dialog = new TimeSearchingEventDialog(
                this,
                new TimeSearchingEventDialog.OnTimeSearchingEventListener() {
                    @Override
                    public void timeSearchingEvent(Calendar searchedTimeCalendar) {
                        pivotStartTimeCalendar.set(Calendar.HOUR_OF_DAY, searchedTimeCalendar.get(Calendar.HOUR_OF_DAY));
                        pivotStartTimeCalendar.set(Calendar.MINUTE, searchedTimeCalendar.get(Calendar.MINUTE));
                        movePartyListPivot(searchedTimeCalendar);
                    }
                });
        dialog.show();
    }
}
