package com.saikalyandaroju.whatsappclone.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.saikalyandaroju.whatsappclone.Activities.RecieverCallingScreen;
import com.saikalyandaroju.whatsappclone.Fragments.ChatsFragment;
import com.saikalyandaroju.whatsappclone.R;

import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_INVITATION;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_INVITATION_RESPONSE;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_INVITER_TOKEN;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_MEETING_ROOM;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_MEETING_TYPE;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_NAME;
import static com.saikalyandaroju.whatsappclone.Utils.Constants.REMOTE_MSG_TYPE;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    NotificationManager notificationManager;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sharedPreferences = getApplicationContext().getSharedPreferences("FCM_TOKEN", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("deviceToken", s).apply();
        if (FirebaseAuth.getInstance().getUid() != null) {
            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid()).update("deviceToken", s)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("check", "success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("check", "failed");
                }
            });
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        String type = remoteMessage.getData().get(REMOTE_MSG_TYPE);
        if (type != null) {
            if (type.equals(REMOTE_MSG_INVITATION)) {
                Intent intent = new Intent(getApplicationContext(), RecieverCallingScreen.class);
                intent.putExtra(REMOTE_MSG_MEETING_TYPE, remoteMessage.getData().get(REMOTE_MSG_MEETING_TYPE));
                intent.putExtra(REMOTE_MSG_NAME, remoteMessage.getData().get(REMOTE_MSG_NAME));
                intent.putExtra(REMOTE_MSG_INVITER_TOKEN, remoteMessage.getData().get(REMOTE_MSG_INVITER_TOKEN));
                intent.putExtra(REMOTE_MSG_INVITER_TOKEN, remoteMessage.getData().get(REMOTE_MSG_INVITER_TOKEN));
                intent.putExtra(REMOTE_MSG_MEETING_ROOM, remoteMessage.getData().get(REMOTE_MSG_MEETING_ROOM));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // since we are starting from non activity context.

                startActivity(intent);
            } else if (type.equals(REMOTE_MSG_INVITATION_RESPONSE)) {
                Log.i("checkmeet", type);
                Intent intent = new Intent(REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtra(REMOTE_MSG_INVITATION_RESPONSE, remoteMessage.getData().get(REMOTE_MSG_INVITATION_RESPONSE));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            }
        }
        else {
            manageNotification(remoteMessage);
        }

    }

    private void manageNotification(RemoteMessage remoteMessage) {
        notificationManager = (NotificationManager) getSystemService(ChatsFragment.chatsFragment.getActivity().NOTIFICATION_SERVICE);
        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        Log.i("check", title + " " + message);
        Intent resultIntent = new Intent(ChatsFragment.chatsFragment.getContext(), ChatsFragment.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ChatsFragment.chatsFragment.getContext(),
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        int notificationIdentity = 1;

        NotificationCompat.Builder n = new NotificationCompat.Builder(ChatsFragment.chatsFragment.getContext());


        n.setContentTitle(title)
                .setContentText(message);

        //content text is inner most one.
        n.setSmallIcon(R.drawable.ic_launcher_foreground);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);
        n.setLargeIcon(bitmap);


        n.setAutoCancel(true);
        n.setContentIntent(pendingIntent);
        n.setDefaults(NotificationCompat.DEFAULT_ALL);

        Uri ringtonepath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        n.setSound(ringtonepath);


        //if app is installed in Oreo device version 8and 8.1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "mychannel";
            NotificationChannel channel = new NotificationChannel(channelId, "GOOGLE Promotions", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            n.setChannelId(channelId);
        }

        notificationManager.notify(notificationIdentity, n.build());
    }
}
