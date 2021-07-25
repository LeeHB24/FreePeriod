package xyz.capsaicine.freeperiod.activities.mypage;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ReviewItem;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.App;
import xyz.capsaicine.freeperiod.app.DB;
import xyz.capsaicine.freeperiod.app.NetworkService;

public class MyReviewActivity extends AppCompatActivity implements Interface_myPage_reviewClick{

    Button btn_report_yes, btn_report_no;
    TextView review_report_content;
    Dialog reportReviewDialog;


    RatingBar ratingReview;
    float rate;
    String strrate;
    TextView ratingNumber;
    private MyReviewAdapter mReviewAdapter;
    private RecyclerView mReviewListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ArrayList<ReviewItem> reviewList = new ArrayList<ReviewItem>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);
        setReportDialog();
        mReviewListView = (RecyclerView) findViewById(R.id.mypage_listView);
        ratingReview = (RatingBar) findViewById(R.id.ratingReview);
        ratingNumber = (TextView)findViewById(R.id.rateNumber);

        mReviewAdapter = new MyReviewAdapter(reviewList, this);
        mReviewListView.setAdapter(mReviewAdapter);
        mReviewListView.setLayoutManager(new LinearLayoutManager(this));
        getMyReviewList(Account.getInstance().getUserId());
    }

    private void getMyReviewList(int userId){
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        Call<ArrayList<ReviewItem>> getUserReviewList = networkService.getMyReviewList(userId);
        getUserReviewList.enqueue(new Callback<ArrayList<ReviewItem>>() {
            @Override
            public void onResponse(Response<ArrayList<ReviewItem>> response, Retrofit retrofit) {
                ArrayList<ReviewItem> myReviewList = response.body();
                float ratingSum = 0;
                for(int i = 0; i < myReviewList.size(); i++){
                    ratingSum += myReviewList.get(i).getRating();
                }
                mReviewAdapter.setReviewList(myReviewList);
                mReviewAdapter.notifyDataSetChanged();
                rate = ratingSum / myReviewList.size();
                ratingReview.setRating(rate);
                strrate = Float.toString(rate);
                ratingNumber.setText(strrate);
            }
            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void onReviewReportClick(View view, int pos) {
        showReportReviewDialog(mReviewAdapter.getItem(pos).getReviewIndex(),mReviewAdapter.getItem(pos).getContent());
    }
    private void setReportDialog(){
        reportReviewDialog = new Dialog(this);
        reportReviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    private void showReportReviewDialog(int reviewIndex, String content){
        reportReviewDialog.setContentView(R.layout.mypage_report_dialog);
        btn_report_yes = reportReviewDialog.findViewById(R.id.btn_report_yes);
        btn_report_no = reportReviewDialog.findViewById(R.id.btn_report_no);
        review_report_content = reportReviewDialog.findViewById(R.id.text_report_content);
        review_report_content.setText(content);
        btn_report_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportReviewDialog.dismiss();
            }
        });
        btn_report_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportReviewDialog.dismiss();
                DB.getInstance().postReportReview(reviewIndex);
            }
        });
        reportReviewDialog.show();
    }
}
