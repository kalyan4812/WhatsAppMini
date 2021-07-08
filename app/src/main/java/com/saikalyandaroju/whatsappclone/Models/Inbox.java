package com.saikalyandaroju.whatsappclone.Models;

import java.io.Serializable;
import java.util.Date;

public class Inbox implements Serializable {
    private String name, frm, msg, image;
    private Date time;
    private int count;

    public Inbox() {
        this.name = "";
        this.frm = "";
        this.msg = "";
        this.image = "";
        this.time = new Date();
        this.count = 0;
    }

    public Inbox(String msg, String frm, String name, String image, int count) {
        this.name = name;
        this.frm = frm;
        this.msg = msg;
        this.image = image;
        this.count = count;
    }
 /*   public Inbox( String frm, String name, String image, int count) {
        this.name = name;
        this.frm = frm;

        this.image = image;
        this.count = count;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrm() {
        return frm;
    }

    public void setFrm(String frm) {
        this.frm = frm;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Inbox(String name, String frm, String msg, String image, Date time, int count) {
        this.name = name;
        this.frm = frm;
        this.msg = msg;
        this.image = image;
        this.time = time;
        this.count = count;
    }
}
