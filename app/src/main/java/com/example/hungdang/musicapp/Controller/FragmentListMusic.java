package com.example.hungdang.musicapp.Controller;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hungdang.musicapp.Adapter.AdapterRC;
import com.example.hungdang.musicapp.Interface.ChangeFragment;
import com.example.hungdang.musicapp.R;


import static com.example.hungdang.musicapp.Controller.MainActivity.listMusic;

public class FragmentListMusic extends Fragment {


    private RecyclerView recyclerView;

    ChangeFragment changeFragment ;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        changeFragment = (ChangeFragment) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.activity_listmusic, container, false);
        seControl(view);
        return view;
    }

    private void seControl(View view) {

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(new AdapterRC(listMusic, getContext()) {
            @Override
            protected void OnButtonClicked(int position) {
                changeFragment.ChangeFragment(position);
            }

        });

    }

}
