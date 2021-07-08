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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.saikalyandaroju.whatsappclone.Activities.MainActivity;
import com.saikalyandaroju.whatsappclone.Adapters.PeopleAdapter;
import com.saikalyandaroju.whatsappclone.Models.User;
import com.saikalyandaroju.whatsappclone.R;

public class PeopleFragment extends Fragment  {
    PeopleAdapter userAdapter;
    private FirebaseAuth firebaseAuth;
    private Query query;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;

    public PeopleFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
        query = FirebaseFirestore.getInstance().collection("Users").orderBy("name", Query.Direction.DESCENDING);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chats, container, false);
        return v;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {

        } catch (ClassCastException e) {
            Log.e("check", e.toString());
        }

    }





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayout=view.findViewById(R.id.layout);
        setUpPaging();
        initRecycler(view);



        ((MainActivity) getActivity()).getSearch().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                userAdapter.submitData(s);
                userAdapter.notifyDataSetChanged();
            }
        });


    }

    private void initRecycler(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(userAdapter);
    }

    private void setUpPaging() {
        PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(10).setPrefetchDistance(2).build();
        FirestorePagingOptions<User> firestorePagingOptions=new FirestorePagingOptions.Builder<User>().setLifecycleOwner(getViewLifecycleOwner())
                .setQuery(query,config,User.class).build();
        //FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        userAdapter = new PeopleAdapter(firestorePagingOptions);
    }





}
