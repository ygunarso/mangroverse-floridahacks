package com.floridahacksibm.mangrovetracker;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PickupProcessAdapter extends FragmentStatePagerAdapter {
    private Context myContext;
    public PickupProcessAdapter(Context context, FragmentManager fm) {
        super(fm);
        myContext = context;
    }
    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:;
                return new NurseryMapFragment();
            case 1:
                return new SaplingCountFragment();
            case 2:
                return new OrderConfirmFragment();
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return 3;
    }
}
