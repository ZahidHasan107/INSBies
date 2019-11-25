package com.example.insbies;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.insbies.adapters.AdapterUsers;
import com.example.insbies.models.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Users extends Fragment {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUsers> usersList;
    FirebaseAuth firebaseAuth;


    public Users() {
        // Required empty public constructor
    }




    private void checkuserStatus(){


        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){






        }
        else
        {


            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }











    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);

        //init recyclearview
        recyclerView=view.findViewById(R.id.user_recyclerview);

        //set properties

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init user list
        usersList=new ArrayList<>();

        //getAll users

        getAllUsers();

        return view;
    }

    private void getAllUsers() {
        //get current users
        FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        //get containing users info
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("user");
        //get all data
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    ModelUsers modelUsers=ds.getValue(ModelUsers.class);
                    if(!modelUsers.getEmail().equals(fuser.getEmail()))
                    {
                        usersList.add(modelUsers);
                    }

                    //adapter
                    adapterUsers=new AdapterUsers(getActivity(),usersList);

                    recyclerView.setAdapter(adapterUsers);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
