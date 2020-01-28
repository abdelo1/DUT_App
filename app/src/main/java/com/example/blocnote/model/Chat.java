package com.example.blocnote.model;

public class Chat {

    private String message;
    private String senderid,receiverid;
    private String time;
    private long timestamp;
public Chat(){}
    public Chat(String message, String senderid, String receiverid,String time,long timestamp) {
        this.message = message;
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.timestamp=timestamp;
         this.time=time;


    }
    public long getTimeStamp() {
        return this.timestamp;
    }

    public void setTimeStamp(long timestamp) {
        this.timestamp = timestamp;
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
