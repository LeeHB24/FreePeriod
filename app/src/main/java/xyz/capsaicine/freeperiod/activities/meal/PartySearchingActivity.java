package xyz.capsaicine.freeperiod.activities.meal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.app.DB;
import xyz.capsaicine.freeperiod.views.RecyclerPartyCardViewAdapter;

public class PartySearchingActivity extends AppCompatActivity implements Button.OnClickListener{
    private RecyclerView recyclerPartyCard;
    private RecyclerView.Adapter adapterRecyclerPartyCard;
    private RecyclerView.LayoutManager managerRecyclerPartyCard;

    private Party.Type partyType;

    private TextView txtSearch;
    private EditText editTag;
    private ImageButton ibtnSearch;
    private ImageButton ibtnBack;

    private ArrayList<Party> partyListWithTag = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_searching);

        partyType = (Party.Type) getIntent().getSerializableExtra("partyType");

        recyclerPartyCard = findViewById(R.id.recycler_partycard);
        managerRecyclerPartyCard = new LinearLayoutManager(this);
        recyclerPartyCard.setLayoutManager(managerRecyclerPartyCard);

        txtSearch = findViewById(R.id.txt_search);
        txtSearch.setText(partyType == Party.Type.Meal ? "밥 모임 태그 검색" : "활동 모임 태그 검색");

        editTag = findViewById(R.id.edit_search_tag);
        ibtnSearch = findViewById(R.id.ibtn_search_tag);
        ibtnBack = findViewById(R.id.ibtn_back_page);

        ibtnSearch.setOnClickListener(this);
        ibtnBack.setOnClickListener(this);

        editTag.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    showMatchTagParty(editTag.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ibtn_back_page:
                finish();
                break;
            case R.id.ibtn_search_tag:
                showMatchTagParty(editTag.getText().toString());
                break;
        }
    }

    private void showMatchTagParty(String targetTag){
        targetTag = targetTag.toLowerCase();
        partyListWithTag = new ArrayList<>();
        ArrayList<Party> partyPool =
                partyType == Party.Type.Meal ?
                DB.getInstance().getRecruitMealPartyList() :
                DB.getInstance().getRecruitPlayPartyList();
        for(Party party : partyPool){
            if(party.getPartyName().toLowerCase().contains(targetTag) || party.containTag(targetTag)){
                partyListWithTag.add(party);
            }
        }
        adapterRecyclerPartyCard = new RecyclerPartyCardViewAdapter(partyListWithTag, this);
        recyclerPartyCard.setAdapter(adapterRecyclerPartyCard);
    }
}
