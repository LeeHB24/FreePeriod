package xyz.capsaicine.freeperiod.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.capsaicine.freeperiod.R;

public class RecyclerPartyCardViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgPartyCover;
    public TextView txtPartyName;
    public TextView txtCapacity;
    public TextView txtPartyTime;
    public TextView txtCloseTime;
    public TextView txtPartyTags;

    public RecyclerPartyCardViewHolder(View itemView){
        super(itemView);
        imgPartyCover = itemView.findViewById(R.id.img_partycover);
        txtPartyName = itemView.findViewById(R.id.txt_partyname);
        txtCapacity = itemView.findViewById(R.id.txt_capacity);
        txtPartyTime = itemView.findViewById(R.id.txt_partytime);
        txtCloseTime = itemView.findViewById(R.id.txt_closetime);
        txtPartyTags = itemView.findViewById(R.id.txt_partytags);
    }
}
