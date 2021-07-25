package xyz.capsaicine.freeperiod.activities.mypage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.DB;
import xyz.capsaicine.freeperiod.app.Utility;
import xyz.capsaicine.freeperiod.views.RecyclerPartyCardViewHolder;

public class myPagePermittedPartyListAdapter extends RecyclerView.Adapter<RecyclerPartyCardViewHolder>{

    private ArrayList<MyPageActivity.userRequest> listParty;
    Context context;
    Context activityContext;
    Interface_myPagePartyListClick mListener;

    public myPagePermittedPartyListAdapter(ArrayList listParty, Context activityContext){
        this.listParty = listParty;
        this.activityContext = activityContext;
    }
    public void setmListener(Interface_myPagePartyListClick mListener) {
        this.mListener = mListener;
    }

    public myPagePermittedPartyListAdapter(ArrayList<MyPageActivity.userRequest> listParty, Context context, Interface_myPagePartyListClick mListener) {
        this.listParty = listParty;
        this.context = context;
        this.mListener = mListener;
    }

    @Override
    public RecyclerPartyCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partycard, parent, false);
        context = parent.getContext();
        RecyclerPartyCardViewHolder holder = new RecyclerPartyCardViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerPartyCardViewHolder holder, final int position){
        Party party = listParty.get(position).getParty();
        holder.imgPartyCover.setImageDrawable(party.getPartyCover());
        holder.txtPartyName.setText(party.getPartyName());
        holder.txtCapacity.setText(party.getCapacityString());
        holder.txtPartyTime.setText(party.getPartyTimeString());
        holder.txtCloseTime.setText(party.getRemainTimeString());
        holder.txtPartyTags.setText(Utility.getTagsInOneString(party.getPartyTagList(), true));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(mListener!=null){
                mListener.onPermittedPartyClick(view, position);
            }
            }
        });
    }

    public MyPageActivity.userRequest getItem(int pos){
        return listParty.get(pos);
    }

    @Override
    public int getItemCount(){
        return listParty.size();
    }
}
