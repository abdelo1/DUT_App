package com.example.blocnote.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blocnote.R;

public class Fragment_events extends Fragment {

    public Fragment_events(){}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =getLayoutInflater().inflate(R.layout.fragment_events,container,false);
        return view;
    }
}
