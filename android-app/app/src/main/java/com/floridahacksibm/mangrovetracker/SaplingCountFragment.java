package com.floridahacksibm.mangrovetracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaplingCountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaplingCountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SaplingCountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SaplingCountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaplingCountFragment newInstance(String param1, String param2) {
        SaplingCountFragment fragment = new SaplingCountFragment();
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
        return inflater.inflate(R.layout.fragment_sapling_count, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView redValue = getView().findViewById(R.id.redValue);
        ImageView redIncrement = getView().findViewById(R.id.redIncrement);
        ImageView redDecrement = getView().findViewById(R.id.redDecrement);
        redIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.parseInt(redValue.getText().toString());
                if (current <= 0) {
                    redDecrement.setColorFilter(getResources().getColor(R.color.black));
                }
                int newValue = current+1;
                redValue.setText(String.valueOf(newValue));
            }
        });
        redDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.parseInt(redValue.getText().toString());
                int newValue = current-1;
                if (newValue <= 0) {
                    redDecrement.setColorFilter(getResources().getColor(R.color.gray));
                }
                if (current > 0) {
                    redValue.setText(String.valueOf(newValue));
                }
            }
        });

        TextView blackValue = getView().findViewById(R.id.blackValue);
        ImageView blackIncrement = getView().findViewById(R.id.blackIncrement);
        ImageView blackDecrement = getView().findViewById(R.id.blackDecrement);
        blackIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.parseInt(blackValue.getText().toString());
                if (current <= 0) {
                    blackDecrement.setColorFilter(getResources().getColor(R.color.black));
                }
                int newValue = current+1;
                blackValue.setText(String.valueOf(newValue));
            }
        });
        blackDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.parseInt(blackValue.getText().toString());
                int newValue = current-1;
                if (newValue <= 0) {
                    blackDecrement.setColorFilter(getResources().getColor(R.color.gray));
                }
                if (current > 0) {
                    blackValue.setText(String.valueOf(newValue));
                }
            }
        });

        TextView whiteValue = getView().findViewById(R.id.whiteValue);
        ImageView whiteIncrement = getView().findViewById(R.id.whiteIncrement);
        ImageView whiteDecrement = getView().findViewById(R.id.whiteDecrement);
        whiteIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.parseInt(whiteValue.getText().toString());
                if (current <= 0) {
                    whiteDecrement.setColorFilter(getResources().getColor(R.color.black));
                }
                int newValue = current+1;
                whiteValue.setText(String.valueOf(newValue));
            }
        });
        whiteDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.parseInt(whiteValue.getText().toString());
                int newValue = current-1;
                if (newValue <= 0) {
                    whiteDecrement.setColorFilter(getResources().getColor(R.color.gray));
                }
                if (current > 0) {
                    whiteValue.setText(String.valueOf(newValue));
                }
            }
        });
    }

}