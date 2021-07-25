package xyz.capsaicine.freeperiod.Model;

import android.graphics.drawable.Drawable;

import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatRoomItem;

public class ChatRoomInDatabase {

    private int partyLeader;
    private String status;
    private int roomId;
    private byte[] partyCover;
    private String roomName;
    private int capacityMax;
    private int capacityCurrent;
    private String lastMessage ="";
    private String lastTalkTime = "";

    public ChatRoomInDatabase(String status, int roomId, byte[] partyCover, String roomName, int partyLeader, int capacityMax, int capacityCurrent, String lastMessage, String lastTalkTime) {
        this.status = status;
        this.roomId = roomId;
        this.partyCover = partyCover;
        this.roomName = roomName;
        this.capacityMax = capacityMax;
        this.capacityCurrent = capacityCurrent;
        this.lastMessage = lastMessage;
        this.lastTalkTime = lastTalkTime;
        this.partyLeader = partyLeader;
    }

    public ChatRoomInDatabase() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public byte[] getPartyCover() {
        return partyCover;
    }

    public void setPartyCover(byte[] partyCover) {
        this.partyCover = partyCover;
    }

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

    public int getPartyLeader() {
        return partyLeader;
    }

    public void setPartyLeader(int partyLeader) {
        this.partyLeader = partyLeader;
    }
}
