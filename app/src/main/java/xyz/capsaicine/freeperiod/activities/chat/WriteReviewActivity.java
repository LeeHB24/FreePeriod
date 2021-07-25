package xyz.capsaicine.freeperiod.activities.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ReviewItem;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.App;
import xyz.capsaicine.freeperiod.app.NetworkService;

import static xyz.capsaicine.freeperiod.app.App.retrofit;

public class WriteReviewActivity extends AppCompatActivity {

    private int target;
    private TextView showTarget;

    private EditText content;
    private RatingBar starRating;
    private TextView showRating;
    private Button btn_yes;
    private Button btn_no;
    private float rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        NetworkService networkService = App.retrofit.create(NetworkService.class);

        Intent intent = getIntent();
        int userId = Account.getInstance().getUserId();
        int targetId = intent.getExtras().getInt("targetUserId");
        int partyId = intent.getExtras().getInt("partyId");
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        starRating = (RatingBar) findViewById(R.id.rating);
        btn_yes=(Button)findViewById(R.id.btn_yes);
        btn_no=(Button)findViewById(R.id.btn_no);
        showRating = (TextView)findViewById(R.id.showRating);
        content = (EditText)findViewById(R.id.review_content);
        showTarget=(TextView)findViewById(R.id.showTarget);

        starRating.setStepSize((float) 0.5);
        starRating.setRating((float) 2.5);
        starRating.setIsIndicator(false);
        starRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                showRating.setText("평점:"+rating);
            }
        });


        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=starRating.getRating();
                String contentText = content.getText().toString();//리뷰 스트링화
                ReviewItem review = new ReviewItem(userId, targetId, partyId, contentText, rating, date.toString());
                NetworkService networkService = retrofit.create(NetworkService.class);

                Call<reviewInfo> reviewInfoCall = networkService.sendReview(review);
                reviewInfoCall.enqueue(new Callback<reviewInfo>() {
                    @Override
                    public void onResponse(Response<reviewInfo> response, Retrofit retrofit) {
                        reviewInfo result = response.body();
                        if(result.confirm.equals("Success"))
                        {
                            Toast.makeText(getBaseContext(),"리뷰가 작성되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(getBaseContext(),"리뷰작성오류.다시시도해주세요", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getBaseContext(),"리뷰작성오류.다시시도해주세요", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            }
        });

        btn_no.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"리뷰작성이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    //review등록 성공여부 (Test용)
    public class reviewInfo{
        String confirm;
    }

}

