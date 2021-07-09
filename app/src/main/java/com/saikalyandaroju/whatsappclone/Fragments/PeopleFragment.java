package com.saikalyandaroju.whatsappclone.Fragments;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.saikalyandaroju.whatsappclone.Activities.MainActivity;
import com.saikalyandaroju.whatsappclone.Adapters.PeopleAdapter;
import com.saikalyandaroju.whatsappclone.Models.Contacts;
import com.saikalyandaroju.whatsappclone.Models.User;
import com.saikalyandaroju.whatsappclone.R;

import java.util.HashMap;
import java.util.Map;

public class PeopleFragment extends Fragment  {
    PeopleAdapter userAdapter;
    private FirebaseAuth firebaseAuth;
    private Query query;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    Map<String, Contacts> mycontacts = new HashMap<>();

    public PeopleFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
        query = FirebaseFirestore.getInstance().collection("Users").orderBy("name", Query.Direction.DESCENDING);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public void onStart() {
        super.onStart();

    }

    private void askContactsPermission() {
        Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_CONTACTS).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if (permissionGrantedResponse.getPermissionName().equals(Manifest.permission.READ_CONTACTS)) {

                    getContacts();


                }
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                Toast.makeText(getContext(), "PERMISSION IS NEEDED ", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void getContacts() {

        mycontacts.clear();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null
                , null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phonenum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(phonenum.length()>=10) {
                if(phonenum.length()==10){
                    phonenum="+91"+phonenum;
                }
                Contacts contacts = new Contacts(name, phonenum);


                if (!mycontacts.containsKey(contacts.getPhone())) {
                    mycontacts.put(contacts.getPhone(), contacts);
                }

                Log.i("check", contacts.getPhone());
            }

        }
        setUpPaging();







    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayout=view.findViewById(R.id.layout);

        askContactsPermission();

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
        if(mycontacts!=null)
        userAdapter.submitContacts(mycontacts);
    }





}
