package xyz.capsaicine.freeperiod.activities.chat.ListViewAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Date;

import xyz.capsaicine.freeperiod.R;
import xyz.capsaicine.freeperiod.activities.chat.ChatUser;
import xyz.capsaicine.freeperiod.activities.chat.Interface_ChattingClickListener;
import xyz.capsaicine.freeperiod.app.Account;
import xyz.capsaicine.freeperiod.activities.chat.ListViewItem.ChatItem;
import xyz.capsaicine.freeperiod.app.Utility;

public class ChatAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_CHAT_SENT = 1;
    private static final int VIEW_TYPE_CHAT_RECEIVED = 2;

    private Context mContext;
    private Interface_ChattingClickListener mListener;
    private ArrayList<ChatItem> mChatList;

    public ChatAdapter(Context mContext, ArrayList<ChatItem> mChatList, Interface_ChattingClickListener mListener) {
        this.mListener = mListener;
        this.mContext = mContext;
        this.mChatList = mChatList;
    }

    public void addChat(ChatItem chat)
    {
        mChatList.add(chat);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_CHAT_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_msg_sent_layout, parent, false);
            return new Chatting_ViewHolder_Sent(view, mListener);
        } else if (viewType == VIEW_TYPE_CHAT_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_msg_received_layout, parent, false);
            return new Chatting_ViewHolder_Received(view, mListener);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

      ChatItem message = (ChatItem) mChatList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_CHAT_SENT:
                ((Chatting_ViewHolder_Sent) holder).bind(message);
                break;
            case VIEW_TYPE_CHAT_RECEIVED:
                ((Chatting_ViewHolder_Received) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    @Override
    public int getItemViewType(int position) {

       ChatItem message = (ChatItem) mChatList.get(position);
//       ChatUser mUser = new ChatUser();
       Account user = Account.getInstance();

        // TODO: 2018-10-05 have to compare user's id ( function in the future needed "getId"
        if (message.getUser().getId()==user.getUserId()) {
            // If the current user is the sender of the message
            return VIEW_TYPE_CHAT_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_CHAT_RECEIVED;
        }
    }

    /****************ViewHolder*****************************************/
    private class Chatting_ViewHolder_Received extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView mMessage;
        TextView mUserName;
        TextView mReceivedTime;
        ImageView mProfileImage;

        private Interface_ChattingClickListener mListener;

        public Chatting_ViewHolder_Received(@NonNull View itemView, Interface_ChattingClickListener listener) {
            super(itemView);
            mMessage = (TextView)itemView.findViewById(R.id.text_Chatting_chatter_msg);
            mUserName = (TextView)itemView.findViewById(R.id.text_Chatting_chatter_name);
            mReceivedTime = (TextView)itemView.findViewById(R.id.text_Chatting_chatter_timeStamp);
            mProfileImage = (ImageView)itemView.findViewById(R.id.imgView_Chatting_chatter_profile);

            mListener = listener;
            mProfileImage.setOnClickListener(this);
            mMessage.setOnLongClickListener(this);
        }

        void bind(ChatItem message)
        {
            mMessage.setText(message.getMessage());
            mUserName.setText(message.getUser().getName());
//            mReceivedTime.setText(toHourMinute(message.getCreatedAt()));
            mReceivedTime.setText(message.getCreatedAt());
            // TODO: 2018-10-05

            //mProfileImage.setImageURI(Uri);// 프로필 이미지 처리해야함
        }

        @Override
        public void onClick(View view) {
            mListener.onImageClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onMessageLongClick(view, getAdapterPosition());
            return true;
        }
    }


    private class Chatting_ViewHolder_Sent extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView mMessage;
        TextView mReceivedTime;

        private Interface_ChattingClickListener mListener;

        public Chatting_ViewHolder_Sent(@NonNull View itemView, Interface_ChattingClickListener listener) {
            super(itemView);
            mMessage = (TextView)itemView.findViewById(R.id.text_Chatting_chatter_msg);
            mReceivedTime = (TextView)itemView.findViewById(R.id.text_Chatting_chatter_timeStamp);
            mListener = listener;
            mMessage.setOnLongClickListener(this);
        }

        void bind(ChatItem message)
        {
            mMessage.setText(message.getMessage());
//            mReceivedTime.setText(toHourMinute(message.getCreatedAt()));
            mReceivedTime.setText(message.getCreatedAt());
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onMessageLongClick(view, getAdapterPosition());
            return true;
        }
    }

    public ChatItem getItem(int position)
    {return mChatList.get(position);}
//
//    private String toHourMinute(String date){
//        Date hm = new Date(date);
//        return Utility.getTimeString(hm);
//    }

    /*****************************************************/


}
