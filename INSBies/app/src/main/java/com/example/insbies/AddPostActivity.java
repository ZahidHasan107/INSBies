package com.example.insbies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static com.example.insbies.R.id.pUpload;

public class AddPostActivity extends AppCompatActivity {

    ActionBar actionBar;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    //views
    EditText titleEt, descriptionEt;
    ImageView imageIv;
    Button upload;
    //permotion
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];
    Uri image_uri = null;

    //user
    String name, email, dp, uid;

    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Add New Post");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //init permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd=new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        //get some info
        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    name=""+ds.child("name").getValue();
                    email=""+ds.child("email").getValue();
                    dp=""+ds.child("image").getValue();

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //init Views
        titleEt = findViewById(R.id.pTitle);
        descriptionEt = findViewById(R.id.pDescriptionEt);
        imageIv = findViewById(R.id.pImageIv);
        upload = findViewById(pUpload);

        imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showImagePickDialog();

            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Title = titleEt.getText().toString().trim();
                String Description = descriptionEt.getText().toString().trim();

                if(TextUtils.isEmpty(Title)){
                    Toast.makeText(AddPostActivity.this, "Enter title", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(Description)){
                    Toast.makeText(AddPostActivity.this, "Enter description", Toast.LENGTH_SHORT).show();
                }
                if(image_uri==null){
                    uploadData(Title,Description,"noImage");
                }
                else{
                    uploadData(Title,Description,String.valueOf(image_uri));

                }

            }
        });


    }

    private void uploadData(final String title, String description, String uri) {

        pd.setMessage("Publishing post...");
        pd.show();

        String timestamp=String.valueOf(System.currentTimeMillis());
        String filePathAndName="Posts/"+"post_"+timestamp;

        if(!uri.equals("noImage")){
            StorageReference ref= FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            String downloadUri=uriTask.getResult().toString();
                            if(uriTask.isSuccessful()){
                                HashMap<Object,String> hashMap=new HashMap<>();

                                hashMap.put("uid",uid);
                                hashMap.put("uName",name);
                                hashMap.put("UEmail",email);
                                hashMap.put("uDp",dp);
                                hashMap.put("pId",timestamp);
                                hashMap.put("pTitle",title);
                                hashMap.put("pDescr",description);
                                hashMap.put("pImage",downloadUri);
                                hashMap.put("pTime",timestamp);

                                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");

                                reference.child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this, "Post Publoshed", Toast.LENGTH_SHORT).show();
                                                titleEt.setText("");
                                                descriptionEt.setText("");
                                                imageIv.setImageURI(null);
                                                image_uri=null;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
        else {

            HashMap<Object,String> hashMap=new HashMap<>();

            hashMap.put("uid",uid);
            hashMap.put("uName",name);
            hashMap.put("UEmail",email);
            hashMap.put("uDp",dp);
            hashMap.put("pId",timestamp);
            hashMap.put("pTitle",title);
            hashMap.put("pDescr",description);
            hashMap.put("pImage","noImage");
            hashMap.put("pTime",timestamp);

            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");

            reference.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this, "Post Published", Toast.LENGTH_SHORT).show();
                            titleEt.setText("");
                            descriptionEt.setText("");
                            imageIv.setImageURI(null);
                            image_uri=null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }

    }


    private void showImagePickDialog() {
        String[] option = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image from");
        //set optiojn dialog
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (!checkCameraPermission()) {
                        requestcameraPermission();
                    } else {
                        pickFromCamera();
                    }

                }
                if (i == 1) {
                    if (!checkStoragePermission()) {
                        requeststoragePermission();
                    } else {
                        pickFromGallery();
                    }

                }
            }
        });
        builder.create().show();

    }

    private void pickFromGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {


        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp discription");
        //put uri

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //intent camera

        Intent camerainternt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camerainternt.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(camerainternt, IMAGE_PICK_CAMERA_CODE);

    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requeststoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestcameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

            email = user.getEmail();
            uid = user.getUid();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_add_post).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {

                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && WriteStorageAccepted) {
                        pickFromCamera();

                    } else {
                        Toast.makeText(this, "Plese enable camera Permission", Toast.LENGTH_SHORT).show();
                    }

                }

            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean WriteStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (WriteStorageAccepted) {
                        pickFromGallery();

                    } else {
                        Toast.makeText(this, "Plese enable Storage Permission", Toast.LENGTH_SHORT).show();
                    }

                }

            }
            break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_GALLERY_CODE) {
            image_uri = data.getData();
            imageIv.setImageURI(image_uri);

        } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
            //image_uri=data.getData();
            imageIv.setImageURI(image_uri);


        }


    }
}
