package com.saikalyandaroju.whatsappclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.saikalyandaroju.whatsappclone.Models.User;
import com.saikalyandaroju.whatsappclone.R;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_DATA;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_INVITATION;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_INVITATION_ACCEPTED;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_INVITATION_CANCELLED;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_INVITATION_RESPONSE;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_INVITER_TOKEN;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_MEETING_ROOM;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_MEETING_TYPE;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_NAME;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_REGISTRATION_IDS;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_TYPE;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSSG_INVITATION_REJECTED;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.api_link;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.contentType;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.serverkey;

public class SendingCallingScreen extends AppCompatActivity {
    AppCompatImageButton cancelCall;
    TextView name, subtitle;
    SharedPreferences sharedPreferences;
    String meetingRoom = null;
    String meetingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_calling_screen);
        initPrefrences();
        setUpIds();
        recieveData();

    }

    private void initPrefrences() {
        sharedPreferences = getApplicationContext().getSharedPreferences("FCM_TOKEN", MODE_PRIVATE);
    }

    private void recieveData() {
        if (getIntent() != null) {
            User user = (User) getIntent().getSerializableExtra("user");
            name.setText(user.getName());
             meetingType = getIntent().getStringExtra("type");
            subtitle.setText(meetingType + " Calling...");
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.i("token", "not succesful");
                        return;
                    }
                    arrangeMeeting(meetingType,user.getDeviceToken(),task.getResult().getToken());

                }
            });

            cancelCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // onBackPressed();
                    cancelInvitataion(user.getDeviceToken());
                }
            });
        }
    }

    private void arrangeMeeting(String meetingType, String deviceToken,String inviterToken) {
        Log.i("checkmeet",inviterToken);
        try {
            JSONArray token = new JSONArray();
            token.put(deviceToken);
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION);
            data.put(REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(REMOTE_MSG_NAME, sharedPreferences.getString("username", ""));
            data.put(REMOTE_MSG_INVITER_TOKEN, inviterToken);
            meetingRoom = FirebaseAuth.getInstance().getUid() + "_" + UUID.randomUUID().toString().substring(0, 5);
            data.put(REMOTE_MSG_MEETING_ROOM, meetingRoom);
            body.put(REMOTE_MSG_DATA, data);


            body.put(REMOTE_MSG_REGISTRATION_IDS, token);
            sendNotification(body, REMOTE_MSG_INVITATION);

        } catch (JSONException e) {

        }
    }

    private void setUpIds() {
        cancelCall = findViewById(R.id.button3);
        name = findViewById(R.id.textView2);
        subtitle = findViewById(R.id.textView3);


    }


    private void sendNotification(JSONObject body, String type) {
        JsonObjectRequest myrequest = new JsonObjectRequest(api_link, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (type.equals(REMOTE_MSG_INVITATION)) {
                    Toast.makeText(getApplicationContext(), "Call connecting...", Toast.LENGTH_SHORT).show();
                } else if (type.equals(REMOTE_MSG_INVITATION_RESPONSE)) {
                    Toast.makeText(getApplicationContext(), "call cancelled..", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("check", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverkey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        rq.add(myrequest);
    }

    private void cancelInvitataion(String recieverToken) {
        try {
            JSONArray token = new JSONArray();
            token.put(recieverToken);
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION_RESPONSE);
            data.put(REMOTE_MSG_INVITATION_RESPONSE, REMOTE_MSG_INVITATION_CANCELLED);

            body.put(REMOTE_MSG_DATA, data);
            body.put(REMOTE_MSG_REGISTRATION_IDS, token);
            sendNotification(body, REMOTE_MSG_INVITATION_RESPONSE);

        } catch (JSONException e) {

        }
    }

    private BroadcastReceiver invitationResponseReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(REMOTE_MSG_INVITATION_RESPONSE);
            Log.i("checkmeet",type);
            if (type != null) {
                if (type.equals(REMOTE_MSG_INVITATION_ACCEPTED)) {
                   // Toast.makeText(getApplicationContext(), "Invitation Accepted", Toast.LENGTH_SHORT).show();
                    try {
                        URL serverURL = new URL("https://meet.jit.si");
                        JitsiMeetConferenceOptions.Builder conferenceOptions = new JitsiMeetConferenceOptions.Builder()
                                .setServerURL(serverURL).setWelcomePageEnabled(false)
                                .setRoom(meetingRoom)
                                ;
                        if(meetingType.equals("audio")){
                            conferenceOptions.setVideoMuted(true);
                        }
                        JitsiMeetActivity.launch(SendingCallingScreen.this, conferenceOptions.build());
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else if (type.equals(REMOTE_MSSG_INVITATION_REJECTED)) {
                    Toast.makeText(getApplicationContext(), "Invitation Rejected", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(invitationResponseReciever,
                new IntentFilter(REMOTE_MSG_INVITATION_RESPONSE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(invitationResponseReciever);
    }
}