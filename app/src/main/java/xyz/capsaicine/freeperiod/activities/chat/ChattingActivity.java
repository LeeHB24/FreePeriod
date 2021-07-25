package xyz.capsaicine.freeperiod.activities.chat;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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


import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter.ChatAdapter;
import xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter.ChatterAdapter;
import xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter.ReviewAdapter;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatRoomItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatterItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ReviewItem;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.App;
import xyz.capsaicine.freeperiod.app.DB;
import xyz.capsaicine.freeperiod.app.NetworkService;

import static android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
import static android.support.v4.widget.DrawerLayout.LOCK_MODE_UNLOCKED;

public class ChattingActivity extends AppCompatActivity implements DrawerLayout.DrawerListener, View.OnClickListener, Interface_ChattingClickListener, TextWatcher,Interface_updateChatScreen{


    private ArrayList<ChatterItem> chatterList = new ArrayList<ChatterItem>();
    private ArrayList<ReviewItem> reviewList = new ArrayList<ReviewItem>();
    //Have to Change below to current room name;
    private int curRoomId = -1;
    private String curRoomName;
    private Party.Status curRoomStauts;
    private int partyLeader;

    private Interface_updateChatScreen mListener;
//    BroadcastReceiver mReceiver;

    public final DBHelper dbHelper = new DBHelper(this, ChatDBCtrct.chatDB, null, 1);


    private LinearLayout notifiactionLayout;

    //각 종 View 선언
    private ImageButton btn_Send; //메세지 전송 버튼
    private DrawerLayout mDrawer; //Drawer (공지, 프로필 등을 담음)
    private EditText mMessage; //메세지를 쓰는 곳
    private InputMethodManager imm; //키보드 관리자
    private TextView mMainNotification;
    private TextView mDrawerNotification;


    //Dialog -- closeroom
    Dialog closeRoomDialog;
    Button btn_closeroom_yes, btn_closeroom_no;
    //Dialog -- leaveroom
    Dialog leaveRoomDialog;
    Button btn_leaveroom_yes, btn_leaveroom_no;

    //Dialog -- Profile
    private Dialog profileDialog;
    private ImageButton btn_cancelProfile;
    private ImageView imgView_profileImg;
    private RatingBar ratingBar_rate;
    private Button btn_review;
    private Button btn_report;
    private TextView text_name;
    private ListView mReviewListView;

    private int lastSearchIndex; // 채팅 검색 Index 마지막 위치
    //Dialog -- searchchatfinished
    private Button btn_searchchatDialog_ok;
    private Dialog searchChatDialog;
    //Dialog Notify Message
    private Button btn_notifychat_yes;
    private Button btn_notifychat_no;
    private Dialog notifyChatDialog;


    // TODO: 2018-10-30  Have to be changed ChatUser --> User .getInstance
    //User선언
    private ChatUser mUser; // 유저의 정보 저장해야함 일단 임의로 설정 나중에 Static 으로 선언
    //RecyclerView 선언
    private RecyclerView mChatRecycler;
    private RecyclerView mChatterRecycler;

    //Adapter 선언
    private ChatterAdapter mChatterAdapter;
    private ChatAdapter mChatAdapter;
    private ReviewAdapter mReviewAdapter;
    //Toolbar 관련 변수
    private Toolbar mToolbar;
    private EditText mSearchChat;
    private TextView mRoomName;
    private View mLayoutToolbar_origin;
    private View mLayoutToolbar_clicksearch;
    private ImageButton btn_back;
    private ImageButton btn_searchCancel;
    private ImageButton btn_openDrawer;
    private ImageButton btn_search;
    private ImageButton btn_tmpSearch;

    //Drawer 내 버튼 설정
    private ImageButton btn_leaveRoom;
    private ImageButton btn_map;
    private Button btn_block;


    ChatService myService;
    boolean isService = false; // 서비스 중인 확인용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener = this;
        curRoomId = getIntent().getIntExtra(ChatDBCtrct.roomId, -1);
        ChatRoomItem chatRoomInfo = dbHelper.getChatRoomItem(curRoomId);
        curRoomStauts = Party.Status.valueOf(chatRoomInfo.getStatus());
        curRoomName = chatRoomInfo.getRoomName();
        partyLeader = chatRoomInfo.getPartyLeader();
//        Toast.makeText(this, "Received Party ID :"+ curRoomId, Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_chatting);
        setProfileWindow();
        setSearchChatDialogWindow();
        setNotifyChatDialogWindow();
        setLeaveRoomWindow();
        setcloseRoomWindow();
/***********************View 초기화************************************/
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//Drawer꺼낼 때 키보드 내리기
        mDrawer = findViewById(R.id.Chatting_drawerLayout);
        mMessage = findViewById(R.id.editText_Chatting_Message);
        btn_Send = findViewById(R.id.imgBtn_Chatting_SendMessage);
        mChatRecycler = findViewById(R.id.recylerView_Chatting_Chat);
        mChatterRecycler = findViewById(R.id.recyclerView_chat_drawer_Chatter);
        mMainNotification = findViewById(R.id.text_Chatting_mainNotification);

        notifiactionLayout = (LinearLayout)findViewById(R.id.layout_Chatting_mainNotification);

        mDrawerNotification = findViewById(R.id.text_chat_drawer_notification);
        mToolbar = findViewById(R.id.toolbar_chatting);
        //메세지 오래 클릭하면 생기는 버튼
        btn_leaveRoom = findViewById(R.id.imgbtn_chatting_leaveRoom);
        btn_map  = findViewById(R.id.imgbtn_chatting_map);
        btn_block = findViewById(R.id.imgbtn_chatting_blockRoom);
        Log.i("ACCOUNTID", Account.getInstance().getUserId()+"");
        Log.i("PARTYLEADER", partyLeader+"");
        if(partyLeader != Account.getInstance().getUserId()){
            btn_block.setVisibility(View.GONE);
        }else{
            btn_leaveRoom.setVisibility(View.GONE);
        }
        /************************progile 관련 설정 ***************************/
        btn_review = findViewById(R.id.btn_dialog_review);

        /***********************Toolbar관련 설정**************************************************/
        setSupportActionBar(mToolbar);
        mSearchChat = findViewById(R.id.edittext_toolbarChat_searchtext);
        mSearchChat.addTextChangedListener(this);
        mRoomName = findViewById(R.id.text_toolbarChat_roomName);
        mLayoutToolbar_origin = findViewById(R.id.chat_toolbarLayout_origin);
        mLayoutToolbar_clicksearch = findViewById(R.id.chat_toolbarLayout_clicksearch);
        btn_back = findViewById(R.id.imgbtn_toolbarChat_back);
        btn_search = findViewById(R.id.imgbtn_toolbarChat_search);
        btn_searchCancel = findViewById(R.id.imgbtn_toolbarChat_searchcancel);
        btn_openDrawer = findViewById(R.id.imgbtn_toolbarChat_drawer);
        btn_tmpSearch= findViewById(R.id.imgbtn_toolbarChat_tmpsearch);

        btn_back.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_searchCancel.setOnClickListener(this);
        btn_openDrawer.setOnClickListener(this);
        btn_tmpSearch.setOnClickListener(this);
        /***********************Drawer내 버튼 ******************/
        btn_leaveRoom.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        btn_block.setOnClickListener(this);
        /****************Swipe로 Drawer Open 방지**************/
        mDrawer.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
        /*******************************/
        mChatterAdapter = new ChatterAdapter(this, new ArrayList<ChatterItem>(), this);
        mChatAdapter = new ChatAdapter(this, new ArrayList<ChatItem>(), this);
        // TODO: 2018-10-30  Have to be changed to below in near future
        //mUser= User.getInstance();
        mUser = new ChatUser();
        mUser.setId(Account.getInstance().getUserId());
        mUser.setName(Account.getInstance().getUserName());
        //mNotificationRecycler.setLayoutManager(new LinearLayoutManager(this));
        mChatRecycler.setLayoutManager(new LinearLayoutManager(this));
        mChatterRecycler.setLayoutManager(new LinearLayoutManager(this));
        mDrawer.addDrawerListener(this);
        btn_Send.setOnClickListener(this);

        /*******************************************************************/
        mChatterRecycler.setAdapter(mChatterAdapter);
        //DB로부터 채팅 기록을 불러옴
        bringNotification(this.curRoomId);
        dbHelper.getChatHistory(mChatAdapter, curRoomId);
        lastSearchIndex = mChatAdapter.getItemCount()-1;
        /***********************************/
        mChatRecycler.setAdapter(mChatAdapter);
        mRoomName.setText(curRoomName);

        //Test///////////////////////////////////////////////////////////
        if(curRoomStauts == Party.Status.Blocked){
            Toast.makeText(this, "동결 상태이므로 채팅을 할 수 없습니다", Toast.LENGTH_SHORT).show();
            btn_Send.setClickable(false);
            mMessage.setFreezesText(true);
        }
        //BIND SERVICE

    }

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            Log.i("SERVICECONNECTION", "ONSERVICECONNECTED");
            ChatService.LocalBinder mb = (ChatService.LocalBinder) service;
            myService = mb.getService(); // 서비스가 제공하는 메소드 호출하여
            myService.setCurRoomId(curRoomId);
            myService.setmChatListener(mListener);
            // 서비스쪽 객체를 전달받을수 있슴
            isService = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false;
            if(myService!=null)
            myService.setCurRoomId(-1);
            Log.i("SERVICECONNECTION","ONSERVICEDISCONNECTED");
            Toast.makeText(getApplicationContext(),
                    "서비스 연결 해제",
                    Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onDestroy() {
        Log.i("CHATTING", "ONDESTROY");
        super.onDestroy();
        if(isService){
//            unbindService(conn); // 서비스 종료
            isService = false;
        }
    }


    @Override
    protected void onResume() {
        if(!isService) {
            if(myService==null){
            Intent serviceIntent = new Intent(ChattingActivity.this, ChatService.class);
            bindService(serviceIntent, conn, BIND_AUTO_CREATE);
            }
        }
        getChatRoomUserList(curRoomId);
        if(myService!=null)
        myService.setCurRoomId(curRoomId);
        super.onResume();
    }

    public ArrayList<ChatterItem> getChatRoomUserList(int roomId) {
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        Call<ArrayList<ChatterItem>> getChatRoomUserList = networkService.getChatRoomUserList(roomId);
        getChatRoomUserList.enqueue(new Callback<ArrayList<ChatterItem>>() {
            @Override
            public void onResponse(Response<ArrayList<ChatterItem>> response, Retrofit retrofit) {
                chatterList = response.body();
                for(int i = 0; i < chatterList.size(); i++){
                    chatterList.get(i).setUser(new ChatUser(chatterList.get(i).getUserName(),chatterList.get(i).getUserId()));
                }
                mChatterAdapter.setmChatterList(chatterList);
                mChatterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("FAILED", "CHATTERLIST");

            }
        });
        return chatterList;
    }


    @Override
    protected void onPause() {
        if(myService!=null)
            myService.setCurRoomId(-1);
        super.onPause();
    }

    /*************Drawer에 대한 리스너 정의*******************/
    @Override
    public void onDrawerSlide(@NonNull View view, float v) {
        hideKeyBoard();
    }
    @Override
    public void onDrawerOpened(@NonNull View view) {
        mDrawer.setDrawerLockMode(LOCK_MODE_UNLOCKED);
        hideKeyBoard();
    }
    @Override
    public void onDrawerClosed(@NonNull View view) {
        mDrawer.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
    }
    @Override
    public void onDrawerStateChanged(int i) {
    }

    private void setBtn_map(){
        Intent intent = new Intent(this, ChattingMapActivity.class);
        intent.putExtra(ChatDBCtrct.roomId, curRoomId);
        startActivity(intent);
    }


    //뒤로가기 버튼 눌렀을때의 event 처리
    @Override
    public void onBackPressed() {
        if(myService!=null)
        myService.setCurRoomId(-1);
        super.onBackPressed();
    }

    /*************************************************************/
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.imgBtn_Chatting_SendMessage:{

                ChatItem chat = new ChatItem(mUser, mMessage.getText().toString(), "10:30", curRoomId);
                //sendMsg(chat);
                if(myService!=null)
                myService.sendMsg(chat);
                clearKeyBoard();
                break;
            }
            case R.id.imgbtn_toolbarChat_back:{
                onBackPressed();
                break;
            }
            case R.id.imgbtn_toolbarChat_search:{
                mLayoutToolbar_origin.setVisibility(View.GONE);
                mLayoutToolbar_clicksearch.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.imgbtn_toolbarChat_tmpsearch:{
                String str = mSearchChat.getText().toString();
                search(str);
                break;
            }
            case R.id.imgbtn_toolbarChat_searchcancel:{
                cancelSearch();
                break;
            }
            case R.id.imgbtn_toolbarChat_drawer:{
                if(mDrawer.isDrawerOpen(Gravity.RIGHT))
                    mDrawer.closeDrawer(Gravity.RIGHT);
                else
                    mDrawer.openDrawer(Gravity.RIGHT);
                break;
            }
            case R.id.text_Chatting_mainNotification:{
                break;
            }
            case R.id.btn_searchchatDialog_ok:{
                searchChatDialog.dismiss();
                break;
            }
            case R.id.imgbtn_dialog_cancel:{
                profileDialog.dismiss();
                break;
            }
            case R.id.imgbtn_chatting_leaveRoom:{
                showLeaveRoomDialog(curRoomId);
                break;
            }
            case R.id.imgbtn_chatting_map:{
                setBtn_map();
                break;
            }
            case R.id.imgbtn_chatting_blockRoom:{
                showCloseRoomDialog(curRoomId);
                break;
            }
        }
    }
//Edit Text Data Change Listener
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void afterTextChanged(Editable editable) {
        lastSearchIndex = mChatAdapter.getItemCount()-1;}
        /******************************************/
    //RecyclerView의 마지막 Item을 본다

    private void focusOnLastMessage(){
        mChatRecycler.scrollToPosition(mChatAdapter.getItemCount()-1);
    }

    public void search(String str){
        for(int pos = lastSearchIndex; pos>=0; pos--){
            if(mChatAdapter.getItem(pos).getMessage().contains(str))
            {
                lastSearchIndex = pos-1;
                mChatRecycler.smoothScrollToPosition(pos);
                return;
            }
        }
        showSearchChatDialog();
    }

    public void cancelSearch()
    {
        mSearchChat.setText("");
        mLayoutToolbar_origin.setVisibility(View.VISIBLE);
        mLayoutToolbar_clicksearch.setVisibility(View.GONE);
    }


    //메세지 입력칸 초기화
    private void clearKeyBoard(){
        mMessage.setText(null);
    }
    //키보드를 내린다
    private void hideKeyBoard() {
        imm.hideSoftInputFromWindow(mMessage.getWindowToken(), 0); //Drawer가 열리면 키보드를 내림
    }
    // TODO: 2018-10-05

    /***********************************공지사항 PART***********************************************/
    //공지사항으로 설정한다 //서버와 연동
    private void bringNotification(int roomId) {
        ChatItem item = dbHelper.getNotificationFromDB(roomId);
        if (item != null) {
            setNotification(item);
        }
    }
    private void setNotification(ChatItem item)
    {
        String notification = item.getMessage();
        mMainNotification.setText(notification);
        mDrawerNotification.setText(notification);
        notifiactionLayout.setVisibility(View.VISIBLE);
        mMainNotification.setVisibility(View.VISIBLE);
    }
    //공지사항을 가린다
    private void hideNotification()
    {
        mMainNotification.setVisibility(View.GONE);
    }
    //공지 추가 //나중에 서버와 연동
    private void requestNotification(String notification)
    {
        /*
       1. Request Updating Notification
       2. Get Reply from the Server
       3. If yes occure func "setNotification
         */
        //setNotification(notification);
    }
    /***********************************************************/
    /*****************채팅 참가자 Part*************************/
    //채팅 참가자 추가 //나중에 서버와 연동
    private void addChatter(ChatterItem chatter)
    {
        mChatterAdapter.addChatter(chatter);
    }
    /******************************************/
    // TODO: 2018-10-05 // 서버와 연동 필요함


    /**************************************************************/
    /*********Interface_ChattingClickListener 정의 **********************/
    @Override
    public void onImageClick(View view, int position) {
        showProfile(mChatAdapter.getItem(position).getUser());
    }
    @Override
    public void onMessageLongClick(View view, int position) {
        showNotifyChatDialog(mChatAdapter.getItem(position));
    }
    @Override
    public void onChatterClick(View view, int position) {
        showProfile(mChatterAdapter.getItem(position).getUser());
    }

    /************************************************************************/

    //대화 내용 공지하기 관련 부분
    private void setNotifyChatDialogWindow(){
        notifyChatDialog = new Dialog(this);
        notifyChatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    private void showNotifyChatDialog(ChatItem item){
        notifyChatDialog.setContentView(R.layout.chat_notifychat_dialog);
        btn_notifychat_yes = notifyChatDialog.findViewById(R.id.btn_notifychat_yes);
        btn_notifychat_no = notifyChatDialog.findViewById(R.id.btn_notifychat_no);
        btn_notifychat_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyChatDialog.dismiss();
            }
        });
        btn_notifychat_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myService!=null)
                myService.sendNotification(item);
                notifyChatDialog.dismiss();
            }
        });
        notifyChatDialog.show();
    }

    //대화 내용 검색 찾기 관련 부분
    private void setSearchChatDialogWindow(){
        searchChatDialog = new Dialog(this);
        searchChatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    private void showSearchChatDialog(){
        searchChatDialog.setContentView(R.layout.chat_searchresult_dialog);
        btn_searchchatDialog_ok = (Button)searchChatDialog.findViewById(R.id.btn_searchchatDialog_ok);
        btn_searchchatDialog_ok.setOnClickListener(this);
        searchChatDialog.show();
    }

    //방 나가기 확인 다이얼로그
    private void setLeaveRoomWindow(){
        leaveRoomDialog = new Dialog(this);
        leaveRoomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    private void showLeaveRoomDialog(int roomId)
    {
        leaveRoomDialog.setContentView(R.layout.chatroom_warning_leave);
        btn_leaveroom_yes = leaveRoomDialog.findViewById(R.id.btn_leaveroom_yes);
        btn_leaveroom_no = leaveRoomDialog.findViewById(R.id.btn_leaveroom_no);
        btn_leaveroom_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myService!=null){
                    myService.leaveRoom(roomId);
                }
                postLeaveRoom(roomId);
            }
        });
        btn_leaveroom_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveRoomDialog.dismiss();
            }
        });
        leaveRoomDialog.show();
    }
    private void setcloseRoomWindow(){
        closeRoomDialog = new Dialog(this);
        closeRoomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    private void showCloseRoomDialog(int roomId)
    {
        closeRoomDialog.setContentView(R.layout.chatroom_warning_close);
        btn_closeroom_yes = closeRoomDialog.findViewById(R.id.btn_closeroom_yes);
        btn_closeroom_no = closeRoomDialog.findViewById(R.id.btn_closeroom_no);
        btn_closeroom_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postCloseRoom(roomId);
                closeRoomDialog.dismiss();
            }
        });
        btn_closeroom_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeRoomDialog.dismiss();
            }
        });
        closeRoomDialog.show();
    }

    /**********************************Dialog*****************************************/
    //setProfileWindow() --> showProfile() -->setProfile()
    private void setProfileWindow(){
        profileDialog = new Dialog(this);
        profileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    private void showProfile(ChatUser mUser)
    {
        profileDialog.setContentView(R.layout.chat_chatuser_profile);
        btn_cancelProfile = (ImageButton) profileDialog.findViewById(R.id.imgbtn_dialog_cancel);
        btn_review = (Button) profileDialog.findViewById(R.id.btn_dialog_review);
        btn_report = (Button) profileDialog.findViewById(R.id.btn_dialog_report);


        imgView_profileImg = (ImageView) profileDialog.findViewById(R.id.img_dialog_image);
        text_name = (TextView)profileDialog.findViewById(R.id.text_dialog_name);
        mReviewListView = (ListView) profileDialog.findViewById(R.id.listView_review);
        mReviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            }
        });
        ratingBar_rate = (RatingBar) profileDialog.findViewById(R.id.rating_dialog_rate);

        btn_cancelProfile.setOnClickListener(this);
        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNotSelfClicked(mUser.getId())) {
                    Intent reviewIntent = new Intent(ChattingActivity.this, WriteReviewActivity.class);
                    reviewIntent.putExtra("targetUserId", mUser.getId());
                    reviewIntent.putExtra("partyId", curRoomId);
                    ChattingActivity.this.startActivity(reviewIntent);
                }else{
                    Toast.makeText(ChattingActivity.this, "자기 자신한테는 리뷰를 남길 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNotSelfClicked(mUser.getId())) {
                Intent reportIntent = new Intent(ChattingActivity.this, ReportActivity.class);
                reportIntent.putExtra(ChatDBCtrct.roomId, curRoomId);
                reportIntent.putExtra(ChatDBCtrct.userID, mUser.getId());
                ChattingActivity.this.startActivity(reportIntent);
                }else{
                    Toast.makeText(ChattingActivity.this, "자기 자신한테는 신고를 남길 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setProfile(mUser);
        profileDialog.show();
    }
    private boolean isNotSelfClicked(int userId){
        if(Account.getInstance().getUserId() == userId){
            return false;
        }
        else {
            return true;
        }
    }
    //Profile창에서 User의 이미지, 이름, 평점, 리뷰를 가져옴
    private void setProfile(ChatUser mUser) {
        setProfileName(mUser);
        setProfileRate(mUser);
        setProfileReview(mUser);
        setProfileImage(mUser);
    }
    //유저의 이미지를 가져와 프로필 창에 저장
    private void setProfileImage(ChatUser mUser){

    }
    //유저의 이름을 가져와 프로필 창에 저장
    private void setProfileName(ChatUser mUser){
        text_name.setText(mUser.getName());
    }
    //유저의 평점을 가져와 프로필 창에 저장
    private void setProfileRate(ChatUser mUser){
        //Server에 "Get" User's Rate // User's ID를 보냄 --> rate에 저장
        float rate = 4;
        ratingBar_rate.setRating(rate);
        //QUERY FOR GETTING USER RATE
//        getUserRate(mUser.getId());
    }
    //유저의 리뷰를 가져와 프로필 창에 저장
    private void setProfileReview(ChatUser user){

        mReviewAdapter = new ReviewAdapter();
        mReviewListView.setAdapter(mReviewAdapter);
//        QUERY FOR GETTING REVIEW LIST
        getUserReviewList(user.getId());
    }


    public int postLeaveRoom(int roomId){
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        Call<Integer> leaveRoom = networkService.postLeaveRoom(Account.getInstance().getUserId(), roomId);
        leaveRoom.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Response<Integer> response, Retrofit retrofit) {
                dbHelper.deleteRoom(roomId);
                if(myService!=null)
                    myService.setCurRoomId(-1);
                onBackPressed();
            }
            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(myService, "방 나가기 실패", Toast.LENGTH_SHORT).show();
            }
        });
        return 1;
    }
    public void postCloseRoom(int partyId){
        DB.getInstance().postCloseParty(partyId);
    }



    private void getUserRate(int userId){
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        Call<Float> getUserRate = networkService.getUserRate(userId);
        getUserRate.enqueue(new Callback<Float>() {
            @Override
            public void onResponse(Response<Float> response, Retrofit retrofit) {
                //TODO jinsun, 이 방식이 될까?
                Float rating = response.body();
                ratingBar_rate.setRating(rating.floatValue());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    @Override
    public void receiveChat(ChatItem item) {
        mChatAdapter.addChat(item);
        mChatAdapter.notifyDataSetChanged();
        focusOnLastMessage();
    }


    @Override
    public void receiveNotification(ChatItem item) {
        setNotification(item);
    }
    /**********************************************************************************************************************/
    //Find Chat

    private void  getUserReviewList(int userId){
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
}

