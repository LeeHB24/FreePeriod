package xyz.capsaicine.freeperiod.activities.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.socket.client.IO;
import io.socket.client.Socket;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatRoomItem;
import xyz.capsaicine.freeperiod.activities.login.LoginActivity;
import xyz.capsaicine.freeperiod.app.Account;

import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;
import xyz.capsaicine.freeperiod.app.Firebase.MyFirebaseMessagingService;

public class ChatService extends Service{
    DBHelper dbHelper = new DBHelper(this, ChatDBCtrct.chatDB, null, 1);
    Interface_updateChatScreen mChatListener = null;
    Interface_updateChatRoomScreen mRoomListener;
    private BroadcastReceiver mReceiver;

    public Interface_updateChatRoomScreen getmRoomListener() {
        return mRoomListener;
    }

    public void setmRoomListener(Interface_updateChatRoomScreen mRoomListener) {
        this.mRoomListener = mRoomListener;
    }

    public Interface_updateChatScreen getmChatListener() {
        return mChatListener;
    }

    public void setmChatListener(Interface_updateChatScreen mChatListener) {
        this.mChatListener = mChatListener;
    }

    private final IBinder mBinder = new LocalBinder();
    class LocalBinder extends Binder {
        ChatService getService() {
            return ChatService.this;
        }
    }

    private int curRoomId = -1;

    public int getCurRoomId() {
        return curRoomId;
    }

    public void setCurRoomId(int curRoomId) {
        Log.i("SETROOMID", curRoomId+"");
        this.curRoomId = curRoomId;
    }

    public static final String broadcastConent = "broadcastContent";

    public static final String recType = "type";
    //Socket.on 구분자
    public static final String receiveMsg = "receiveMsg";
    public static final String receivceNotification = "receiveNotification";
    //Socket.emit
    public static final String leaveRoom = "leaveRoom";
    public static final String sendNotification = "notification";
    public static final String sendMsg = "message";
    public static final String connectRoom = "connectRoom";
    public static final String setSocketID = "socketID";
    NotificationManager Notifi_M;
    ServiceThread thread = null;
    Notification notify;

    public ServiceThread getThread() {
        return thread;
    }

    public void setThread(ServiceThread thread) {
        this.thread = thread;
    }

    public ChatService() {
    }

    @Override
    public void onCreate() {
        Log.i("SERVICEE", "ONCREATE");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        registerReceiver();
        Log.i("SERVICEE", "ONSTARTCOMMAND");
        autoLogin();
        //기존의 연결을 끊고 재 연결 -- 소켓의 중복 연결 방지
        if(thread!= null){
            thread.stopForever();
            thread.interrupt();
            thread = null;
        }
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.setDbHelper(dbHelper);
        thread.start();
        return START_NOT_STICKY; //START_STICKY 로 바꿀시 한동안, 어플 종료 후 한동안 소켓 유지
    }

    private void autoLogin(){
        SharedPreferences auto = getSharedPreferences("auto", 0);
        boolean isAutoLogin = auto.getBoolean(LoginActivity.ISAUTOLOGIN, false);
        if(isAutoLogin){
            Account account = Account.getInstance();
            account.setUserId(auto.getInt(LoginActivity.ID, -1));
            account.setUserEmail(auto.getString(LoginActivity.EMAIL, null));
            account.setUserPassword(auto.getString(LoginActivity.PASSWORD, null));
            account.setUserSchool(auto.getString(LoginActivity.SCHOOL, null));
            account.setUserName( auto.getString(LoginActivity.NAME, null));
        }
        else{
            return;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("SERVICEE", "ONTASKREMOVED");
        onDestroy();
        curRoomId = -1;
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy(){
        unregisterReceiver();
        Log.i("SERVICEE","DESTROY");
        curRoomId = -1;
        //thread.Disconnect();
        if(thread!=null) {
            thread.stopForever();
            thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("SERVICEE", "ONUNBIND");
        setCurRoomId(-1);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("SERVICEE", "ONBIND");
        return mBinder;
    }

    public void connectRoom(int roomId){
        thread.connectRoom(roomId);
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            Bundle resultData = msg.getData();
            String type = resultData.getString(ChatService.recType);
            ChatItem item = getChatItem(resultData);
            //Save to DB
            if(type.equals(ChatService.receiveMsg)) {
                dbHelper.insertChat(item);
                dbHelper.updateLastMessage(item);

                if(mRoomListener != null){
                    Log.i("SERVICEE", "MROOMLISTENER RECEIVECHAT");
                    notification(item);
                    mRoomListener.receiveChat(item);
                }
                if(getCurRoomId() == item.getRoomId()){
                    mChatListener.receiveChat(item);
                }
                else{
                    notification(item);
                }
            }

            else if(type.equals(ChatService.receivceNotification)){
                dbHelper.insertNotification(item);
                if(getCurRoomId() == item.getRoomId()){
                    mChatListener.receiveNotification(item);
                }

            }
        }
    };

    private void notification(ChatItem item){
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(ChatService.this, ChattingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ChatService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notify = new Notification.Builder(getApplicationContext())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_info))
                .setSmallIcon(R.drawable.base) //ICON
                .setContentTitle(item.getRoomId()+"번방")
                .setContentText(item.getUser().getName()+" : "+item.getMessage()+ " " + item.getCreatedAt())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .build();

        //소리추가
        notify.defaults = Notification.DEFAULT_SOUND;
        //알림 소리를 한번만 내도록
        notify.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        //확인하면 자동으로 알림이 제거 되도록
        notify.flags = Notification.FLAG_AUTO_CANCEL;

        Notifi_M.notify(777, notify);

    }
    private ChatItem getChatItem(Bundle resultData){
        String type = resultData.getString(ChatService.recType);
        int roomId = resultData.getInt(ChatDBCtrct.roomId);
        int userId = resultData.getInt(ChatDBCtrct.userID);
        String userName = resultData.getString(ChatDBCtrct.userName);
        String createdAt = resultData.getString(ChatDBCtrct.createdAt);
        String message = resultData.getString(ChatDBCtrct.message);
        return new ChatItem(new ChatUser(userName, userId), message, createdAt, roomId);
    }



    public void sendMsg(ChatItem item){
        thread.sendMsg(item);
    }
    public void sendNotification(ChatItem item)
    {
        thread.sendNotification(item);
    }

    public void leaveRoom(int roomId){thread.leaveRoom(roomId);}

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
                    connectRoom(item.getRoomId());
                }
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
}
