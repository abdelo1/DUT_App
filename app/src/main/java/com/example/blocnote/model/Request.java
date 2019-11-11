package com.example.blocnote.model;

public class Request {
    //celui qui recoit
    private String receiverId;
    //celui qui envoit
    private String senderId;
    private String accepted;
    private String handle;
    private String nom;
    private String photourl;
    private  String filiere;
    public void setName(String nom) {
        this.nom = nom;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getNom() {
        return this.nom;
    }

    public Request(String receiverId, String senderId, String accepted, String handle, String nom, String photourl, String filiere) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.accepted = accepted;
        this.handle = handle;
        this.nom = nom;
        this.photourl = photourl;
        this.filiere = filiere;
    }

    public String getFiliere() {
        return this.filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getPhotourl() {
        return this.photourl;
    }



    public Request(){}

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getReceiverId() {
        return this.receiverId;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public String getAccepted() {
        return this.accepted;
    }

    public String getHandle() {
        return this.handle;
    }
}
