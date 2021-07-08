package com.saikalyandaroju.whatsappclone.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saikalyandaroju.whatsappclone.R;

public class ChatFragmentViewHolder extends RecyclerView.ViewHolder {
    TextView count,time,title,subtitle;
    ImageView prof;

    public ChatFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
        count=itemView.findViewById(R.id.countTv);
        time=itemView.findViewById(R.id.timeTv);
        title=itemView.findViewById(R.id.titleTv);
        subtitle=itemView.findViewById(R.id.subTitleTv);
        prof=itemView.findViewById(R.id.userImgView);

    }
}
