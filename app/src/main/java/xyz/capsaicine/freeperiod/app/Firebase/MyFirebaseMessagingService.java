package xyz.capsaicine.freeperiod.app.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

import xyz.capsaicine.freeperiod.Model.ChatRoomInDatabase;
import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.ChatActivity;
import xyz.capsaicine.freeperiod.activities.chat.ChatDBCtrct;
import xyz.capsaicine.freeperiod.activities.chat.ChatService;
import xyz.capsaicine.freeperiod.activities.chat.ChatUser;
import xyz.capsaicine.freeperiod.activities.chat.DBHelper;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatRoomItem;
import xyz.capsaicine.freeperiod.activities.login.LoginActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final DBHelper dbHelper = new DBHelper(this, ChatDBCtrct.chatDB, null, 1);

    private final String notificationType = "type";
    private final String notificationTitle = "title";
    private final String notificationContent = "data";

    public static final String broadcastContent = "content";
    public static final String endterChatRoom = "enterChatRoom";
    public static final String newMessage = "newMessage";
    public static final String rejectPartyRequest = "rejectPartyRequest";
    public static final String joinRequest = "joinRequest";
    public static final String reportReview = "reportUser";

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    public MyFirebaseMessagingService() {
        super();
    }

    // 메시지 수신
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived");

        setNewlyUpdatedFalse();
        Map<String, String> data = remoteMessage.getData();
        String type = data.get(notificationType);
        String title = data.get(notificationTitle);
        String message = data.get(notificationContent);
        if(type.equals(endterChatRoom)){
            Intent mIntent = new Intent(endterChatRoom);
            mIntent.putExtra(broadcastContent,message);
            sendBroadcast(mIntent);
//            ChatRoomItem room = getChatRoomItem(message);
            ChatRoomInDatabase room = getChatRoomInDatabaseItem(message);
            dbHelper.insertChatRoom(room);
            sendNotification(title, room.getRoomName());
        }
        else if(type.equals(joinRequest)){
            sendNotification(title, "");
        }
        //다른처리 추가
        else if(type.equals(newMessage)){
//            Intent intent = new Intent(this, ChatService.class);
//            startService(intent);
            Intent mIntent = new Intent(newMessage);
            mIntent.putExtra(broadcastContent,message);
            sendBroadcast(mIntent);
            ChatItem chat = getChatItem(message);
            dbHelper.insertChat(chat);
            sendNotification(title, chat.getMessage());
        }
        else if(type.equals(rejectPartyRequest)){
            String partyName = getRejectedPartyName(message);
            sendNotification(title, partyName);
        }
        else if(type.equals(reportReview)){
            sendNotification(title, "");
        }

    }

    private void setNewlyUpdatedFalse(){
        SharedPreferences auto = getSharedPreferences("auto", 0);
        SharedPreferences.Editor editor = auto.edit();
        editor.putBoolean(LoginActivity.CHATROOMRESTED, false);
        editor.commit();
    }

    private void sendNotification(String title, String content) {
        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_info))
                .setSmallIcon(R.drawable.base) //ICON
                .setContentTitle(title) //제목
                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
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
    private ChatRoomInDatabase getChatRoomInDatabaseItem(String message){
        JsonParser jsonParsers = new JsonParser();
        JsonObject jsonMessage = (JsonObject) jsonParsers.parse(message);
        int roomId = jsonMessage.get(ChatDBCtrct.roomId).getAsInt();
        String roomName = jsonMessage.get(ChatDBCtrct.roomName).getAsString();
        int capacityCurrent = jsonMessage.get(ChatDBCtrct.capacityCurrent).getAsInt();
        int capacityMax = jsonMessage.get(ChatDBCtrct.capacityMax).getAsInt();
        int partyLeader = jsonMessage.get(ChatDBCtrct.partyLeader).getAsInt();


        ChatRoomInDatabase item = new ChatRoomInDatabase();
        item.setRoomId(roomId);
        item.setPartyCover(new byte[0]);
        item.setStatus("Recruiting");
        item.setRoomName(roomName);
        item.setCapacityCurrent(capacityCurrent);
        item.setCapacityMax(capacityMax);
        item.setPartyLeader(partyLeader);
        return item;
    }
    private String getRejectedPartyName(String message){
        JsonParser jsonParsers = new JsonParser();
        JsonObject jsonMessage = (JsonObject) jsonParsers.parse(message);
        String partyName = jsonMessage.get(ChatDBCtrct.roomName).getAsString();
        return partyName;
    }
    private ChatItem getChatItem(String message){
        JsonParser jsonParsers = new JsonParser();
        JsonObject jsonMessage = (JsonObject) jsonParsers.parse(message);
        int userId = jsonMessage.get(ChatDBCtrct.userID).getAsInt();
        String userName = jsonMessage.get(ChatDBCtrct.userName).getAsString();
        String msg = jsonMessage.get(ChatDBCtrct.message).getAsString();
        String createdAt = jsonMessage.get(ChatDBCtrct.createdAt).getAsString();
        int roomId = jsonMessage.get(ChatDBCtrct.roomId).getAsInt();


        ChatItem chat = new ChatItem(new ChatUser(userName, userId), msg, createdAt, roomId);
        return chat;
    }

}
