package com.example.insbies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import com.example.insbies.adapters.AdapterChat;
import com.example.insbies.adapters.AdapterUsers;
import com.example.insbies.models.ModelChat;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class chat_activity extends AppCompatActivity {
    Toolbar toolbar;
  ImageView imageView;
  TextView name,activity;
  EditText messegeEt;
  ImageButton send;


      RecyclerView recyclerView;


      TextView nameib;



    String hisuid,myuid;
    String hisImage;

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //
    ValueEventListener seenListener;
    DatabaseReference userRefSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);

        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        //imageView=(ImageView) findViewById(R.id.);
        name=findViewById(R.id.username);

        recyclerView = findViewById(R.id.chat_recylerView);
        imageView=(ImageView) findViewById(R.id.profilechat);
        nameib=findViewById(R.id.username);

        activity=findViewById(R.id.activity);
         messegeEt=findViewById(R.id.messageEt);
         send=findViewById(R.id.sendButton);


         //
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        //recyclerview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

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
                    hisImage=""+ds.child("image").getValue();
                    nameib.setText(name);
                    try {
                        Picasso.get().load(hisImage).placeholder(R.drawable.face_img).into(imageView);
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
               messegeEt.setText("");

            }
        });

        readMessages();

        seenMessage();

    }

    private void seenMessage() {
        userRefSeen=FirebaseDatabase.getInstance().getReference("chats");
        seenListener=userRefSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);

                    String meg=chat.getMessege();
                    //Toast.makeText(chat_activity.this, meg, Toast.LENGTH_SHORT).show();
                  //  Toast.makeText(chat_activity.this, sender, Toast.LENGTH_SHORT).show();


                        if(chat.getReciever().equals(myuid) && chat.getSender().equals(hisuid)
                                ){
                            HashMap<String,Object> hasSeenHasmap=new HashMap<>();
                            hasSeenHasmap.put("isSeen", true);
                            ds.getRef().updateChildren(hasSeenHasmap);
                        }

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readMessages() {
        chatList=new ArrayList<>();
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){

                    ModelChat chat=ds.getValue(ModelChat.class);


                        if(chat.getReciever().equals(myuid) && chat.getSender().equals(hisuid)||
                                chat.getReciever().equals(hisuid) && chat.getSender().equals(myuid)){
                            chatList.add(chat);
                        }

                    //adapter
                        adapterChat=new AdapterChat(chat_activity.this, chatList, hisImage);
                        adapterChat.notifyDataSetChanged();
                        //recyclerView
                        recyclerView.setAdapter(adapterChat);
                    }

                    }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String message) {

        DatabaseReference ds=FirebaseDatabase.getInstance().getReference();

        String timestamp=String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender", myuid);
        hashMap.put("reciever", hisuid);
        hashMap.put("messege", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeen", false);

        ds.child("chats").push().setValue(hashMap);


    }
    private void checkuserStatus(){


        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            myuid=user.getEmail();

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

    @Override
    protected void onPause() {
        super.onPause();
        userRefSeen.removeEventListener(seenListener);
    }
}




