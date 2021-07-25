package xyz.capsaicine.freeperiod.activities.chat;

public class ChatUser {

    private String userName = "HwanBum";
    private int userId = 1;

    public ChatUser() {
    }

    public ChatUser(String name, int id) {
        this.userName = name;
        this.userId = id;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public int getId() {
        return userId;
    }

    public void setId(int id) {
        this.userId = id;
    }
}
