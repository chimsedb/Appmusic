package com.example.hungdang.musicapp.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdapterVP extends FragmentStatePagerAdapter {

    ArrayList<Fragment> arrfm ;

    public AdapterVP(FragmentManager fm,ArrayList<Fragment> arrfm) {
        super(fm);
        this.arrfm= arrfm;
    }

    @Override
    public Fragment getItem(int i) {
        return arrfm.get(i);
    }

    @Override
    public int getCount() {
        return arrfm.size();
    }
}
