package com.example.insbies;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


public class profile extends Fragment {


    //firebase
    FirebaseAuth ba;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String storagepath="user_profile_imgs/";

    TextView name,email,mobile;
    ImageView img,cover;
    FloatingActionButton fab;
    ProgressDialog pd;


    //permission
    private  static  final  int CAMERA_REQUEST_CODE=100;
    private  static  final  int STORAGE_REQUEST_CODE=200;
    private  static  final  int IMAGE_PICK_GALLERY_CODE=300;
    private  static  final  int IMAGE_PICK_CAMERA_CODE=400;
    //array of permissions
    String cameraPermissions[];
    String storagePermissions[];
    String profile;

    Uri image_uri;





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
        storageReference=getInstance().getReference();

        //init permissios
        cameraPermissions =new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //init views
        name=view.findViewById(R.id.name);
        email=view.findViewById(R.id.email);
        mobile=view.findViewById(R.id.phone);
        img=view.findViewById(R.id.profile);
        cover=view.findViewById(R.id.coverimg);
        fab=view.findViewById(R.id.fab);

        pd=new ProgressDialog(getActivity());



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
                    String image=""+ds.child("imgage").getValue();

                    //set
                    name.setText(pro_name_);
                    email.setText(Pro_email);
                    mobile.setText(pro_mobile);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });







        return view;
    }

    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result;
    }

    private void requestStrongePermission(){
     requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }



    private boolean checkCameraPermission(){


        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);



        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }

    private void requestCameraPermission(){
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
    }




    private void showEditProfileDialog() {
        String option[]={"Edit Profile Picture","Edit cover picture","Edit Name","Edit Phone"};
        //alert
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Choose Action");
        //item to dialog
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which==0)
                {
                    //profile picture
                    pd.setMessage("Updating profile Picture");
                    profile="image";
                    showImagePicDialog();
                }
                else  if(which==1)
                {
                    pd.setMessage("Updating cover Picture");
                    profile="cover";
                    showImagePicDialog();
                }
               else if(which==2)
                {
                    //edit name
                    pd.setMessage("Updating Name");

                    showNamePhoneUpdate("name");
                }
                else if(which==3)
                {
                    //edit phone
                    pd.setMessage("Updating Phone");
                    showNamePhoneUpdate("Phone");

                }


            }
        });
        builder.create().show();
    }

    private void showNamePhoneUpdate(String key) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+key);
        //set layout
        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        EditText editText=new EditText(getActivity());
        editText.setHint("Enter "+key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //button for update

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String value=editText.getText().toString().trim();
                if(!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String,Object> result=new HashMap<>();
                    result.put(key,value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Updated...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                }
                else {
                    Toast.makeText(getActivity(), "Please enter", Toast.LENGTH_SHORT).show();
                }

            }
        });
       // builder.create().show();

        //button for cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.create().show();

    }

    private void showImagePicDialog() {
        String option[]={"Camera","Gallery"};
        //alert
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Pick Image From");
        //item to dialog
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which==0)
                {
                    //camera
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }

                }
               else if(which==1)
                {
                    //gallery
                    if(!checkStoragePermission())
                    {
                        requestStrongePermission();
                    }
                    else {
                        pickFromGallery();
                    }

                }



            }
        });
        builder.create().show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                image_uri=data.getData();
                uploadProfilephoto(image_uri);
            }
            if(requestCode==IMAGE_PICK_CAMERA_CODE){
                //image_uri=data.getData();
                uploadProfilephoto(image_uri);

            }




        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilephoto(Uri uri) {
        pd.show();

        String filePath=storagepath+""+profile +"_"+user.getUid();

        StorageReference storageReference2nd=storageReference.child(filePath);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image is uploaded to storage
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri=uriTask.getResult();
                //check uploader or not
                if(uriTask.isSuccessful())
                {
                    HashMap<String,Object> results=new HashMap<>();
                    results.put(profile,downloadUri.toString());
                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Image update", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error updating Image", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Some error", Toast.LENGTH_SHORT).show();
                }
                
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });






    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case CAMERA_REQUEST_CODE:{

               if(grantResults.length>0){
                   boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                   boolean WriteStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                   if(cameraAccepted && WriteStorageAccepted){
                       pickFromCamera();

                   }
                   else {
                       Toast.makeText(getActivity(), "Plese enable camera Permission", Toast.LENGTH_SHORT).show();
                   }

               }

           }
           break;
           case STORAGE_REQUEST_CODE:
           {
               if(grantResults.length>0){
                   boolean WriteStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                   if(WriteStorageAccepted){
                       pickFromGallery();

                   }
                   else {
                       Toast.makeText(getActivity(), "Plese enable Storage Permission", Toast.LENGTH_SHORT).show();
                   }

               }

           }
           break;

       }





        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickFromGallery() {

        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);


    }

    private void pickFromCamera() {

        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp pick");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp discription");
        //put uri

        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //intent camera

        Intent camerainternt=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camerainternt.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(camerainternt,IMAGE_PICK_CAMERA_CODE);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {

        }
        if(item.getItemId()==R.id.action_add_post)
        {
            startActivity(new Intent(getActivity(), AddPostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
