package com.example.blocnote.model;

public class Friend {
    private String urlphoto;
    private  String nom;
    private String filiere;
    private  String id;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFiliere() {
        return this.filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public Friend( ) {

    }

    public Friend(String urlphoto, String nom) {
        this.urlphoto = urlphoto;
        this.nom = nom;
    }

    public String getUrlphoto() {
        return this.urlphoto;
    }

    public void setUrlphoto(String urlphoto) {
        this.urlphoto = urlphoto;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
