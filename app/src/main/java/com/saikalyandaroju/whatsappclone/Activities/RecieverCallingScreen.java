package com.saikalyandaroju.whatsappclone.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.saikalyandaroju.whatsappclone.R;
import com.saikalyandaroju.whatsappclone.Utils.Constants;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

public class RecieverCallingScreen extends AppCompatActivity {
    AppCompatImageButton cancelCall, acceptCall;
    TextView name, subtitle;
    String recievetoken;
    String meetingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever_calling_screen);
        setUpIds();
        recieveData();
    }

    private void recieveData() {
        meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);
        recievetoken=getIntent().getStringExtra(REMOTE_MSG_INVITER_TOKEN);
        if (meetingType != null) {
            if (meetingType.equals("video")) {
                name.setText(getIntent().getStringExtra(Constants.REMOTE_MSG_NAME));
                subtitle.setText("Incoming " + meetingType+ " call...");
            }
            else if(meetingType.equals("audio")){
                name.setText(getIntent().getStringExtra(Constants.REMOTE_MSG_NAME));
                subtitle.setText("Incoming " + meetingType + " call...");
            }
        }
        cancelCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationResponse(REMOTE_MSSG_INVITATION_REJECTED, recievetoken);
                startActivity(new Intent(RecieverCallingScreen.this, MainActivity.class));
            }
        });
        acceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),recievetoken,Toast.LENGTH_SHORT).show();
                sendInvitationResponse(REMOTE_MSG_INVITATION_ACCEPTED, recievetoken);
            }
        });
    }

    private void setUpIds() {
        cancelCall = findViewById(R.id.button2);
        acceptCall = findViewById(R.id.button);
        name = findViewById(R.id.textView2);
        subtitle = findViewById(R.id.textView3);


    }

    private void sendInvitationResponse(String type, String recieverToken) {
        try {
            JSONArray token = new JSONArray();
            token.put(recieverToken);
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION_RESPONSE);
            data.put(REMOTE_MSG_INVITATION_RESPONSE, type);

            body.put(REMOTE_MSG_DATA, data);
            body.put(REMOTE_MSG_REGISTRATION_IDS, token);
            sendNotification(body, type);

        } catch (JSONException e) {

        }
    }


    private void sendNotification(JSONObject body, String type) {
        JsonObjectRequest myrequest = new JsonObjectRequest(api_link, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (type.equals(REMOTE_MSG_INVITATION_ACCEPTED)) {
                    //Toast.makeText(getApplicationContext(), "call accepted", Toast.LENGTH_SHORT).show();

                    try {
                        URL serverURL = new URL("https://meet.jit.si");
                        JitsiMeetConferenceOptions.Builder conferenceOptions = new JitsiMeetConferenceOptions.Builder()
                                .setServerURL(serverURL).setWelcomePageEnabled(false)
                                .setRoom(getIntent().getStringExtra(REMOTE_MSG_MEETING_ROOM))
                                ;
                        if(meetingType.equals("audio")){
                            conferenceOptions.setVideoMuted(true);
                        }
                        JitsiMeetActivity.launch(RecieverCallingScreen.this, conferenceOptions.build());
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else if (type.equals(REMOTE_MSSG_INVITATION_REJECTED)) {
                    Toast.makeText(getApplicationContext(), "call rejected", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
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

    private BroadcastReceiver invitationResponseReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(REMOTE_MSG_INVITATION_CANCELLED)) {
                    Toast.makeText(getApplicationContext(), "Invitation Cancelled", Toast.LENGTH_SHORT).show();
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