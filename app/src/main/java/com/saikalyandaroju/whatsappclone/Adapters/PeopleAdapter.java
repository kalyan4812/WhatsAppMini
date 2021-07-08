package com.saikalyandaroju.whatsappclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.auth.FirebaseAuth;
import com.saikalyandaroju.whatsappclone.Activities.ChatActivity;
import com.saikalyandaroju.whatsappclone.R;
import com.saikalyandaroju.whatsappclone.Models.User;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

public class PeopleAdapter extends FirestorePagingAdapter<User, RecyclerView.ViewHolder> {

    private Context context;
    private String searchQuery = "";
    private SharedPreferences sharedPreferences;

    public PeopleAdapter(@NonNull FirestorePagingOptions<User> options) {
        super(options);
    }

    public void submitData(String s) {
        this.searchQuery = s;
    }

    @Override
    protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position, @NonNull final User model) {
        if (holder instanceof PeopleViewHolder) {
            PeopleViewHolder holder1 = (PeopleViewHolder) holder;


            holder1.title.setText(model.getName());
            holder1.subtitle.setText(model.getStatus());
            Picasso.get().load(model.getThumbImage()).placeholder(R.drawable.ic_person_black_24dp).error(R.drawable.ic_person_black_24dp).into(holder1.prof);
            holder1.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("user", model);
                    context.startActivity(intent);
                }
            });


        } else {
            //
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sharedPreferences =context.getSharedPreferences("FCM_TOKEN", MODE_PRIVATE);
        if (viewType == 2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new PeopleViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_item, parent, false);
            return new EmptyViewHolder(view);
        }
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
    }

    @Override
    protected void onError(@NonNull Exception e) {
        super.onError(e);
    }

    @Override
    public int getItemViewType(int position) {
        User user = getItem(position).toObject(User.class);
        if (user != null && user.getUserId() != null){
            Log.i("check",user.getUserId());
        }

        if (user != null && user.getUserId() != null && user.getUserId().toString().equals(FirebaseAuth.getInstance().getUid().toString())) {
            Log.i("check","current user");
            return 1;
        }
        if (searchQuery.equals("") || searchQuery.equals(" ")) {
            return 2;
        }


        if (user != null && !(user.getName().toLowerCase().trim().contains(searchQuery.toLowerCase().trim()))) {
            return 1;
        } else {
            return 2;
        }
        // return super.getItemViewType(position);
    }
}
