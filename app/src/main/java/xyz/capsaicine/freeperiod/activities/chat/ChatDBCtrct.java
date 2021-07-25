package xyz.capsaicine.freeperiod.activities.chat;

import android.database.sqlite.SQLiteDatabase;

import java.sql.Blob;

import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;

public class ChatDBCtrct {


    public static final String chatDB = "dbChat.db";
    public static String room = "ROOM_NO";

    //TABLE 이름 <공지사항 TABLE> <채팅방 목록 TABLE> <각 채팅방 TABLE>
    public static final String notificationTable = "notificationTable";
    public static final String roomListTable = "roomList";


    //채팅방 TABLE 관련 COLUMN <ROOMID,USERID, USERNAME, MESSAGE, CREATEDAT>
    public static final String userID ="userID";
    public static final String userName ="userName";
    public static final String message = "message";
    public static final String createdAt = "createdAt";


    //채팅방 목록 TABLE COLUMN <ROOMID, IMAGE, ROOMNAME, CAPACITYCURRENT, CAPACITYMAX, LASTMESSAGE, LASTTALKTIME>

    public static final String roomStatus = "roomStatus";
    public static final String image = "image"; //방 이미지 --> 추후 이미지로 바뀔예정 FIREBASE에 있는...URL로 연결하여 Load
    public static final String roomName = "roomName"; //후에 채팅방 이름 받아올거임
    public static final String capacityCurrent = "capacityCurrent"; //수용인원 n/m
    public static final String capacityMax = "capacityMax"; //수용인원 n/m
    public static final String lastMessage = "lastMessage";
    public static final String lastTalkTime = "lastTalkTime";
    public static final String partyLeader = "partyLeader";

    //공지 TABLE COLUMN <ROOMID, USERID, USERNAME, NOTIFICATION, NOTIFICATIONTIME>

    public static final String notification = "notification";
    public static final String notificationTime ="notificationTime";
    //공통
    public static final String roomId = "roomId";


    public static final String SQL_createRoomListTable = "CREATE TABLE IF NOT EXISTS " + roomListTable +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            roomId + " INTEGER," +
            image + " BLOB, " +
            roomName + " TEXT, " +
            partyLeader + " INTEGER," +
            capacityCurrent + " INTEGER," +
            capacityMax + " INTEGER," +
            lastMessage + " TEXT," +
            lastTalkTime + " TEXT," +
            roomStatus + " TEXT);";


    public static final String SQL_createNotificationTable = "CREATE TABLE " + notificationTable +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            roomId + " INTEGER, " +
            userID + " INTEGER," +
            userName + " TEXT, " +
            notification + " TEXT," +
            notificationTime+ " TEXT);";



    public static String SQL_deleteRoomFromList(int roomId){
        return "DELETE FROM "+ChatDBCtrct.roomListTable+" WHERE "+ChatDBCtrct.roomId+"=" + roomId + ";";
    }
    public static String SQL_deleteRoomFromNotificationList(int roomId){
        return "DELETE FROM "+ChatDBCtrct.notificationTable+" WHERE "+ChatDBCtrct.roomId+"=" + roomId + ";";
    }
    public static String SQL_dropRoomTable(int roomId){
        return "DROP TABLE IF EXISTS " + ChatDBCtrct.room+roomId+";";
    }

    public static String SQL_createRoomTable(int roomId){
        String room = ChatDBCtrct.room +roomId;
        String createRoomTable = "CREATE TABLE " + room +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                userID + " INTEGER, " +
                userName + " TEXT, " +
                message + " TEXT, " +
                createdAt+ " TEXT);";
        return createRoomTable;
    }

    public static String SQL_insertRoomList(int roomId, byte[] image, String roomName,int partyLeader, int capacityCurrent, int capacityMax, String lastMessage, String lastTalkTime, String roomStatus) {
            return "INSERT INTO " + roomListTable + " VALUES(null, " +
                    roomId + ", " +
                    "?" + ", '" +
                    roomName + "', '" +
                    partyLeader + "', '" +

                    capacityCurrent + "', '" +
                    capacityMax + "', '" +
                    lastMessage + "', '" +
                    lastTalkTime + "', '" +
                    roomStatus + "')";
    }

    public static String SQL_updateRoomLastMessage(ChatItem item){

        int roomId;
        String message, lastTalkTime;
        roomId = item.getRoomId();
        String room = ChatDBCtrct.room +roomId;
        message = item.getMessage();
        lastTalkTime = item.getCreatedAt();
        String updateQuery = "UPDATE " + ChatDBCtrct.roomListTable + " SET "+
                ChatDBCtrct.lastMessage+ "='" + message+ "', "+
                ChatDBCtrct.lastTalkTime+ "='" +lastTalkTime+ "' "+
                "WHERE " + ChatDBCtrct.roomId + "=" + roomId + "";
        return updateQuery;
    }

    public static String SQL_updateRoomImage(int roomId){
        String updateQuery = "UPDATE " + ChatDBCtrct.roomListTable + " SET "+
                ChatDBCtrct.image+ "=" + "?"+ " "+
                "WHERE " + ChatDBCtrct.roomId + "=" + roomId + "";
        return updateQuery;
    }
    public static String SQL_updateRoomCapacityCurrent(int roomId, int capacityCurrent){
        String updateQuery = "UPDATE " + ChatDBCtrct.roomListTable + " SET "+
                ChatDBCtrct.capacityCurrent+ "=" + capacityCurrent+ " "+
                "WHERE " + ChatDBCtrct.roomId + "=" + roomId + "";
        return updateQuery;
    }
    public static String SQL_updateRoomStatus(int roomId, String status){
        String updateQuery = "UPDATE " + ChatDBCtrct.roomListTable + " SET "+
                ChatDBCtrct.roomStatus+ "='" + status+ "' "+
                "WHERE " + ChatDBCtrct.roomId + "=" + roomId + "";
        return updateQuery;
    }

    public static String SQL_updateNotification(ChatItem chat){
        String userName,message,notificationTime;
        int roomId, userID;
        roomId = chat.getRoomId();
        userName = chat.getUser().getName();
        userID = chat.getUser().getId();
        message=chat.getMessage();
        notificationTime = chat.getCreatedAt();

        String updateQuery = "UPDATE " + ChatDBCtrct.notificationTable + " SET "+
                ChatDBCtrct.userID+ "=" + userID+ ", "+
                ChatDBCtrct.userName + "='" + userName+ "', "+
                ChatDBCtrct.notification+"='" + message+ "', "+
                ChatDBCtrct.notificationTime+ "='" +notificationTime+ "' "+
                "WHERE " + ChatDBCtrct.roomId + "=" + roomId + "";
        return updateQuery;
    }

    public static String SQL_insertChat(ChatItem chat)
    {
        String userName,message,createdAt;
        int roomId, userID;
        userName = chat.getUser().getName();
        userID = chat.getUser().getId();
        message=chat.getMessage();
        createdAt = chat.getCreatedAt();
        roomId = chat.getRoomId();
        String room = ChatDBCtrct.room+ roomId;
        // DB에 입력한 값으로 행 추가
        String insertQuery ="INSERT INTO "+room+" VALUES(null, " + userID + ",'" + userName + "', '" + message + "', '" + createdAt + "')";

        return insertQuery;
    }











}
