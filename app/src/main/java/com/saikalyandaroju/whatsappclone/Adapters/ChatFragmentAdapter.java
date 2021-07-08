package com.saikalyandaroju.whatsappclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.saikalyandaroju.whatsappclone.Activities.ChatActivity;
import com.saikalyandaroju.whatsappclone.Fragments.ChatsFragment;
import com.saikalyandaroju.whatsappclone.Models.Inbox;
import com.saikalyandaroju.whatsappclone.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class ChatFragmentAdapter extends FirebaseRecyclerAdapter<Inbox, RecyclerView.ViewHolder> {

    private Context context;
    private String searchQuery = "";
    private SharedPreferences sharedPreferences;



    public ChatFragmentAdapter(@NonNull FirebaseRecyclerOptions<Inbox> options) {
        super(options);


    }


    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull final Inbox model) {
        if (holder instanceof ChatFragmentViewHolder) {
            ChatFragmentViewHolder holder1 = (ChatFragmentViewHolder) holder;

            bindData(holder1, model);

        }
    }

    public void submitData(String s) {
        this.searchQuery = s;
    }

    private void bindData(ChatFragmentViewHolder holder, final Inbox model) {
        if (model.getCount() > 0) {
            holder.count.setText(model.getCount() + "");
        } else {
            holder.count.setVisibility(View.GONE);
        }
        // holder.time.setText(format(model.getTime()));
        setDate(model, holder);
        holder.title.setText(model.getName());
        holder.subtitle.setText(model.getMsg());
        if (!model.getImage().isEmpty()) {
            Picasso.get().load(model.getImage()).placeholder(R.drawable.ic_person_black_24dp).error(R.drawable.ic_person_black_24dp).into(holder.prof);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("inbox", model);
                context.startActivity(intent);
            }
        });

    }

    private void setDate(Inbox model, ChatFragmentViewHolder holder) {

        Log.i("date", model.getTime().toString());


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));

        try {
            //Date date=new Date(top.getcreated());

            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("IST"));
            Date currentdate = c.getTime();
            Date date = model.getTime();

            Log.i("date", date.toString() + "   " + currentdate.toString());
            if (currentdate.getDate() == date.getDate()) {
                holder.time.setText(getTime(date));
            } else {
                holder.time.setText(getDate(date));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (searchQuery.equals("")||searchQuery.trim().equals("")) {
            return 1;
        }

        Inbox chat = getItem(position);

        if (chat.getName().toLowerCase().trim().contains(searchQuery.toLowerCase().trim())) {
            return 1;
        } else {
            return 2;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        sharedPreferences =context.getSharedPreferences("FCM_TOKEN", MODE_PRIVATE);
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ChatFragmentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_item, parent, false);
            return new EmptyViewHolder(view);
        }
    }

    public String format(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH : mm");
        return simpleDateFormat.format(date);
    }

    public String getDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM ");
        return simpleDateFormat.format(date);
    }

    public String getTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
        return simpleDateFormat.format(date);
    }


}
