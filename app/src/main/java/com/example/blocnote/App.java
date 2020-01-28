package com.example.blocnote;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {
    public App(){

    }
    public static void init(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

                                             }
