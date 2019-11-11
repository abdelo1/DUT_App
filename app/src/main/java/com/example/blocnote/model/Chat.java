package com.example.blocnote.model;

public class Chat {

    private String message;
    private String senderid,receiverid;
    private String time;
    private long timeStamp;
public Chat(){}
    public Chat(String message, String senderid, String receiverid,String time,long timeStamp) {
        this.message = message;
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.timeStamp=timeStamp;
         this.time=time;


    }
    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }

}
