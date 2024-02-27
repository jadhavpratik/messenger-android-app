package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class setting extends AppCompatActivity {
    private ImageView setProfile;
    private EditText setName, setStatus;
    private Button doneButton;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private String email, password;
    private Uri setImageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        setProfile = findViewById(R.id.settingprofile);
        setName = findViewById(R.id.settingname);
        setStatus = findViewById(R.id.settingstatus);
        doneButton = findViewById(R.id.donebut);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("mail").getValue(String.class);
                password = snapshot.child("password").getValue(String.class);
                String name = snapshot.child("userName").getValue(String.class);
                String profile = snapshot.child("profilepic").getValue(String.class);
                String status = snapshot.child("status").getValue(String.class);

                setName.setText(name);
                setStatus.setText(status);
                Picasso.get().load(profile).into(setProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), 10);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        progressDialog.show();

        String name = setName.getText().toString().trim();
        String status = setStatus.getText().toString().trim();

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

        if (setImageUri != null) {
            storageReference.putFile(setImageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String finalImageUri = uri.toString();
                                                Users users = new Users(auth.getUid(), name, email, password, finalImageUri, status);
                                                reference.setValue(users)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                progressDialog.dismiss();
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(setting.this, "Data is saved", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(setting.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(setting.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(setting.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Handle the case when no new image is selected
            Users users = new Users(auth.getUid(), name, email, password, null, status);
            reference.setValue(users)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(setting.this, "Data is saved", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(setting.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(setting.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            setImageUri = data.getData();
            setProfile.setImageURI(setImageUri);
        }
    }
}
















//
//package com.example.messenger;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import com.squareup.picasso.Picasso;
//
//import java.net.URI;
//
//public class setting extends AppCompatActivity {
//    ImageView setprofile;
//    EditText setname, setstatus;
//    Button donebutton;
//    FirebaseAuth auth;
//    FirebaseDatabase database;
//    FirebaseStorage storage;
//    String email,password;
//    Uri setImageUri;
//
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setting);
//
//
//        auth = FirebaseAuth.getInstance();
//        database=FirebaseDatabase.getInstance();
//
//        storage = FirebaseStorage.getInstance();
//        setprofile= findViewById(R.id.settingprofile);
//        setname=findViewById(R.id.settingname);
//        setstatus = findViewById(R.id.settingname);
//        donebutton= findViewById(R.id.donebut);
//
//        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
//        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//              email = snapshot.child("mail").getValue().toString();
//              password = snapshot.child("password").getValue().toString();
//              String name = snapshot.child("userName").getValue().toString();
//              String profile = snapshot.child("profilepic").getValue().toString();
//              String status = snapshot.child("status").getValue().toString();
//              setname.setText(name);
//              setstatus.setText(status);
//                Picasso.get().load(profile).into(setprofile);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        setprofile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("imge/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select picture"),10);
//            }
//        });
//        donebutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = setname.getText().toString();
//                String status = setstatus.getText().toString();
//                if (setImageUri!=null){
//                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    String finalimageUri =uri.toString();
//                                    Users users = new Users(auth.getUid(),name,email,password,finalimageUri,status);
//                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()){
//                                                Toast.makeText(setting.this, "Data is save", Toast.LENGTH_SHORT).show();
//                                                Intent intent = new Intent(setting.this, MainActivity.class);
//                                                startActivity(intent);
//                                                finish();
//                                            }else {
//                                                Toast.makeText(setting.this, "Something went... ", Toast.LENGTH_SHORT).show();
//                                            }
//
//                                        }
//                                    });
//
//                                }
//                            });
//
//                        }
//                    });
//                }else {
//                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            String finalImageUri = uri.toString();
//                            Users users = new Users(auth.getUid(),name,email,password,finalImageUri,status);
//                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                    if(task.isSuccessful()){
//
//                                        Toast.makeText(setting.this, "Data is save", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(setting.this, MainActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }else {
//                                        Toast.makeText(setting.this, "Something went... ", Toast.LENGTH_SHORT).show();
//
//
//                                    }
//
//                                }
//                            });
//
//                        }
//                    });
//                }
//            }
//        });
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==10){
//            if (data!=null){
//                setImageUri = data.getData();
//                setprofile.setImageURI(setImageUri);
//            }
//        }
//    }
//}