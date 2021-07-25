package xyz.capsaicine.freeperiod.activities.chat.ListViewItem;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.Calendar;

import xyz.capsaicine.freeperiod.Model.ChatRoomInDatabase;
import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.Model.PartyInDatabase;
import xyz.capsaicine.freeperiod.app.Utility;

public class ChatRoomItem {


    private String status;
    private int roomId;
    private int partyLeader;
    private Drawable partyCover;
    private String roomName;
    private int capacityMax;
    private int capacityCurrent;
    private String lastMessage ="";
    private String lastTalkTime = "";

    public ChatRoomItem(int roomId, Drawable partyCover, String roomName, int capacityMax, int capacityCurrent, String lastmessage, String lastTalkTime) {
        this.roomId = roomId;
        this.partyCover = partyCover;
        this.roomName = roomName;
        this.capacityMax = capacityMax;
        this.capacityCurrent = capacityCurrent;
        this.lastMessage = lastmessage;
        this.lastTalkTime = lastTalkTime;
    }

    public ChatRoomItem(int roomId, Drawable partyCover, String roomName, int capacityMax, int capacityCurrent, String lastMessage, String lastTalkTime,String status) {
        this.status = status;
        this.roomId = roomId;
        this.partyCover = partyCover;
        this.roomName = roomName;
        this.capacityMax = capacityMax;
        this.capacityCurrent = capacityCurrent;
        this.lastMessage = lastMessage;
        this.lastTalkTime = lastTalkTime;
    }

    public int getPartyLeader() {
        return partyLeader;
    }

    public void setPartyLeader(int partyLeader) {
        this.partyLeader = partyLeader;
    }

    public ChatRoomItem(ChatRoomInDatabase chatRoomInDatabase){
        roomId = chatRoomInDatabase.getRoomId();
        status = chatRoomInDatabase.getStatus();
        partyCover = Utility.convertByteArrayToDrawable(chatRoomInDatabase.getPartyCover());
        roomName = chatRoomInDatabase.getRoomName();
        capacityMax = chatRoomInDatabase.getCapacityMax();
        capacityCurrent = chatRoomInDatabase.getCapacityCurrent();
        partyLeader = chatRoomInDatabase.getPartyLeader();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ChatRoomItem() {
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Drawable getPartyCover() {
        return partyCover;
    }

    public void setPartyCover(Drawable partyCover) {
        this.partyCover = partyCover;
    }
//    public void setPartyCover(Bitmap bitmap) {this.partyCover.}

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getCapacityMax() {
        return capacityMax;
    }

    public void setCapacityMax(int capacityMax) {
        this.capacityMax = capacityMax;
    }

    public int getCapacityCurrent() {
        return capacityCurrent;
    }

    public void setCapacityCurrent(int capacityCurrent) {
        this.capacityCurrent = capacityCurrent;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastTalkTime() {
        return lastTalkTime;
    }

    public void setLastTalkTime(String lastTalkTime) {
        this.lastTalkTime = lastTalkTime;
    }
    public String getCapacityString(){
        return "(" + capacityCurrent + "/" + capacityMax + ")";
    }
}
