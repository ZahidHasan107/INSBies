package com.example.insbies.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insbies.R;
import com.example.insbies.chat_activity;
import com.example.insbies.models.ModelUsers;
import com.squareup.picasso.Picasso;

import java.util.List;

public class  AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    public AdapterUsers(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    Context context;
    List<ModelUsers> usersList;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate layout

        View view= LayoutInflater.from(context).inflate(R.layout.row_users,parent,false);



        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        //get data


        String hisUID= usersList.get(position).getEmail();
        String UserImage=usersList.get(position).getImg();
        String UserName=usersList.get(position).getName();
        String UserEmail=usersList.get(position).getEmail();
        //set data
        holder.mName.setText(UserName);
        holder.mEmail.setText(UserEmail);
        try{
            Picasso.get().load(UserImage).into(holder.avaterIv);

        }catch(Exception e)
        {

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, ""+UserEmail, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context,chat_activity.class);
                intent.putExtra("UserEmail",UserEmail);
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    //view holder

    class  MyHolder extends RecyclerView.ViewHolder{

        ImageView avaterIv;
        TextView mName,mEmail;



        public MyHolder(@NonNull View itemView) {
            super(itemView);

           avaterIv=itemView.findViewById(R.id.avatarIv);
           mName=itemView.findViewById(R.id.nameTv);
           mEmail=itemView.findViewById(R.id.emailTv);


        }
    }




}
