package xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ReviewItem;

public class ReviewAdapter extends BaseAdapter{

    ArrayList<ReviewItem> reviewList = new ArrayList<>();

    public ArrayList<ReviewItem> getReviewList() {
        return reviewList;
    }

    public void setReviewList(ArrayList<ReviewItem> reviewList) {
        this.reviewList = reviewList;
    }

    public ReviewAdapter(){}
        public ReviewAdapter(ArrayList<ReviewItem> reviewList){
            this.reviewList = reviewList;
        }
        public void addReviewList(ReviewItem reviewItem) {
            reviewList.add(reviewItem);
        }

        @Override
        public int getCount() {
            return reviewList.size();
        }

        @Override
        public ReviewItem getItem(int position) {
            return reviewList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final int pos = position;
            final Context context = parent.getContext();

            RatingBar rate;
            TextView timeStamp;
            TextView reviewContent;


            if(convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.chat_chatuser_review  , parent, false);
            }

            //VIEW 연결
            rate = (RatingBar)convertView.findViewById(R.id.ratingBar_review_rate);
            timeStamp = (TextView)convertView.findViewById(R.id.text_review_timeStamp);
            reviewContent = (TextView)convertView.findViewById(R.id.text_review_content);
            ReviewItem item = reviewList.get(position);


            //VIEW 설정
            rate.setRating(item.getRating());
            timeStamp.setText(item.getTimestamp());
            reviewContent.setText(item.getContent());

            return convertView;

        }
    }