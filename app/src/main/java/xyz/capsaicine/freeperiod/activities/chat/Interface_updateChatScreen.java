package xyz.capsaicine.freeperiod.activities.chat;

import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;

public interface Interface_updateChatScreen {

    void receiveChat(ChatItem item);
    void receiveNotification(ChatItem item);

}
