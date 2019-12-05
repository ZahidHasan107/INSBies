package com.example.insbies.adapters;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.insbies.R;
import com.example.insbies.ThereProfile;
import com.example.insbies.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    Context context;
    List<ModelPost> postList;

    String myUid;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
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
            holder.pImageIv.setVisibility(View.VISIBLE);
            try{
                Picasso.get().load(pImage).into(holder.pImageIv);

            }catch (Exception e){

            }

        }


        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
                showMoreOption(holder.more,uid,myUid,pId,pImage);

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
        /*holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, ThereProfile.class);
                intent.putExtra("UserEmail",uEmail);
                context.startActivity(intent);
            }
        });*/





    }

    private void showMoreOption(ImageButton more, String uid, String myUid, final String pId,final String pImage) {

        //creating popup menu
        PopupMenu popupMenu=new PopupMenu(context,more, Gravity.END);
        if(uid.equals(myUid)) {
            //add item
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
        }

        //
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id=menuItem.getItemId();

                if(id==0){
                    beginDelete(pId,pImage);
                }

                return false;
            }
        });
        popupMenu.show();

    }

    private void beginDelete(String pId, String pImage) {
        if(pImage.equals("noImage")){
            deleteWithoutImage(pId);

        }else {

            deletewithImage(pId,pImage);

        }

    }

    private void deletewithImage(String pId, String pImage) {
        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Deleting...");

        StorageReference picRef= FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Query fquery= FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                        fquery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();
                                }
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void deleteWithoutImage(String pId) {

        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Deleting...");

        Query fquery= FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        LinearLayout profileLayout;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init view
            uPicture= itemView.findViewById(R.id.uPictureIv);
            pImageIv=itemView.findViewById(R.id.pImage);
            uName=itemView.findViewById(R.id.uNameIv);
            pTime=itemView.findViewById(R.id.uTimeIv);
            pTitle=itemView.findViewById(R.id.pTitleIv);
            pDescription=itemView.findViewById(R.id.pDescriptionIv);
            pLike=itemView.findViewById(R.id.pLikeIv);
            likebtn=itemView.findViewById(R.id.likeBtn);
            commentbtn=itemView.findViewById(R.id.commentBtn);
            shatebtn=itemView.findViewById(R.id.shareBtn);
            more=itemView.findViewById(R.id.moreBtn);
            profileLayout=itemView.findViewById(R.id.profileLayout);
        }
    }
}
