package com.floridahacksibm.mangrovetracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlantFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TabLayout plantTabs;
    ViewPager viewPager;

    public PlantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlantFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlantFragment newInstance(String param1, String param2) {
        PlantFragment fragment = new PlantFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        plantTabs = getView().findViewById(R.id.plantTabs);
        viewPager = getView().findViewById(R.id.viewPager);

        plantTabs.addTab(plantTabs.newTab().setText("Plant"));
        plantTabs.addTab(plantTabs.newTab().setText("Pick up"));

        final PlantAdapter adapter = new PlantAdapter(getContext(),getChildFragmentManager(), plantTabs.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(plantTabs));
        plantTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }

    private void setupRecyclerView() {
//        RecyclerView myTreesList = getView().findViewById(R.id.trees_list);
//        MyTreesAdapter adapter = new MyTreesAdapter(array, getActivity());
//        myTreesList.setAdapter(adapter);
//        myTreesList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }
}