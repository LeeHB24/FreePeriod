package xyz.capsaicine.freeperiod.activities.chat;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatRoomItem;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.Utility;

public class ServiceThread extends Thread implements Interface_Chat_SocketConnection{
    private final static String uri = "http://49.236.132.218";
    private final static int port = 8000;
    private Socket socket;
    DBHelper dbHelper;
    Handler handler;

    public DBHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    boolean isRun = true;

    public ServiceThread(Handler handler){
        this.handler = handler;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
            Disconnect();
        }
    }
    @Override
    public void run(){
        if(isRun) {
            synchronized (this){
//                Log.i("SERVICETHREAD", "RUN");
                ConnectToServer(uri, port);
                receiveMsg();
            }

        }
    }

    @Override
    public void ConnectToServer(String Uri, int port) {
        try {
            //socket.disconnect();
            Log.i("서버연결", "ok");
//            socket = IO.socket("http://49.236.132.218:8000/");
            Log.i("SERVICETHREAD", "CONNECT TO SERVER");
            socket = IO.socket(Uri + ":" +port+"/");
            socket.connect();
            //isSocketDisconnected = false;
            setConnectRoom();
        } catch (URISyntaxException e) {
            Log.i("SERVICETHREAD", "SERVER NOT CONNECTED");
            e.printStackTrace();
        }
    }

    private void setConnectRoom(){
        setSocketID();
        ArrayList<ChatRoomItem> roomList = dbHelper.getRoomListTable();
        for(int idx = 0; idx < roomList.size(); idx++)
            connectRoom(roomList.get(idx).getRoomId());
    }


    @Override
    public void Disconnect() {
        Log.i("SOCKET", "DISCONNECT");
        socket.disconnect();
//        isSocketDisconnected = true;
    }

    @Override
    public void sendNotification(ChatItem chat) {
        JsonObject preJsonObject = new JsonObject();
        preJsonObject.addProperty(ChatDBCtrct.roomId, chat.getRoomId());
        preJsonObject.addProperty(ChatDBCtrct.userID,chat.getUser().getId());
        preJsonObject.addProperty(ChatDBCtrct.userName, chat.getUser().getName());
        preJsonObject.addProperty(ChatDBCtrct.message,chat.getMessage());
        preJsonObject.addProperty(ChatDBCtrct.createdAt,chat.getCreatedAt());

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(preJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(ChatService.sendNotification, jsonObject);
    }

    @Override
    public void connectRoom(int roomId) {
        JsonObject preJsonObject = new JsonObject();
        preJsonObject.addProperty(ChatDBCtrct.roomId, roomId);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(preJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(ChatService.connectRoom, jsonObject);
    }

    public void setSocketID(){
        JsonObject preJsonObject = new JsonObject();
        preJsonObject.addProperty(ChatDBCtrct.userID, Account.getInstance().getUserId());
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(preJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(ChatService.setSocketID, jsonObject);
    }

    @Override
    public void leaveRoom(int partyId) {
        JsonObject preJsonObject = new JsonObject();
        preJsonObject.addProperty(ChatDBCtrct.roomId, partyId);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(preJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(ChatService.leaveRoom, jsonObject);
    }

    @Override
    public void sendMsg(ChatItem chat) {
        JsonObject preJsonObject = new JsonObject();
        preJsonObject.addProperty(ChatDBCtrct.roomId, chat.getRoomId());
        preJsonObject.addProperty(ChatDBCtrct.userID,chat.getUser().getId());
        preJsonObject.addProperty(ChatDBCtrct.userName, chat.getUser().getName());
        preJsonObject.addProperty(ChatDBCtrct.message,chat.getMessage());
        preJsonObject.addProperty(ChatDBCtrct.createdAt,chat.getCreatedAt());

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(preJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(ChatService.sendMsg, jsonObject);
    }

    @Override
    public void receiveMsg(){

        socket.on(ChatService.receiveMsg, (Object... objects) -> {
            JsonParser jsonParsers = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParsers.parse(objects[0] + "");
            Message msg = handler.obtainMessage();
            Bundle serviceBundle = getServiceBundle(ChatService.receiveMsg, jsonObject);
            msg.setData(serviceBundle);
            handler.sendMessage(msg);
        });

        socket.on(ChatService.receivceNotification, (Object... objects) ->{
            JsonParser jsonParsers = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParsers.parse(objects[0] + "");
            Message msg = handler.obtainMessage();
            Bundle serviceBundle = getServiceBundle(ChatService.receivceNotification, jsonObject);
            msg.setData(serviceBundle);
            handler.sendMessage(msg);
        });

    }

    Bundle getServiceBundle(String recType, JsonObject jsonObject){
        Date currentTime = Calendar.getInstance().getTime();
        Bundle serviceBundle = new Bundle();
        int roomId = jsonObject.get(ChatDBCtrct.roomId).getAsInt();
        int userID = jsonObject.get(ChatDBCtrct.userID).getAsInt();
        String userName = jsonObject.get(ChatDBCtrct.userName).getAsString();
        String message = jsonObject.get(ChatDBCtrct.message).getAsString();
//        String createdAt = currentTime.toString();
        String createdAt = Utility.getTimeString(currentTime);
//
        serviceBundle.putString(ChatService.recType, recType);
        serviceBundle.putInt(ChatDBCtrct.roomId, roomId);
        serviceBundle.putInt(ChatDBCtrct.userID, userID);
        serviceBundle.putString(ChatDBCtrct.userName, userName);
        serviceBundle.putString(ChatDBCtrct.message, message);
        serviceBundle.putString(ChatDBCtrct.createdAt, createdAt);
        return serviceBundle;
    }
}