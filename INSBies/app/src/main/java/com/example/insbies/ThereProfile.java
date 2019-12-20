package com.example.insbies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.insbies.adapters.AdapterPosts;
import com.example.insbies.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ThereProfile extends AppCompatActivity {

    RecyclerView postRecyclerview;
    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String Email;

    TextView name,email,mobile,uaddress,ublood,umajor,uposition;;
    ImageView img,cover;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

        postRecyclerview=findViewById(R.id.recyclerview_post);
        firebaseAuth=FirebaseAuth.getInstance();

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        mobile=findViewById(R.id.phone);
        img=findViewById(R.id.profile);
        cover=findViewById(R.id.coverimg);
        uaddress=findViewById(R.id.addres);
        ublood=findViewById(R.id.blood);
        umajor=findViewById(R.id.major);
        uposition=findViewById(R.id.position);

        Intent intent=getIntent();
        Email=intent.getStringExtra("UserEmail");

        Query query=FirebaseDatabase.getInstance().getReference("user").orderByChild("email").equalTo(Email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //checkc unite required data get

                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    String pro_name_= ""+ds.child("name").getValue();
                    String Pro_email= ""+ds.child("email").getValue();
                    String pro_mobile= ""+ds.child("mobile").getValue();
                    String image=""+ds.child("imgage").getValue();
                    String pro_add=""+ds.child("address").getValue();
                    String pro_blood=""+ds.child("blood").getValue();
                    String pro_major=""+ds.child("major").getValue();
                    String pro_position=""+ds.child("position").getValue();


                    //set
                    name.setText(pro_name_);
                    email.setText(Pro_email);
                    mobile.setText(pro_mobile);
                    uaddress.setText(pro_add);
                    ublood.setText(pro_blood);
                    umajor.setText(pro_major);
                    uposition.setText(pro_position);

                    try {
                        Picasso.get().load(image).into(img);

                    }catch (Exception e)
                    {
                        Picasso.get().load(R.drawable.ic_menu_camera).into(img);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postList=new ArrayList<>();


        checkUserStatus();
        loadHisPost();
    }

    private void loadHisPost() {
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postRecyclerview.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
        Query query=ref.orderByChild("UEmail").equalTo(Email);
        //get item
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelPost mypost=ds.getValue(ModelPost.class);

                    postList.add(mypost);

                    adapterPosts=new AdapterPosts(ThereProfile.this,postList);

                    postRecyclerview.setAdapter(adapterPosts);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfile.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private  void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){

        }
        else {
            startActivity(new Intent(this,MainActivity.class));
          finish();
        }
    }
}
