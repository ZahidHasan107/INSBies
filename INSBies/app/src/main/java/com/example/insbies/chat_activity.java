package com.example.insbies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.insbies.adapters.AdapterUsers;
import com.example.insbies.models.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class chat_activity extends AppCompatActivity {

    Toolbar toolbar;
  RecyclerView recyclerView;
  ImageView imageView;
  TextView nameib,activity;
  EditText messegeEt;
  ImageButton send;
FirebaseAuth firebaseAuth;
String hisuid,myuid;
FirebaseDatabase firebaseDatabase;
DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);

        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView=findViewById(R.id.user_recyclerview);
        imageView=(ImageView) findViewById(R.id.profilechat);
        nameib=findViewById(R.id.username);
        activity=findViewById(R.id.activity);
         messegeEt=findViewById(R.id.messageEt);
         send=findViewById(R.id.sendButton);

          Intent intent=getIntent();
          firebaseDatabase =FirebaseDatabase.getInstance();
          databaseReference=firebaseDatabase.getReference("user");
         firebaseAuth = FirebaseAuth.getInstance();
         hisuid =intent.getStringExtra ("UserEmail");
         //
        Query query=databaseReference.orderByChild("email").equalTo(hisuid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    String name=""+ds.child("name").getValue();
                    String image=""+ds.child("image").getValue();
                    nameib.setText(name);
                    try {
                        Picasso.get().load(image).placeholder(R.drawable.face_img).into(imageView);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.face_img).into(imageView);

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=messegeEt.getText().toString().trim();
                //
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(chat_activity.this, "Cannot send the empty message...", Toast.LENGTH_SHORT).show();
                }
                else {
                    //
                    sendMessage(message);
                }

            }
        });

    }
    private void sendMessage(String message) {

        DatabaseReference ds=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender", myuid);
        hashMap.put("reciever", hisuid);
        hashMap.put("messege", message);
        ds.child("chats").push().setValue(hashMap);


    }
    private void checkuserStatus(){


        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            myuid=user.getUid();

        }
        else
        {
            startActivity(new Intent(this,MainActivity.class));
           finish();
        }

    }


    @Override
    protected void onStart() {

        checkuserStatus();
        super.onStart();
    }
}




