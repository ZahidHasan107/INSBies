package com.example.insbies.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insbies.R;
import com.example.insbies.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    private Context context;
    private List<ModelChat> chatList;
    private String imageUrl;

    //firebase
    FirebaseUser fuser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

      if(i==MSG_TYPE_RIGHT){
          View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent,false);
          return new MyHolder(view);
      }
      else {
          View view= LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent,false);
          return new AdapterChat.MyHolder(view);
      }


    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String message=chatList.get(position).getMessege();
        String timeStamp=chatList.get(position).getTimestamp();

        //convert time stamp
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();


        //set data
        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);
        try{
            Picasso.get().load(imageUrl).into(holder.profileIv);

        }catch(Exception e){


        }

        //set seen/delivered
        if(position==chatList.size()-1){
            if(chatList.get(position).isSeen()){
                holder.isSeeTv.setText("Seen");
            }
            else {
                holder.isSeeTv.setText("Delivered");
            }
        }
        else {
            holder.isSeeTv.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemViewType(int position) {
        //get current user
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fuser.getEmail())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views
        ImageView profileIv;
        TextView messageTv,timeTv,isSeeTv;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profileIv=itemView.findViewById(R.id.profileIV);
            messageTv=itemView.findViewById(R.id.messageIV);
            timeTv=itemView.findViewById(R.id.timeIV);
            isSeeTv=itemView.findViewById(R.id.isseenIV);
        }
    }

}
