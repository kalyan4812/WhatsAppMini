package com.saikalyandaroju.whatsappclone.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.saikalyandaroju.whatsappclone.Adapters.ChatActivityAdapter;
import com.saikalyandaroju.whatsappclone.Models.ChatEvent;
import com.saikalyandaroju.whatsappclone.Models.DateHeader;
import com.saikalyandaroju.whatsappclone.Models.Inbox;
import com.saikalyandaroju.whatsappclone.Models.Message;
import com.saikalyandaroju.whatsappclone.Models.User;
import com.saikalyandaroju.whatsappclone.R;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements ChatActivityAdapter.HighFiveClickListener {
    TextView name, onlineTv;
    ShapeableImageView shapeableImageView;
    EmojiPopup emojiPopup;
    EmojiEditText emojiEditText;
    RelativeLayout relativeLayout;
    private String currentUserId = FirebaseAuth.getInstance().getUid();
    private FirebaseDatabase firebaseDatabase;
    ImageView send;
    String friendId;
    String names, images;
    private User currentUser;
    List<ChatEvent> chatEvents = new ArrayList<>();
    private ChatActivityAdapter chatAdapter;
    RecyclerView recyclerView;
    ChildEventListener valueEventListener;
    Query query;
    ImageView smile;
    SwipeRefreshLayout swipeRefreshLayout;
    String currentUserName;
    SharedPreferences sharedPreferences;
    String friendToken;
    String api_links = "https://oakspro.com/projects/project36/kalyan/Notifications/send_notification.php";

    User freind_user;
    AppCompatImageButton videocall, voicecall;
     MaterialToolbar materialToolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new GoogleEmojiProvider());
        setContentView(R.layout.activity_chat);
        materialToolbar=findViewById(R.id.toolbar);
        firebaseDatabase = FirebaseDatabase.getInstance();
        swipeRefreshLayout = findViewById(R.id.swipeToLoad);
        setTitle("");
        setSupportActionBar(materialToolbar);
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
        sharedPreferences = getApplicationContext().getSharedPreferences("FCM_TOKEN", MODE_PRIVATE);
        send = findViewById(R.id.sendBtn);
        recyclerView = findViewById(R.id.msgRv);
        smile = findViewById(R.id.smileBtn);
        relativeLayout = (RelativeLayout) findViewById(R.id.rootView);
        name = findViewById(R.id.nameTv);
        onlineTv = findViewById(R.id.onlineTv);
        emojiEditText = findViewById(R.id.msgEdtv);
        videocall = findViewById(R.id.videoCall);
        voicecall = findViewById(R.id.voiceCall);
        emojiPopup = EmojiPopup.Builder.fromRootView(relativeLayout).build(emojiEditText);
        shapeableImageView = findViewById(R.id.userImgView);
        chatAdapter = new ChatActivityAdapter(chatEvents, currentUserId);
        chatAdapter.setHighFiveListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        currentUserName = sharedPreferences.getString("username", "");
        //loadMessages();
        // listenToMessages();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                chatAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        smile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.toggle();
            }
        });
        if (getIntent() != null && getIntent().getSerializableExtra("user") != null) {
            User mUser = (User) getIntent().getSerializableExtra("user");

            friendId = mUser.getUserId();
            names = mUser.getName();
            images = mUser.getThumbImage();
            name.setText(mUser.getName());

            Picasso.get().load(mUser.getThumbImage()).error(R.drawable.ic_person_black_24dp).placeholder(R.drawable.ic_person_black_24dp).into(shapeableImageView);

        } else if (getIntent() != null && getIntent().getSerializableExtra("inbox") != null) {
            Inbox user = (Inbox) getIntent().getSerializableExtra("inbox");
            friendId = user.getFrm();
            names = user.getName();
            images = user.getImage();
            name.setText(user.getName());
            if (!user.getImage().isEmpty())
                Picasso.get().load(user.getImage()).error(R.drawable.ic_person_black_24dp).placeholder(R.drawable.ic_person_black_24dp).into(shapeableImageView);

        }
        FirebaseFirestore.getInstance().collection("Users").document(friendId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (value.exists()) {
                    User user = (User) value.toObject(User.class);
                    freind_user = user;
                    friendToken = user.getDeviceToken();
                    if (user.getOnlineStatus().equals("Online")) {
                        onlineTv.setVisibility(View.VISIBLE);
                        onlineTv.setText("Online");
                    } else {
                        onlineTv.setVisibility(View.INVISIBLE);
                    }

                }
            }

        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojiEditText.getText().toString().length() > 0) {
                    sendMessage(emojiEditText.getText().toString());

                    if (!onlineTv.getText().toString().equals("Online") || onlineTv.getVisibility() == View.INVISIBLE) {
                        sendfromserver(emojiEditText.getText().toString(), currentUserName, friendToken);
                    }
                    emojiEditText.setText("");

                }
            }
        });
        FirebaseFirestore.getInstance().collection("Users").document(currentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
            }
        });
       listenToMessages();
        videocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (freind_user != null) {
                    Intent intent = new Intent(ChatActivity.this, SendingCallingScreen.class);
                    intent.putExtra("user", freind_user);
                    intent.putExtra("type", "video");
                    startActivity(intent);
                }

            }
        });

        /// swipeRefreshLayout.setRefreshing(false);
        markAsRead();

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void sendfromserver(final String msg, final String currentUserName, final String friendToken) {
        Log.i("response", msg + "\n" + currentUserName + "\n" + friendToken);
        StringRequest request = new StringRequest(Request.Method.POST, api_links, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("response", "onResponse: " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Volley: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fcm_token", friendToken);
                params.put("title", currentUserName);
                params.put("message", msg);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    private void sendMessage(String msg) {
        String id = getMessages(friendId).push().getKey();
        if (id != null) {
            Message message = new Message(msg, currentUserId, id);
            getMessages(friendId).child(id).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });

            updateLastMessage(message);
        }
    }
   /* public void loadMessages(){
        query=getMessages(friendId).orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Message message=(Message)dataSnapshot.getValue(Message.class);
                    addMessage(message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void updateLastMessage(final Message message) {
        final Inbox inbox = new Inbox(names, friendId, message.getMsg().toString(), images, message.getSentAt(), 0);
        getInbox(currentUserId, friendId).setValue(inbox).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getInbox(friendId, currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Inbox inb = (Inbox) snapshot.getValue(Inbox.class);
                        inbox.setFrm(message.getSenderId());
                        inbox.setName(currentUser.getName());
                        inbox.setImage(currentUser.getThumbImage());
                        inbox.setCount(1);


                        if (inb != null && inb.getFrm().equals(message.getSenderId())) {
                            inbox.setCount(inb.getCount() + 1);
                        }

                        getInbox(friendId, currentUserId).setValue(inbox);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private DatabaseReference getMessages(String friendId) {
        return firebaseDatabase.getReference().child("messages/" + getId(friendId));
    }

    private DatabaseReference getInbox(String toUser, String fromUser) {
        return firebaseDatabase.getReference().child("chats/" + toUser + "/" + fromUser);
    }

    private String getId(String friendId) {
        if (friendId != null && friendId.compareTo(currentUserId) > 0) {
            return currentUserId + friendId;
        } else {
            return friendId + currentUserId;
        }
    }

    private void markAsRead() {
        Map map=new HashMap();
        map.put("count",0);
        getInbox(currentUserId, friendId).updateChildren(map);
    }

    private void listenToMessages() {

        query = getMessages(friendId).orderByKey();
        valueEventListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = (Message) snapshot.getValue(Message.class);
                addMessage(message);
                Log.i("msg", message.getMsg());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // chatAdapter.notifyDataSetChanged();
                Message message = (Message) snapshot.getValue(Message.class);
                Log.i("msgs", message.getMsg());
                updateMessage(message);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("msgs", error.getMessage());
            }
        });

    }

    private void updateMessage(Message message) {

        int position = 0;
        for (int i = 0; i < chatEvents.size(); i++) {
            if (chatEvents.get(i) instanceof Message) {
                if (((Message) chatEvents.get(i)).getMsgId() == message.getMsgId()) {
                    position = i;
                    break;
                }
            }
        }
        chatEvents.set(position, message);
      Log.i("pos",position+"");

        chatAdapter.notifyItemChanged(position);
    }

    private void addMessage(Message message) {
        ChatEvent chatEvent;
        if (chatEvents.size() > 0) {
            chatEvent = chatEvents.get(chatEvents.size() - 1);
        } else {
            chatEvent = null;
        }
        if (chatEvent == null || !isSameDayAs(chatEvent.getSentAt(), message.getSentAt())) {
            chatEvents.add(new DateHeader(message.getSentAt(), ChatActivity.this));
        }
        chatEvents.add(message);
        chatAdapter.notifyItemInserted(chatEvents.size() - 1);

        recyclerView.scrollToPosition(chatEvents.size() - 1);

    }

    public static Boolean isToday(Date date) {
        return DateUtils.isToday(date.getTime());
    }

    public static Boolean isSameDayAs(Date date, Date date2) {
        return date.getDay() == date2.getDay();
    }

    @Override
    public void highFiveClick(String id, Boolean status) {
        updateHighFive(id, status);
    }

    private void updateHighFive(String id, Boolean status) {
        Map map = new HashMap();
        map.put("liked", status);
        getMessages(friendId).child(id).updateChildren(map);

    }


    public void voiceCall(View view) {
        if (freind_user != null) {
            Intent intent = new Intent(ChatActivity.this, SendingCallingScreen.class);
            intent.putExtra("user", freind_user);
            intent.putExtra("type", "audio");
            startActivity(intent);
        }
    }

}
