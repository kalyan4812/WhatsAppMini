package com.saikalyandaroju.whatsappclone.Models;

import com.saikalyandaroju.whatsappclone.Models.ChatEvent;

import java.util.Date;

public class Message implements ChatEvent {
    private String msg, senderId, msgId;
    private int status = 1;
    private Boolean liked = false;
    private String type = "TEXT";
    private Date sentAt;

    public Message() {

    }

    public Message(String msg, String senderId, String msgId) {
        this.msg = msg;
        this.senderId = senderId;
        this.msgId = msgId;
        this.liked = false;
        this.status = 1;
        this.type = "TEXT";
        this.sentAt = new Date();
    }

    public Message(String msg, String senderId, String msgId, int status, Boolean liked, String type) {
        this.msg = msg;
        this.senderId = senderId;
        this.msgId = msgId;
        this.status = status;
        this.liked = liked;
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
}

