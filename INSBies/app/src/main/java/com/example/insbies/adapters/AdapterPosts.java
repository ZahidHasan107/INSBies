package com.example.insbies.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insbies.R;
import com.example.insbies.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    Context context;
    List<ModelPost> postList;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.row_post,parent,false );
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String uid=postList.get(position).getUid();
        String uEmail=postList.get(position).getuEmail();
        String uName=postList.get(position).getuName();
        String uDp=postList.get(position).getuDp();
        String pId=postList.get(position).getpId();
        String pTitle=postList.get(position).getpTitle();
        String pDescription=postList.get(position).getpDescr();
        String pImage=postList.get(position).getpImage();
        String pTimeStamp=postList.get(position).getpTime();

        //convert time stamp
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();
        //set data
        holder.uName.setText(uName);
        holder.pTime.setText(pTimeStamp);
        holder.pTitle.setText(pTitle);
        holder.pDescription.setText(pDescription);
        //dp
        try{
            Picasso.get().load(uDp).placeholder(R.drawable.face_img).into(holder.uPicture);

        }catch (Exception e){

        }

        //post img
        if(pImage.equals("noImage")){
            holder.pImageIv.setVisibility(View.GONE);
        }
        else {
            try{
                Picasso.get().load(pImage).into(holder.pImageIv);

            }catch (Exception e){

            }

        }


        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });
        holder.likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "LIKE", Toast.LENGTH_SHORT).show();
            }
        });
        holder.commentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });

        holder.shatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
            }
        });





    }

    @Override
    public int getItemCount() {
        return postList.size();
    }



    //view holder class
    class  MyHolder extends RecyclerView.ViewHolder{
        //views for xml
        ImageView uPicture,pImageIv;
        TextView uName,pTime,pTitle,pDescription,pLike;
        ImageButton more;
        Button likebtn,commentbtn,shatebtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init view
            uPicture= itemView.findViewById(R.id.uPictureIv);
            pImageIv=itemView.findViewById(R.id.uPictureIv);
            uName=itemView.findViewById(R.id.uNameIv);
            pTime=itemView.findViewById(R.id.uTimeIv);
            pTitle=itemView.findViewById(R.id.pTitleIv);
            pDescription=itemView.findViewById(R.id.pDescriptionIv);
            pLike=itemView.findViewById(R.id.pLikeIv);
            likebtn=itemView.findViewById(R.id.likeBtn);
            commentbtn=itemView.findViewById(R.id.commentBtn);
            shatebtn=itemView.findViewById(R.id.shareBtn);
            more=itemView.findViewById(R.id.moreBtn);
        }
    }
}
