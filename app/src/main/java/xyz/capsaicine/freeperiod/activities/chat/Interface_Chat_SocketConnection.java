package xyz.capsaicine.freeperiod.activities.chat;

import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;

public interface Interface_Chat_SocketConnection {

    void ConnectToServer(String Uri, int port);
    void sendMsg(ChatItem chat);
    void leaveRoom(int partyId);
    void receiveMsg();
    void Disconnect();
    void sendNotification(ChatItem chat);
    void connectRoom(int partyId);

}
