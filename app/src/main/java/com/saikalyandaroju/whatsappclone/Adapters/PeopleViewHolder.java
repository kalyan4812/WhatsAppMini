package com.saikalyandaroju.whatsappclone.Adapters;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saikalyandaroju.whatsappclone.R;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

public class PeopleViewHolder extends RecyclerView.ViewHolder {

    TextView count, time, title, subtitle;
    ImageView prof;



    public PeopleViewHolder(@NonNull View itemView) {
        super(itemView);
        count = itemView.findViewById(R.id.countTv);
        count.setVisibility(View.GONE);
        time = itemView.findViewById(R.id.timeTv);
        time.setVisibility(View.GONE);
        title = itemView.findViewById(R.id.titleTv);
        subtitle = itemView.findViewById(R.id.subTitleTv);
        prof = itemView.findViewById(R.id.userImgView);



    }
}
