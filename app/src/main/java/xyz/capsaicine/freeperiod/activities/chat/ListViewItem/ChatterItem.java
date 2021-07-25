package xyz.capsaicine.freeperiod.activities.chat.ListViewItem;

import xyz.capsaicine.freeperiod.activities.chat.ChatUser;


public class ChatterItem {
    String userName;
    int userId;

    private ChatUser user;

    public ChatterItem(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
        this.user = new ChatUser(userName,userId);
    }

    public ChatUser getUser() {
        return user;
    }

    public void setUser(ChatUser user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
