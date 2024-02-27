package com.example.messenger;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {

    String receiverImg, receiverUid, receiverName, senderUid;

    CircleImageView profile;
    EditText textMsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference chatReference;

    public static String senderImg;
    public static String receiverIImg;



    String senderRoom;
    String receiverRoom;
    RecyclerView mmessagesRecyclerView;
    ArrayList<msgModelclass> messagesList;
    messagesAdpter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);
        getSupportActionBar().hide();




        mmessagesRecyclerView = findViewById(R.id.msgadpter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagesRecyclerView.setLayoutManager(linearLayoutManager);
        messagesList = new ArrayList<>();
        messagesAdapter = new messagesAdpter(chatWin.this, messagesList);
        mmessagesRecyclerView.setAdapter(messagesAdapter);

        receiverName = getIntent().getStringExtra("nameeee");
        receiverImg = getIntent().getStringExtra("reciverImg");
        receiverUid = getIntent().getStringExtra("Uid");

        profile = findViewById(R.id.profileimgg);
        textMsg = findViewById(R.id.textmsg);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        senderUid = firebaseAuth.getUid();
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        // Load sender's image
        loadSenderImage();

        chatReference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                    if (messages != null) {
                        messagesList.add(messages);
                    }
                }

                messagesAdapter.notifyDataSetChanged();

                // Scroll to the last message
                if (!messagesList.isEmpty()) {
                    mmessagesRecyclerView.scrollToPosition(messagesList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        findViewById(R.id.sendbtnn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textMsg.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendChatMessage(message);
                } else {
                    Toast.makeText(chatWin.this, "Enter the Message First", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadSenderImage() {
        DatabaseReference reference = database.getReference().child("user").child(senderUid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot profilePicSnapshot = snapshot.child("profilepic");

                    // Check if profilepic exists and is not null
                    if (profilePicSnapshot.exists() && profilePicSnapshot.getValue() != null) {
                        senderImg = profilePicSnapshot.getValue().toString();
                        receiverIImg = receiverImg;

                    }else {
                        senderImg = null;
                    }

                        // Load sender's image
                        if (senderImg != null) {
                            Picasso.get().load(senderImg).into(profile);
                        }else {
                            Picasso.get().load(R.drawable.man).into(profile);

                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void sendChatMessage(String message) {
        textMsg.setText("");
        Date date = new Date();
        msgModelclass messages = new msgModelclass(message, senderUid, date.getTime());

        DatabaseReference senderReference = chatReference.push();
        senderReference.setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference receiverReference = database.getReference().child("chats").child(receiverRoom).child("messages").push();
                receiverReference.setValue(messages)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Handle completion if needed
                            }
                        });
            }
        });
    }
}






//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



//package com.example.messenger;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class chatWin extends AppCompatActivity {
//
//    String reciverimg, reciverUid, reciverName, SenderUID;
//
//    CircleImageView profile;
//    EditText textmsg;
//    FirebaseAuth firebaseAuth;
//    FirebaseDatabase database;
//    DatabaseReference chatReference;
//
//    public static String senderIMG;
//    public static String reciverIIMG;
//
//    String senderRoom;
//    String reciverRoom;
//    RecyclerView mmessangesAdapter;
//    ArrayList<msgModelclass> messagessArrayList;
//    messagesAdpter messagesAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat_win);
//        getSupportActionBar().hide();
//
//        mmessangesAdapter = findViewById(R.id.msgadpter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);
//        mmessangesAdapter.setLayoutManager(linearLayoutManager);
//        messagessArrayList = new ArrayList<>();
//        messagesAdapter = new messagesAdpter(chatWin.this, messagessArrayList);
//        mmessangesAdapter.setAdapter(messagesAdapter);
//
//        reciverName = getIntent().getStringExtra("nameeee");
//        reciverimg = getIntent().getStringExtra("reciverImg");
//        reciverUid = getIntent().getStringExtra("Uid");
//
//        profile = findViewById(R.id.profileimgg);
//        textmsg = findViewById(R.id.textmsg);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//
//        SenderUID = firebaseAuth.getUid();
//        senderRoom = SenderUID + reciverUid;
//        reciverRoom = reciverUid + SenderUID;
//
//        // Load sender's image
//        loadSenderImage();
//
//        chatReference = database.getReference().child("chats").child(senderRoom).child("messages");
//
//        chatReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                messagessArrayList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
//                    messagessArrayList.add(messages);
//                }
//
//                messagesAdapter.notifyDataSetChanged();
//
//                // Scroll to the last message
//                if (messagessArrayList.size() > 0) {
//                    mmessangesAdapter.scrollToPosition(messagessArrayList.size() - 1);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle error
//            }
//        });
//
//        findViewById(R.id.sendbtnn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String message = textmsg.getText().toString().trim();
//                if (!message.isEmpty()) {
//                    sendChatMessage(message);
//                } else {
//                    Toast.makeText(chatWin.this, "Enter the Message First", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    private void loadSenderImage() {
//        DatabaseReference reference = database.getReference().child("user").child(SenderUID);
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    senderIMG = snapshot.child("profilepic").getValue().toString();
//                    reciverIIMG = reciverimg;
//
//                    // Load sender's image
//                    Picasso.get().load(senderIMG).into(profile);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle error
//            }
//        });
//    }
//
//    private void sendChatMessage(String message) {
//        textmsg.setText("");
//        Date date = new Date();
//        msgModelclass messagess = new msgModelclass(message, SenderUID, date.getTime());
//
//        chatReference.push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                database.getReference().child("chats").child(reciverRoom).child("messages").push().setValue(messagess)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                // Handle completion if needed
//                            }
//                        });
//            }
//        });
//    }
//}
//
//





