package xyz.capsaicine.freeperiod.activities.chat.ListViewItem;

import xyz.capsaicine.freeperiod.activities.chat.ChatUser;

public class ChatItem {

    private ChatUser user;
    private String message; //채팅 내용
    private String createdAt;
    private int roomId; // Room 구분자 후에 ID로 바꿈

    public ChatItem(){}

    public ChatItem(ChatUser user, String message, String createdAt, int roomId) {
        this.user=user;
        this.message = message;
        this.createdAt = createdAt;
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public ChatUser getUser() {
        return user;
    }

    public void setUser(ChatUser user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
