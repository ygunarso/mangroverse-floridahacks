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

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderConfirmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderConfirmFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderConfirmFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderConfirmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderConfirmFragment newInstance(String param1, String param2) {
        OrderConfirmFragment fragment = new OrderConfirmFragment();
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
        return inflater.inflate(R.layout.fragment_order_confirm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConstraintLayout guideLayout = getView().findViewById(R.id.plantWithGuide);
        guideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guideLayout.setBackgroundResource(R.drawable.extras_selected);
                ImageView check = getView().findViewById(R.id.guideCheck);
                check.setImageResource(R.drawable.ic_check_selected);
                TextView totalPriceText = getView().findViewById(R.id.totalPrice);
                String text = totalPriceText.getText().toString();
                int current = Integer.parseInt(text.substring(1));
                String price = "$"+(current+40);
                totalPriceText.setText(price);
            }
        });

        ConstraintLayout kitsLayout = getView().findViewById(R.id.plantKits);
        kitsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kitsLayout.setBackgroundResource(R.drawable.extras_selected);
                ImageView check = getView().findViewById(R.id.kitsCheck);
                check.setImageResource(R.drawable.ic_check_selected);
                TextView totalPriceText = getView().findViewById(R.id.totalPrice);
                String text = totalPriceText.getText().toString();
                int current = Integer.parseInt(text.substring(1));
                String price = "$"+(current+40);
                totalPriceText.setText(price);
            }
        });

    }

}