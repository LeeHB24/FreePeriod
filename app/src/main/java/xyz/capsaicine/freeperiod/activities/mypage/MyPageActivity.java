package xyz.capsaicine.freeperiod.activities.mypage;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.Model.PartyInDatabase;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.PivotActivity;
import xyz.capsaicine.freeperiod.activities.chat.ChatDBCtrct;
import xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter.ReviewAdapter;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ReviewItem;
import xyz.capsaicine.freeperiod.activities.login.LoginActivity;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.App;
import xyz.capsaicine.freeperiod.app.Firebase.MyFirebaseInstanceIdService;
import xyz.capsaicine.freeperiod.app.NetworkService;
import xyz.capsaicine.freeperiod.app.Utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyPageActivity extends PivotActivity implements View.OnClickListener, Interface_myPagePartyListClick{
    private BottomNavigationView navBottom;

    //Button
    Button joinRoomButton;
    Button waitRoomButton;
    Button requestRoomButton;

    //Dialog --Logout
    private Dialog logoutDialog;
    private Button btn_logout_yes;
    private Button btn_logout_no;
    //Dialog -- Profile
    private Dialog profileDialog;
    private ImageButton btn_cancelProfile;
    private ImageView imgView_profileImg;
    private RatingBar ratingBar_rate;
    private ImageButton accept;
    private ImageButton reject;
    private TextView text_name;
    private ListView mReviewListView;
    private ArrayList<ReviewItem> reviewList = new ArrayList<ReviewItem>();

    //Dialog Notify Message
    private Button btn_cancel_yes;
    private Button btn_cancel_no;
    private Dialog cancelRequestDialog;


    private ReviewAdapter mReviewAdapter;

    myPagePermittedPartyListAdapter permittedAdapter;
    myPageWaitingPartyListAdapter waitingAdapter;

    Interface_myPagePartyListClick mListener;
    private RecyclerView mPartyCardRecycler;
    private myPagePermittedPartyListAdapter mPartyListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setProfileWindow();
        setLogoutWindow();
        setCancelRequestDialogWindow();

        mListener= this;
        mPartyCardRecycler = (RecyclerView) findViewById(R.id.recyclerView_mypage_partylist);
        mPartyCardRecycler.setLayoutManager(new LinearLayoutManager(this));

        navBottom = findViewById(R.id.nav_bottom);
        setPivotActivity(MyPageActivity.this, navBottom);

        Button passwordChangeButton = (Button) findViewById(R.id.btn_change_pw);
         joinRoomButton = (Button)findViewById(R.id.btn_joining);//참가중
       waitRoomButton = (Button)findViewById(R.id.btn_waiting);//대기중
        requestRoomButton = (Button)findViewById(R.id.btn_permitting);//요청대기중
        Button lookMyReviewButton = (Button)findViewById(R.id.btn_my_review);
        Button logoutButton = (Button)findViewById(R.id.btn_logout);

        joinRoomButton.setOnClickListener(this);
        waitRoomButton.setOnClickListener(this);
        requestRoomButton.setOnClickListener(this);

        passwordChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordChange();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showLogoutDialog();
            }
        });

        lookMyReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewIntent = new Intent(MyPageActivity.this, MyReviewActivity.class);
                MyPageActivity.this.startActivity(reviewIntent);
            }
        });


    }



private void passwordChange()
{
 LayoutInflater vi =(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    LinearLayout changepwLayout = (LinearLayout) vi.inflate(R.layout.dialog_pwchange,null);

    final EditText currentpwText;
    final EditText changepwText;
    currentpwText = (EditText)changepwLayout.findViewById(R.id.currentpwText);
    changepwText = (EditText)changepwLayout.findViewById(R.id.changepwText);

    new AlertDialog.Builder(this)
            .setTitle("비밀번호변경")
            .setView(changepwLayout)
            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(
                            MyPageActivity.this,
                            "현재비밀번호: "+currentpwText.getText().toString()+
                                    "\n변경비밀번호: "+changepwText.getText().toString(),
                            Toast.LENGTH_LONG).show();

                }
            })
            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

    }

    private void setAutoLoginFalse(){
        SharedPreferences auto = getSharedPreferences("auto", 0);
        SharedPreferences.Editor editor = auto.edit();
        //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
        editor.clear();
        editor.commit();
    }


    private void setColor(int id){
        if(id == R.id.btn_joining){
            joinRoomButton.setTextColor(Color.WHITE);
            waitRoomButton.setTextColor(Color.BLACK);
            requestRoomButton.setTextColor(Color.BLACK);
        }
        if(id == R.id.btn_waiting){
            joinRoomButton.setTextColor(Color.BLACK);
            waitRoomButton.setTextColor(Color.WHITE);
            requestRoomButton.setTextColor(Color.BLACK);
        }
        if(id == R.id.btn_permitting){
            joinRoomButton.setTextColor(Color.BLACK);
            waitRoomButton.setTextColor(Color.BLACK);
            requestRoomButton.setTextColor(Color.WHITE);
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.btn_joining: {
                setColor(id);
                setJoiningAdapter(this);
                break;
            }
            case R.id.btn_waiting: {
                setColor(id);
                setWaitingAdapter(this);
                break;
            }
            case R.id.btn_permitting: {
                setColor(id);
                setPermittingAdapter(this);
                break;
            }
            case R.id.imgbtn_dialog_cancel:{
                profileDialog.dismiss();
                break;
            }
        }


    }
    private void setJoiningAdapter(Context context){
        getJoinedPartyList(Account.getInstance().getUserId(), context);
    }
    private void setWaitingAdapter(Context context){
        getWaitingPartyList(Account.getInstance().getUserId(), context);

    }
    private void setPermittingAdapter(Context context){
        getPermittedPartyList(Account.getInstance().getUserId(), context);
    }



    public void getJoinedPartyList(int userId, Context context){
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        ArrayList<Party> partyList = new ArrayList<>();
        Call<ArrayList<PartyInDatabase>> getJoinedPartyList = networkService.getJoinedPartyList(userId);
        getJoinedPartyList.enqueue(new Callback<ArrayList<PartyInDatabase>>() {
            public void onResponse(Response<ArrayList<PartyInDatabase>> response, Retrofit retrofit) {
                ArrayList<PartyInDatabase> partyInDatabaseList = response.body();
                for(PartyInDatabase partyInDatabase : partyInDatabaseList){
                    Utility.getBitmapFromUrl("http://partyhae.com/web/skin/party_category_bnr.png");
                    Party party = new Party(partyInDatabase);
                    partyList.add(party);
                }
                mPartyCardRecycler.setAdapter(new myPageJoinedPartyAdapter(partyList, context));
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(context, "참가중 목록을 받아올 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getWaitingPartyList(int userId, Context context){
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        ArrayList<Party> partyList = new ArrayList<>();
        Call<ArrayList<PartyInDatabase>> getWaitingPartyList = networkService.getWaitingPartyList(userId);
        getWaitingPartyList.enqueue(new Callback<ArrayList<PartyInDatabase>>() {
            public void onResponse(Response<ArrayList<PartyInDatabase>> response, Retrofit retrofit) {
                ArrayList<PartyInDatabase> partyInDatabaseList = response.body();
                for(PartyInDatabase partyInDatabase : partyInDatabaseList){
                    Utility.getBitmapFromUrl("http://partyhae.com/web/skin/party_category_bnr.png");
                    Party party = new Party(partyInDatabase);
                    partyList.add(party);
                }
                waitingAdapter =  new myPageWaitingPartyListAdapter(partyList, context, mListener);
                mPartyCardRecycler.setAdapter(waitingAdapter);
                waitingAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void getPermittedPartyList(int userId, Context context){
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        ArrayList<userRequest> partyList = new ArrayList<>();
        Call<ArrayList<userDBRequest>> getPermittedPartyList = networkService.getRequestList(userId);
        getPermittedPartyList.enqueue(new Callback<ArrayList<userDBRequest>>() {
            public void onResponse(Response<ArrayList<userDBRequest>> response, Retrofit retrofit) {
                ArrayList<userDBRequest> partyInDatabaseList = response.body();
                for(userDBRequest partyInDatabase : partyInDatabaseList){
                    Utility.getBitmapFromUrl("http://partyhae.com/web/skin/party_category_bnr.png");
                    userRequest party = new userRequest(partyInDatabase.getRequestUserId(),new Party(partyInDatabase.getParty()));
                    partyList.add(party);
                }
                permittedAdapter =new myPagePermittedPartyListAdapter(partyList, context, mListener);
                mPartyCardRecycler.setAdapter(permittedAdapter);
                permittedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }



    @Override
    public void onWaitingPartyClick(View view, int pos) {
        Party item = waitingAdapter.getItem(pos);
        showCancelRequestDialog(Account.getInstance().getUserId(), item.getPartyId());
    }
    @Override
    public void onPermittedPartyClick(View view, int pos) {
        userRequest item = permittedAdapter.getItem(pos);
        showProfile(item.getRequestUserId(), item.getParty().getPartyId());
    }

    public class userRequest{
        private int requestUserId;
        private Party party;
        public userRequest(int requestUserId, Party party) {
            this.requestUserId = requestUserId;
            this.party = party;
        }
        public int getRequestUserId() {
            return requestUserId;
        }

        public void setRequestUserId(int requestUserId) {
            this.requestUserId = requestUserId;
        }

        public Party getParty() {
            return party;
        }

        public void setParty(Party party) {
            this.party = party;
        }
    }

    public class userDBRequest{
        private int requestUserId;
        private PartyInDatabase party;

        public int getRequestUserId() {
            return requestUserId;
        }

        public void setRequestUserId(int requestUserId) {
            this.requestUserId = requestUserId;
        }

        public PartyInDatabase getParty() {
            return party;
        }

        public void setParty(PartyInDatabase party) {
            this.party = party;
        }
    }

    private void setLogoutWindow(){
       logoutDialog = new Dialog(this);
        logoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    private void showLogoutDialog()
    {
        logoutDialog.setContentView(R.layout.mypage_warning_dialog);
        btn_logout_yes = logoutDialog.findViewById(R.id.btn_logout_yes);
        btn_logout_no = logoutDialog.findViewById(R.id.btn_logout_no);
        btn_logout_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logoutIntent = new Intent(MyPageActivity.this, LoginActivity.class);
                setAutoLoginFalse();
                MyFirebaseInstanceIdService fb = new MyFirebaseInstanceIdService();
                //SENDING TO SERVER THE USER TOKEN
                fb.sendRegistrationToServer(Account.getInstance().getUserId(), "");
                getApplicationContext().deleteDatabase(ChatDBCtrct.chatDB);
                MyPageActivity.this.startActivity(logoutIntent);
                finish();
            }
        });
        btn_logout_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog.dismiss();
            }
        });
        logoutDialog.show();
    }


    private void setProfileWindow(){
        profileDialog = new Dialog(this);
        profileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    private void showProfile(int userId, int partyId)
    {
        profileDialog.setContentView(R.layout.requested_user_profile_dialog);
        btn_cancelProfile = (ImageButton) profileDialog.findViewById(R.id.imgbtn_dialog_cancel);
        imgView_profileImg = (ImageView) profileDialog.findViewById(R.id.img_dialog_image);
//        text_name = (TextView)profileDialog.findViewById(R.id.text_dialog_name);
        mReviewListView = (ListView) profileDialog.findViewById(R.id.listView_review);
        ratingBar_rate = (RatingBar) profileDialog.findViewById(R.id.rating_dialog_rate);
        accept = profileDialog.findViewById(R.id.btn_dialog_accept);
        reject = profileDialog.findViewById(R.id.btn_dialog_reject);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postPartyAcceptance(userId,partyId,true);
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                postPartyAcceptance(userId,partyId,false);
            }
            ;
        });
        btn_cancelProfile.setOnClickListener(this);
        setProfile(userId);
        profileDialog.show();
    }
    //Profile창에서 User의 이미지, 이름, 평점, 리뷰를 가져옴
    private void setProfile(int userId) {
//        setProfileName();
        setProfileRate(userId);
        setProfileReview(userId);
        setProfileImage(userId);
    }
    //유저의 이미지를 가져와 프로필 창에 저장
    private void setProfileImage(int userId){
    }
    //유저의 이름을 가져와 프로필 창에 저장
//    private void setProfileName(ChatUser mUser){
//        text_name.setText(mUser.getName());
//    }
    //유저의 평점을 가져와 프로필 창에 저장
    private void setProfileRate(int userId){
        //Server에 "Get" User's Rate // User's ID를 보냄 --> rate에 저장
        float rate = 4;
        ratingBar_rate.setRating(rate);
        //QUERY FOR GETTING USER RATE
//        getUserRate(mUser.getId());
    }
    //유저의 리뷰를 가져와 프로필 창에 저장
    private void setProfileReview(int userId){

        mReviewAdapter = new ReviewAdapter();
        mReviewListView.setAdapter(mReviewAdapter);
//        QUERY FOR GETTING REVIEW LIST
        getUserReviewList(userId);
    }

    private void  getUserReviewList(int userId){
        reviewList.clear();
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        Call<ArrayList<ReviewItem>> getUserReviewList = networkService.getMyReviewList(userId);
        getUserReviewList.enqueue(new Callback<ArrayList<ReviewItem>>() {
            @Override
            public void onResponse(Response<ArrayList<ReviewItem>> response, Retrofit retrofit) {
                ArrayList<ReviewItem> myReviewList = response.body();
                mReviewAdapter.setReviewList(myReviewList);
                mReviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void postPartyAcceptance(int userId,int partyId, boolean isAccept){
        profileDialog.dismiss();
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        Call<Integer> postAcceptance = networkService.postAcceptance(userId,partyId,isAccept);
        postAcceptance.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Response<Integer> response, Retrofit retrofit) {
                getPermittedPartyList(Account.getInstance().getUserId(), getApplicationContext());
                Toast.makeText(MyPageActivity.this, "성공", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
//                Toast.makeText(MyPageActivity.this, "요청 실패", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void postCancelRequest(int userId,int partyId){
        profileDialog.dismiss();
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        Call<Integer> cancelJoinParty = networkService.postCancelJoinParty(userId,partyId);
        cancelJoinParty.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Response<Integer> response, Retrofit retrofit) {
                getWaitingPartyList(userId, getApplicationContext());
//                Toast.makeText(MyPageActivity.this, "성공", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable t) {
//                Toast.makeText(MyPageActivity.this, "요청 실패", Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void setCancelRequestDialogWindow(){
        cancelRequestDialog = new Dialog(this);
        cancelRequestDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    private void showCancelRequestDialog(int userId, int partyId){
        cancelRequestDialog.setContentView(R.layout.mypage_cancel_request_dialog);
        btn_cancel_yes = cancelRequestDialog.findViewById(R.id.btn_cancel_yes);
        btn_cancel_no = cancelRequestDialog.findViewById(R.id.btn_cancel_no);
        btn_cancel_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRequestDialog.dismiss();
            }
        });
        btn_cancel_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRequestDialog.dismiss();
                postCancelRequest(userId, partyId);
                Toast.makeText(MyPageActivity.this, "요청이 취소되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        cancelRequestDialog.show();
    }



}
