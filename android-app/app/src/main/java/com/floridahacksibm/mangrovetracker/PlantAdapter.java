package com.floridahacksibm.mangrovetracker;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PlantAdapter extends FragmentStatePagerAdapter {
    private Context myContext;
    int totalTabs;
    public PlantAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }
    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PlantHomeFragment plantHomeFragment = new PlantHomeFragment();
                return plantHomeFragment;
            case 1:
                PickupHomeFragment pickupHomeFragment = new PickupHomeFragment();
                return pickupHomeFragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}