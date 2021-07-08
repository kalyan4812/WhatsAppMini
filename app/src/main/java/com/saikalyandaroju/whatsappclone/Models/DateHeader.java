package com.saikalyandaroju.whatsappclone.Models;

import android.content.Context;

import com.saikalyandaroju.whatsappclone.Models.ChatEvent;

import java.util.Date;

public class DateHeader implements ChatEvent {
    private String date;
    private Date sentAt;
    private Context context;

    public DateHeader(Date sentAt, Context context) {
        this.sentAt = sentAt;
        this.context = context;
        this.date=getSentAt().toString();
    }

    public String getDate() {
        return date;
    }

    @Override
    public Date getSentAt() {
        return sentAt;
    }

    public Context getContext() {
        return context;
    }
}
