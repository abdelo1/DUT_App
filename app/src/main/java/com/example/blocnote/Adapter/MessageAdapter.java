package com.example.blocnote.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.blocnote.R;
import com.example.blocnote.model.Chat;

import com.google.firebase.auth.FirebaseAuth;


import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.SentMessageHolder> {

    private Context mcontext;
    private List<Chat> listChat;
    public static final int MSG_LEFT=0;
    public static final int MSG_Right=1;

    public MessageAdapter(Context context, List<Chat> listChat ){
        this.mcontext=context;
        this.listChat=listChat;


            }


    @NonNull
    @Override
    public SentMessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i==MSG_Right)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sender_layou_message,viewGroup,false);
            return new MessageAdapter.SentMessageHolder(view);
        }
        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.receiver_layou_message,viewGroup,false);
            return new MessageAdapter.SentMessageHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull SentMessageHolder sentMessageHolder, int i) {
        Chat chat =listChat.get(i);
        sentMessageHolder.messageText.setText(chat.getMessage());
        sentMessageHolder.timeText.setText(chat.getTime());
    }


    @Override
    public int getItemCount() {
        return listChat.size();
    }

    @Override
    public int getItemViewType(int position) {
       Chat message = listChat.get(position);

        if (message.getSenderid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            // If the current user is the sender of the message
            return MSG_Right;
        } else {
            // If some other user sent the message
            return MSG_LEFT;
        }
    }


    public class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;


        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);

        }

    }
}
