package xyz.capsaicine.freeperiod.activities.mypage;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.Interface_ChattingClickListener;
import xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter.ChatAdapter;
import xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter.ChatRoomAdapter;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ReviewItem;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.DB;

public class MyReviewAdapter extends RecyclerView.Adapter {
    private final int REVIEWVIEWTYPE = 1;
    ArrayList<ReviewItem> reviewList = new ArrayList<>();
    Interface_myPage_reviewClick mListener = null;

    public Interface_myPage_reviewClick getmListener() {
        return mListener;
    }

    public void setmListener(Interface_myPage_reviewClick mListener) {
        this.mListener = mListener;
    }

    public ArrayList<ReviewItem> getReviewList() {
        return reviewList;
    }

    public void setReviewList(ArrayList<ReviewItem> reviewList) {
        this.reviewList = reviewList;
    }

    public MyReviewAdapter(){

    }

    public MyReviewAdapter(ArrayList<ReviewItem> reviewList, Interface_myPage_reviewClick mListener) {
        this.reviewList = reviewList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == REVIEWVIEWTYPE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_item, parent, false);
            return new reviewHolder(view, mListener);
        }
        return null;
    }
    @Override
    public int getItemViewType(int position) {

        ReviewItem item = (ReviewItem) reviewList.get(position);
        return REVIEWVIEWTYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReviewItem item = reviewList.get(position);
        switch (holder.getItemViewType()) {
            case REVIEWVIEWTYPE:
                ((reviewHolder)holder).bind(item);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public ReviewItem getItem(int pos){
        return reviewList.get(pos);
    }


    private class reviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RatingBar rate;
        TextView timeStamp;
        ImageView reportImage;
        TextView reviewContent;

        public reviewHolder(@NonNull View itemView, Interface_myPage_reviewClick listener) {
            super(itemView);
            rate = (RatingBar)itemView.findViewById(R.id.mypage_review_rate);
            timeStamp = (TextView)itemView.findViewById(R.id.mypage_review_timeStamp);
            reviewContent = (TextView)itemView.findViewById(R.id.mypage_review_content);
            reportImage = (ImageView)itemView.findViewById(R.id.mypage_review_report);
            mListener = listener;
            reportImage.setOnClickListener(this);
        }

        void bind(ReviewItem item)
        {
            rate.setRating(item.getRating());
            timeStamp.setText(item.getTimestamp());
            reviewContent.setText(item.getContent());

        }

        @Override
        public void onClick(View view) {
            mListener.onReviewReportClick(view, getAdapterPosition());
        }
    }





}

