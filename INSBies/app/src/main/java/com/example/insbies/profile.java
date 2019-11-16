package com.example.insbies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile extends Fragment {


    //firebase
    FirebaseAuth ba;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextView name,email,mobile;
    ImageView img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_profile, container, false);

        //init firebase
        ba=FirebaseAuth.getInstance();
        user=ba.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("user");

        //init views
        name=view.findViewById(R.id.name);
        email=view.findViewById(R.id.email);
        mobile=view.findViewById(R.id.phone);
        img=view.findViewById(R.id.profile);


        Query query=databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //checkc unite required data get

                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    String pro_name_= ""+ds.child("name").getValue();
                    String Pro_email= ""+ds.child("email").getValue();
                    String pro_mobile= ""+ds.child("mobile").getValue();

                    //set
                    name.setText(pro_name_);
                    email.setText(Pro_email);
                    mobile.setText(pro_mobile);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








        return view;
    }

}
