package xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter;

import android.content.Context;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import okhttp3.internal.Util;
import xyz.capsaicine.freeperiod.Model.Party;
import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.Interface_ChatRoomClickListener;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatRoomItem;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.app.Utility;

public class ChatRoomAdapter  extends RecyclerView.Adapter{

    private Context mContext;
    private ArrayList<ChatRoomItem> mChatRoomList;
    private Interface_ChatRoomClickListener mListener;
    private String BLOCKED = "BLOCKED";

    private static final int VIEW_TYPE_ROOM_BLOCKED = 1;
    private static final int VIEW_TYPE_ROOM_RECRUITING = 2;
    private static final int VIEW_TYPE_ROOM_CLOSED = 1;



    public ChatRoomAdapter(Context mContext, ArrayList<ChatRoomItem> mChatRoomList, Interface_ChatRoomClickListener mListener) {
        this.mContext = mContext;
        this.mChatRoomList = mChatRoomList;
        this.mListener = mListener;
    }

    public ChatRoomAdapter() {

    }


    public void addChatRoom(ChatRoomItem chatRoom){
        mChatRoomList.add(chatRoom);
    }
    public ChatRoomItem getChatRoom(int pos){
        return mChatRoomList.get(pos);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        mContext = parent.getContext();
        if(viewType == VIEW_TYPE_ROOM_RECRUITING) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_roomitem, parent, false);
            return new ChatRoomAdapter.Chat_ViewHolder_Room(view);
        }
        else if(viewType == VIEW_TYPE_ROOM_BLOCKED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_roomitem_blocked, parent, false);
            return new ChatRoomAdapter.Chat_ViewHolder_Room_Blocked(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatRoomItem room = (ChatRoomItem) mChatRoomList.get(position);
//        ((ChatRoomAdapter.Chat_ViewHolder_Room)holder).bind(room);

        switch (holder.getItemViewType()) {

            case VIEW_TYPE_ROOM_BLOCKED: {
                ((ChatRoomAdapter.Chat_ViewHolder_Room_Blocked) holder).bind(room);
                break;
            }
            case VIEW_TYPE_ROOM_RECRUITING: {
                ((ChatRoomAdapter.Chat_ViewHolder_Room) holder).bind(room);
                break;
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mListener.onRoomClick(view, position);
                }
            });

    }

    @Override
    public int getItemViewType(int position) {

        ChatRoomItem item = (ChatRoomItem) mChatRoomList.get(position);
//       ChatUser mUser = new ChatUser();
        // TODO: 2018-10-05 have to compare user's id ( function in the future needed "getId"
        if (Party.Status.valueOf(item.getStatus())== Party.Status.Blocked) {
            // If the current user is the sender of the message
            return VIEW_TYPE_ROOM_BLOCKED;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_ROOM_RECRUITING;
        }
    }

    @Override
    public int getItemCount() {
        return mChatRoomList.size();
    }



    private class Chat_ViewHolder_Room extends RecyclerView.ViewHolder
    {
        private ImageView imgPartyCover;
        private TextView txtRoomName;
        private TextView txtLastMessage;
        private TextView txtCapacity;
        private TextView txtLastTalkTime;



        public Chat_ViewHolder_Room(@NonNull View itemView) {
            super(itemView);

            imgPartyCover = itemView.findViewById(R.id.img_chat_partycover);
            txtRoomName = itemView.findViewById(R.id.txt_chat_partyname);
            txtLastMessage = itemView.findViewById(R.id.txt_chat_lastmessage);
            txtCapacity = itemView.findViewById(R.id.txt_chat_capacity);
            txtLastTalkTime = itemView.findViewById(R.id.txt_chat_lasttalktime);


        }

        void bind(ChatRoomItem room)
        {
//            Utility.getBitmapFromUrl("http://partyhae.com/web/skin/party_category_bnr.png");
//            imgPartyCover.setImageBitmap(Utility.bitmap);
            imgPartyCover.setImageDrawable(room.getPartyCover());
            txtRoomName.setText(room.getRoomName());
            txtLastTalkTime.setText(room.getLastTalkTime());
            txtCapacity.setText(room.getCapacityString());
            txtLastMessage.setText(room.getLastMessage());
        }
    }


    private class Chat_ViewHolder_Room_Blocked extends RecyclerView.ViewHolder
    {
        private ImageView imgPartyCover;
        private TextView txtRoomName;
        private TextView txtLastMessage;
        private TextView txtCapacity;
        private TextView txtLastTalkTime;


        public Chat_ViewHolder_Room_Blocked(@NonNull View itemView) {
            super(itemView);

            imgPartyCover = itemView.findViewById(R.id.img_chat_partycover);
            txtRoomName = itemView.findViewById(R.id.txt_chat_partyname);
            txtLastMessage = itemView.findViewById(R.id.txt_chat_lastmessage);
            txtCapacity = itemView.findViewById(R.id.txt_chat_capacity);
            txtLastTalkTime = itemView.findViewById(R.id.txt_chat_lasttalktime);


        }



        void bind(ChatRoomItem room)
        {
//            Utility.getBitmapFromUrl("http://partyhae.com/web/skin/party_category_bnr.png");
//            imgPartyCover.setImageBitmap(Utility.bitmap);
            imgPartyCover.setImageDrawable(room.getPartyCover());
            txtRoomName.setText(room.getRoomName());
//            txtLastTalkTime.setText(toMonthDayHourMinute(room.getLastTalkTime()));
            txtLastTalkTime.setText(room.getLastTalkTime());
            txtCapacity.setText(room.getCapacityString());
            txtLastMessage.setText(room.getLastMessage());
        }
    }


    public void sortScreen(){
//        mChatRoomList.sort();

    }

    public void sortScreen(int roomId){
        int pos = findIndexOf(roomId);
        ChatRoomItem item = mChatRoomList.get(pos);
        mChatRoomList.remove(pos);
        mChatRoomList.add(0, item);
    }

    public void updateScreen(int roomId, String lastMessage, String lastTalkTime){
        int pos  = findIndexOf(roomId);
        mChatRoomList.get(pos).setLastMessage(lastMessage);
        mChatRoomList.get(pos).setLastTalkTime(lastTalkTime);
        //To move the current to the top
//        sortScreen(roomId);
    }
    public void updateScreen(int roomId,int capacityCurrent){
        int pos = findIndexOf(roomId);
        mChatRoomList.get(pos).setCapacityCurrent(capacityCurrent);

    }
    private int findIndexOf(int roomId)
    {
        int pos = -1;
        for(int idx = 0; idx < mChatRoomList.size(); idx++){
            if(roomId == mChatRoomList.get(idx).getRoomId()) {
                pos = idx;
                break;
            }
        }
        return pos;
    }

    public void updateScreen(ChatRoomItem item){
        mChatRoomList.add(item);
    }

    public void clearChatRoomList(){
//        for(int i =0; i<mChatRoomList.size(); i++){
//            mChatRoomList.get(i)
//        }
        mChatRoomList.clear();

    }

//
//    public String toMonthDayHourMinute(String date){
//        Date mdhm = new Date(date);
//        return Utility.getChatRoomTimeString(mdhm);
//    }
}
