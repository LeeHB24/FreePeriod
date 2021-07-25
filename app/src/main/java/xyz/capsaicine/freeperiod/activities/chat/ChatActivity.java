package xyz.capsaicine.freeperiod.activities.chat;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import xyz.capsaicine.freeperiod.Model.ChatRoomInDatabase;
import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.PivotActivity;
import xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter.ChatRoomAdapter;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatRoomItem;
import xyz.capsaicine.freeperiod.activities.login.LoginActivity;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.App;
import xyz.capsaicine.freeperiod.app.Firebase.MyFirebaseInstanceIdService;
import xyz.capsaicine.freeperiod.app.Firebase.MyFirebaseMessagingService;
import xyz.capsaicine.freeperiod.app.NetworkService;
import xyz.capsaicine.freeperiod.app.Utility;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;


public class ChatActivity extends PivotActivity implements Interface_ChatRoomClickListener, Interface_updateChatRoomScreen{


    private Interface_updateChatRoomScreen mListener;
    ChatService myService;
    boolean isService = false; // 서비스 중인 확인용
    boolean newlyUpdated = false;


    private BroadcastReceiver mReceiver;
    private static final String TAG = "MyFirebaseIIDService";
    private final String TOKEN = "token";


    DBHelper dbHelper;
    private BottomNavigationView navBottom;
    private SearchView mSearchView;
    private RecyclerView mChatRoomRecycler;
    private ChatRoomAdapter mChatRoomAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mListener = this;
        dbHelper = new DBHelper(this, ChatDBCtrct.chatDB, null, 1);
        navBottom = findViewById(R.id.nav_bottom);
        setPivotActivity(ChatActivity.this, navBottom);
        newlyUpdated = getNewlyUpdated();
        //Your codes here
         /************************************************************************/
        //초기화

        mChatRoomRecycler = (RecyclerView) findViewById(R.id.recyclerView_ChatRoomList);
        mChatRoomRecycler.setLayoutManager(new LinearLayoutManager(this));
        mChatRoomRecycler.addItemDecoration(new DividerItemDecoration(this, 1));
        mChatRoomAdapter = new ChatRoomAdapter(this, new ArrayList<ChatRoomItem>(), this);
        mChatRoomRecycler.setAdapter(mChatRoomAdapter);
//        getChatRoomList(Account.getInstance().getUserId(), this, this);



        //TODO 이 작업을 후에 LOGIN으로 옮겨야함
//        MyFirebaseInstanceIdService fb = new MyFirebaseInstanceIdService();
//        //SENDING TO SERVER THE USER TOKEN
//        fb.sendRegistrationToServer(Account.getInstance().getUserId(), getSharedPreferenceToken());
//        Log.i("TOKEN", getSharedPreferenceToken());
//        Intent serviceIntent = new Intent(ChatActivity.this, ChatService.class);
//        startService(serviceIntent);

    }
    private boolean getNewlyUpdated(){
        SharedPreferences auto = getSharedPreferences("auto", 0);
        return auto.getBoolean(LoginActivity.CHATROOMRESTED, false);
    }
    private void setNewlyUpdated(){
        SharedPreferences auto = getSharedPreferences("auto", 0);
        SharedPreferences.Editor editor = auto.edit();
        editor.putBoolean(LoginActivity.CHATROOMRESTED, true);
        editor.commit();
    }



    public String getSharedPreferenceToken(){
        SharedPreferences pref = getSharedPreferences(TAG, MODE_PRIVATE);
        return pref.getString(TOKEN, "");
    }


    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            Log.i("SERVICECONNECTION", "ONSERVICECONNECTED");
            ChatService.LocalBinder mb = (ChatService.LocalBinder) service;
            myService = mb.getService(); // 서비스가 제공하는 메소드 호출하여
            myService.setmRoomListener((mListener));
//            mb.getService().setCurRoomId(curRoomId);
            // 서비스쪽 객체를 전달받을수 있슴
            isService = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false;
            if(myService!=null)
            myService.setCurRoomId(-1);
        }
    };



    private void getRoomList(){
        ArrayList<ChatRoomItem> list;
        mChatRoomAdapter.clearChatRoomList();
        list = dbHelper.getRoomListTable();
        for(int i =0; i<list.size(); i++){
            mChatRoomAdapter.addChatRoom(list.get(i));
        }
        mChatRoomAdapter.notifyDataSetChanged();
    }


    //메세지가 온 최근 시간으로 업데이트 해줌
    //메세지가 올 때마다 채팅 방을 ListView의 상위로 올림
    public void moveTop(int position)
    {

    }


    @Override
    public void onRoomClick(View view, int pos) {
        Intent intent = new Intent(this, ChattingActivity.class);
        ChatRoomItem item = mChatRoomAdapter.getChatRoom(pos);
        intent.putExtra(ChatDBCtrct.roomId,item.getRoomId()); //후에 클릭한 채팅방의 ID를 보냄
//        intent.putExtra(ChatDBCtrct.roomName, item.getRoomName());
//        intent.putExtra(ChatDBCtrct.partyLeader, item.getPartyLeader());
//        Log.i("ROOMSTATUS", item.getStatus());
//        intent.putExtra(ChatDBCtrct.roomStatus, item.getStatus());
        startActivity(intent);
    }

    public void getChatRoomList(int userId, Context context, Interface_ChatRoomClickListener listener){
        NetworkService networkService = App.retrofit.create(NetworkService.class);
        Call<ArrayList<ChatRoomInDatabase>> getChatRoomListCall = networkService.getChatroomList(userId);
        getChatRoomListCall.enqueue(new Callback<ArrayList<ChatRoomInDatabase>>() {
            @Override
            public void onResponse(Response<ArrayList<ChatRoomInDatabase>> response, Retrofit retrofit) {
                ArrayList<ChatRoomInDatabase> chatRoomList = response.body();
                for(int cnt = 0; cnt < chatRoomList.size(); cnt++) {
                    ChatRoomInDatabase item = chatRoomList.get(cnt);
                    boolean newRoom = dbHelper.insertChatRoom(item);
                    if(newRoom && myService!=null){
                        myService.connectRoom(item.getRoomId());
                    }
                }
                getRoomList();
                setNewlyUpdated();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(context, "재연결 해주세요", Toast.LENGTH_SHORT).show();
                getRoomList();
            }
        });
    }
    private void registerReceiver(){
        if(mReceiver!=null) return;

        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(MyFirebaseMessagingService.endterChatRoom);
        intentfilter.addAction(ChatService.receiveMsg);

        this.mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String receivedMessageFromFireBase = intent.getStringExtra(MyFirebaseMessagingService.broadcastContent);
                //PUSH 로 온 메세지 처리
                if(intent.getAction().equals(MyFirebaseMessagingService.endterChatRoom)){
                    ChatRoomItem item = getChatRoomItem(receivedMessageFromFireBase);
                    mChatRoomAdapter.updateScreen(item);
                }
                mChatRoomAdapter.notifyDataSetChanged();
            }
        };
        this.registerReceiver(this.mReceiver, intentfilter);
    }
    private void unregisterReceiver() {
        if(mReceiver != null){
            this.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO jinsun, 더 좋은 방법으로 개선시켜야함
        Intent serviceIntent = new Intent(ChatActivity.this, ChatService.class);
        bindService(serviceIntent, conn, BIND_AUTO_CREATE);
        if(!getNewlyUpdated()){
            getChatRoomList(Account.getInstance().getUserId(), this, this);
        }else{
            getRoomList();
            updateCurrentRoomCapacity();
            updateCurrentRoomStatus();
        }
//        mChatRoomAdapter.notifyDataSetChanged();
        registerReceiver();
    }

    //TODO
    private void updateCurrentRoomCapacity(){

    }
    private void updateCurrentRoomStatus(){

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
        if(myService!=null)
            myService.setmRoomListener(null);
    }

    @Override
    protected void onDestroy() {
        if(myService!=null)
        myService.setmRoomListener(null);
        super.onDestroy();
    }
//
    private ChatRoomItem getChatRoomItem(String message){
        JsonParser jsonParsers = new JsonParser();
        JsonObject jsonMessage = (JsonObject) jsonParsers.parse(message);
        int roomId = jsonMessage.get(ChatDBCtrct.roomId).getAsInt();
        String roomName = jsonMessage.get(ChatDBCtrct.roomName).getAsString();
        int capacityCurrent = jsonMessage.get(ChatDBCtrct.capacityCurrent).getAsInt();
        int capacityMax = jsonMessage.get(ChatDBCtrct.capacityMax).getAsInt();

        ChatRoomItem item = new ChatRoomItem();
        item.setRoomId(roomId);
        item.setRoomName(roomName);
        item.setCapacityCurrent(capacityCurrent);
        item.setCapacityMax(capacityMax);
        return item;
    }


    @Override
    public void receiveChat(ChatItem item) {
        mChatRoomAdapter.updateScreen(item.getRoomId(), item.getMessage(), item.getCreatedAt());
        mChatRoomAdapter.notifyDataSetChanged();
    }





}
