package com.example.insbies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;



public class chat_activity extends AppCompatActivity {

    Toolbar toolbar;
  RecyclerView recyclerView;
  ImageView imageView;
  TextView name,activity;
  EditText messegeEt;
  ImageButton send;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);

        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView=findViewById(R.id.user_recyclerview);
        //imageView=(ImageView) findViewById(R.id.);
        name=findViewById(R.id.username);
        activity=findViewById(R.id.activity);
         messegeEt=findViewById(R.id.messageEt);
         send=findViewById(R.id.sendButton);




    }
}
