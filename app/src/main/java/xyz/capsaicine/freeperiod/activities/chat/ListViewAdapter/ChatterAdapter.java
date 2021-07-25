package xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.Interface_ChattingClickListener;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatterItem;

public class ChatterAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<ChatterItem> mChatterList;

    private Interface_ChattingClickListener mListener;

    public ArrayList<ChatterItem> getmChatterList() {
        return mChatterList;
    }

    public void setmChatterList(ArrayList<ChatterItem> mChatterList) {
        this.mChatterList = mChatterList;
    }

    public ChatterAdapter(Context mContext, ArrayList<ChatterItem> mChatterList, Interface_ChattingClickListener mListener) {
        this.mContext = mContext;
        this.mChatterList = mChatterList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_chatter, parent, false);
        return new Chatting_ViewHolder_Chatter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatterItem chatter= (ChatterItem)mChatterList.get(position);
        ((Chatting_ViewHolder_Chatter)holder).bind(chatter);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onChatterClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChatterList.size();
    }

    public void addChatter(ChatterItem chatter) {
        mChatterList.add(chatter);
    }

    private class Chatting_ViewHolder_Chatter extends RecyclerView.ViewHolder
    {
        private ImageView mProfileImage;
        private TextView mUserName;
        public Chatting_ViewHolder_Chatter(@NonNull View itemView) {
            super(itemView);

            mProfileImage = (ImageView)itemView.findViewById(R.id.imgView_Chatting_chatter_image);
            mUserName = (TextView)itemView.findViewById(R.id.text_Chatting_chatter_name);
        }

        void bind(ChatterItem chatter)
        {
            // TODO: 2018-10-06
            //mProfileImage.setImageURI(chatter.getUser().getImageUri);

            // TODO: 2018-10-30  Have to get user's name (No function yet)
            mUserName.setText(chatter.getUser().getName());
        }
    }


    public ChatterItem getItem(int position)
    {
        return mChatterList.get(position);
    }

}
