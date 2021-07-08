package com.saikalyandaroju.whatsappclone.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.saikalyandaroju.whatsappclone.Activities.MainActivity;
import com.saikalyandaroju.whatsappclone.Adapters.ChatFragmentAdapter;
import com.saikalyandaroju.whatsappclone.Models.Inbox;
import com.saikalyandaroju.whatsappclone.R;

public class ChatsFragment extends androidx.fragment.app.Fragment {

    //variables.
    ChatFragmentAdapter userAdapter;
    private FirebaseAuth firebaseAuth;
    public static ChatsFragment chatsFragment;
    private Query query;
    private RecyclerView recyclerView;
    private String searchQuery = "";
    LinearLayout linearLayout;


    public ChatsFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
        query = FirebaseDatabase.getInstance().getReference().child("chats").child(firebaseAuth.getUid());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chats, container, false);

        chatsFragment = this;
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayout = view.findViewById(R.id.layout);
        FirebaseRecyclerOptions<Inbox> options = new FirebaseRecyclerOptions.Builder<Inbox>().setLifecycleOwner(getViewLifecycleOwner()).setQuery(query, Inbox.class).build();
        userAdapter = new ChatFragmentAdapter(options);

        initRecycler(view);
        recyclerView.setAdapter(userAdapter);


        ((MainActivity) getActivity()).getSearch().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                userAdapter.submitData(s);
                userAdapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {

        } catch (ClassCastException e) {
            Log.e("check", e.toString());
        }

    }


    private void initRecycler(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public static ChatsFragment getInstance() {
        if (chatsFragment == null) {
            chatsFragment = new ChatsFragment();
        }
        return chatsFragment;
    }


}
