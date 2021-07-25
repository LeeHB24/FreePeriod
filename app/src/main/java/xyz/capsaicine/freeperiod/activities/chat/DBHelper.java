package xyz.capsaicine.freeperiod.activities.chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;

import xyz.capsaicine.freeperiod.Model.ChatRoomInDatabase;
import xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter.ChatAdapter;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatRoomItem;
import xyz.capsaicine.freeperiod.activities.home.HomeDBCtrct;
import xyz.capsaicine.freeperiod.activities.home.LectureColor;
import xyz.capsaicine.freeperiod.app.Utility;

import static android.content.Context.BIND_AUTO_CREATE;


public class DBHelper extends SQLiteOpenHelper {



    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }
    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        db.execSQL(ChatDBCtrct.SQL_createRoomListTable);
        Log.i("TAG", "CREATE ROOMLIST1");
        db.execSQL(ChatDBCtrct.SQL_createNotificationTable);
        db.execSQL(HomeDBCtrct.SQL_createLectureColorListTable);
        Log.i("TAG", "CREATE LECTURECOLORLIST");
    }
    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    //채팅방 생성 관련
    public boolean insertChatRoom(ChatRoomInDatabase room){
        int roomId = room.getRoomId();
        byte[] image = room.getPartyCover();
        Log.i("IMAGEBYTE", image+"");
        String roomName = room.getRoomName();
        int partyLeader = room.getPartyLeader();
        int capacityCurrent = room.getCapacityCurrent();
        int capacityMax  = room.getCapacityMax();
        String lastMessage = room.getLastMessage();
        String lastTalkTime = room.getLastTalkTime();
        String roomStatus = room.getStatus();

        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement p = db.compileStatement(ChatDBCtrct.SQL_insertRoomList(roomId, null, roomName, partyLeader, capacityCurrent, capacityMax, lastMessage, lastTalkTime, roomStatus));
        p.bindBlob(1,image);

        try{
            //채팅방이 새로 개설된거면 채팅방목록에 추가
            db.execSQL(ChatDBCtrct.SQL_createRoomTable(roomId));
            Log.i("CREATE TABLE", "CREATED TABLE "+roomId);
            p.execute();
//            db.execSQL(ChatDBCtrct.SQL_insertRoomList(roomId, image, roomName, capacityCurrent, capacityMax, lastMessage, lastTalkTime, roomStatus));
            Log.i("INSERTED IN LIST", ""+roomId);
            db.close();
            return true;
        }catch (Exception e){
            updateRoomImage(roomId, image);
            updateCapacityCurrent(roomId, capacityCurrent);
            updateRoomStatus(roomId, roomStatus);
        }
        db.close();
        return false;
    }

    //채팅방 생성 관련
    public boolean insertChatRoom(ChatRoomItem room){
        int roomId = room.getRoomId();
        byte[] image = Utility.convertDrawableToByteArray(room.getPartyCover());
        Log.i("IMAGEBYTE", image+"");
        String roomName = room.getRoomName();
        int partyLeader = room.getPartyLeader();
        int capacityCurrent = room.getCapacityCurrent();
        int capacityMax  = room.getCapacityMax();
        String lastMessage = room.getLastMessage();
        String lastTalkTime = room.getLastTalkTime();
        String roomStatus = room.getStatus();

        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement p = db.compileStatement(ChatDBCtrct.SQL_insertRoomList(roomId, null, roomName, partyLeader, capacityCurrent, capacityMax, lastMessage, lastTalkTime, roomStatus));
        p.bindBlob(1,image);

        try{
            //채팅방이 새로 개설된거면 채팅방목록에 추가
            db.execSQL(ChatDBCtrct.SQL_createRoomTable(roomId));
            Log.i("CREATE TABLE", "CREATED TABLE "+roomId);
            p.execute();
//            db.execSQL(ChatDBCtrct.SQL_insertRoomList(roomId, image, roomName, capacityCurrent, capacityMax, lastMessage, lastTalkTime, roomStatus));
            Log.i("INSERTED IN LIST", ""+roomId);
            db.close();
            return true;
        }catch (Exception e){
            updateCapacityCurrent(roomId, capacityCurrent);
            updateRoomStatus(roomId, roomStatus);
        }
        db.close();
        return false;
    }





    public void deleteRoom(int roomId){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(ChatDBCtrct.SQL_deleteRoomFromList(roomId));
        db.execSQL(ChatDBCtrct.SQL_dropRoomTable(roomId));
        db.execSQL(ChatDBCtrct.SQL_deleteRoomFromNotificationList(roomId));
        db.close();
    }
    //NOTIFICATION 관리 부분
    public void updateNotification(ChatItem chat){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(ChatDBCtrct.SQL_updateNotification(chat));
        db.close();
    }
    public void insertNotification(ChatItem chat){
        SQLiteDatabase db = getWritableDatabase();
        String userName,message,createdAt;
        int roomId, userID;
        userName = chat.getUser().getName();
        userID = chat.getUser().getId();
        message=chat.getMessage();
        createdAt = chat.getCreatedAt();
        roomId = chat.getRoomId();

        String selectQuery = "SELECT  * FROM " + ChatDBCtrct.notificationTable + " WHERE "
                +ChatDBCtrct.roomId + " = " + roomId +"";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.getCount()!=0) {
            updateNotification(chat);
        }
        else{
            String insertNotification = "INSERT INTO "+ ChatDBCtrct.notificationTable +" VALUES(null, " +roomId +",'" + userName + "'," + userID + ", '" + message + "', '" + createdAt  + "')";
            db.execSQL(insertNotification);
        }
        db.close();
        return;
    }
    //대화 기록 관리부분
    public void insertChat(ChatItem chat) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(ChatDBCtrct.SQL_insertChat(chat));
        db.close();
    }
    //유저 목록 관리부분

    public void updateLastMessage(ChatItem item){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(ChatDBCtrct.SQL_updateRoomLastMessage(item));
        db.close();
    }

    public void updateRoomImage(int roomId, byte[] image){
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement p = db.compileStatement(ChatDBCtrct.SQL_updateRoomImage(roomId));
        p.bindBlob(1, image);
        p.execute();
        db.close();
    }
    public void updateCapacityCurrent(int roomId, int capacityCurrent){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(ChatDBCtrct.SQL_updateRoomCapacityCurrent(roomId, capacityCurrent));
        db.close();
    }
    public void updateRoomStatus(int roomId, String status){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(ChatDBCtrct.SQL_updateRoomStatus(roomId, status));
        db.close();
    }



    public ChatItem getNotificationFromDB(int partyId){
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + ChatDBCtrct.notificationTable + " WHERE "
                +ChatDBCtrct.roomId + " = " + partyId +"";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.getCount()!=0) {
            ChatItem item = new ChatItem();
            c.moveToFirst();
            item.setRoomId(c.getInt(1));
            ChatUser user = new ChatUser(c.getString(2), c.getInt(3));
            item.setMessage(c.getString(4));
            item.setCreatedAt(c.getString(5));
            item.setUser(user);

            return item;
        }
        db.close();
        return null;
    }


    public void getChatHistory(ChatAdapter mChatAdapter, int roomId) {
        // 읽기가 가능하게 DB 열기
        String room = ChatDBCtrct.room+roomId;
        SQLiteDatabase db = getReadableDatabase();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String getResult = "SELECT * FROM " + room;
        Cursor cursor = db.rawQuery(getResult, null);
        // TODO: 2018-10-30  나중에는 일정 범위만 불러오도록 설정함
        while (cursor.moveToNext()) {
            ChatItem chat = new ChatItem(new ChatUser(cursor.getString(2),cursor.getInt(1)), cursor.getString(3),cursor.getString(4), roomId);
            mChatAdapter.addChat(chat);
        }
        db.close();
    }
    public void clearRoomListTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ChatDBCtrct.roomListTable, null, null);
        db.close();
    }

    public ArrayList<ChatRoomItem> getRoomListTable(){
        ArrayList<ChatRoomItem> roomList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String getResult = "SELECT * FROM " + ChatDBCtrct.roomListTable;
        Cursor cursor = db.rawQuery(getResult, null);
        // TODO: 2018-10-30  나중에는 일정 범위만 불러오도록 설정함
        while (cursor.moveToNext()) {
            ChatRoomItem room = new ChatRoomItem();
            room.setRoomId(cursor.getInt(1));
//            Bitmap bitmap = BitmapFactory.decodeByteArray(cursor.getString(2).getBytes() , 0, cursor.getString(2).getBytes() .length);
//
            room.setPartyCover(Utility.convertByteArrayToDrawable(cursor.getBlob(2)));
            room.setRoomName(cursor.getString(3));
            room.setPartyLeader(cursor.getInt(4));
            room.setCapacityCurrent(cursor.getInt(5));
            room.setCapacityMax(cursor.getInt(6));
            room.setLastMessage(cursor.getString(7));
            room.setLastTalkTime(cursor.getString(8));
            room.setStatus(cursor.getString(9));
            roomList.add(room);
        }
        db.close();
        Log.i("DB", "RETURNED LIST");
        return roomList;
    }
    public ChatRoomItem getChatRoomItem(int partyId){
        ChatRoomItem room = new ChatRoomItem();
        SQLiteDatabase db = getReadableDatabase();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        String getResult = "SELECT * FROM " + ChatDBCtrct.roomListTable +" WHERE "+ChatDBCtrct.roomId+" = '" + partyId+"'";
        Cursor cursor = db.rawQuery(getResult, null);
        while (cursor.moveToNext()) {
            room.setRoomName(cursor.getString(3));
            room.setPartyLeader(cursor.getInt(4));
            room.setStatus(cursor.getString(9));
        }
        return room;
    }


    // jk 작업중(미완성)
    public void insertColorOfLectureTimeTableCell(int lectureId, String colorValueOfLectureTimeTableCell) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(HomeDBCtrct.SQL_insertLectureColor(lectureId, colorValueOfLectureTimeTableCell));
            Log.i("INSERTED IN LIST", ""+lectureId);
        } catch (Exception e) {

        }
        db.close();
    }

    public void getColorOfLectureTimeTableCell(ArrayList<LectureColor> lectureColorList) {
        SQLiteDatabase db = getReadableDatabase();

        String getResult = "SELECT * FROM " + HomeDBCtrct.lectureTimeTableCellColorListTable;
        Cursor cursor = db.rawQuery(getResult, null);
        while (cursor.moveToNext()) {
            LectureColor lectureColor = new LectureColor();
            lectureColor.setLectureId(cursor.getInt(1));
            lectureColor.setLectureColorValue(cursor.getString(2));
            lectureColorList.add(lectureColor);
        }
        db.close();
        Log.i("DB", "RETURNED LIST");
    }

    public void dropDB(){
    }
}