package com.floridahacksibm.mangrovetracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class NurseryMapAdapter extends RecyclerView.Adapter<NurseryMapAdapter.ViewHolder>{
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nurseryName;
        public TextView nurseryAddress;
        public TextView openText;
        public TextView hoursText;
        public TextView redCount;
        public TextView blackCount;
        public TextView whiteCount;
        public ConstraintLayout nurseryCard;
        public ImageView nurseryIcon;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nurseryName = itemView.findViewById(R.id.nurseryName);
            nurseryAddress = itemView.findViewById(R.id.nurseryAddress);
            openText = itemView.findViewById(R.id.openText);
            hoursText = itemView.findViewById(R.id.hoursText);
            redCount = itemView.findViewById(R.id.redCount);
            blackCount = itemView.findViewById(R.id.blackCount);
            whiteCount = itemView.findViewById(R.id.whiteCount);
            nurseryCard = itemView.findViewById(R.id.nurseryCard);
            nurseryIcon = itemView.findViewById(R.id.nurseryIcon);
        }
    }

    // Store a member variable for the contacts
    private JSONArray mArray;
    private Context mContext;
    private NurseryMapFragment fragment;

    // Pass in the contact array into the constructor
    public NurseryMapAdapter(JSONArray array, Context c) {
        mContext = c;
        mArray = array;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public NurseryMapAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View nurseryView = inflater.inflate(R.layout.item_nursery, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(nurseryView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(NurseryMapAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        try {
            JSONObject nursery = mArray.getJSONObject(position);
            // Set item views based on your views and data model
            TextView nurseryName_tv = holder.nurseryName;
            nurseryName_tv.setText(nursery.getString("name"));
            TextView nurseryAddress_tv = holder.nurseryAddress;
            nurseryAddress_tv.setText(nursery.getString("address"));
            TextView openText_tv = holder.openText;
            openText_tv.setText("Open");
            JSONObject availableSaps = nursery.getJSONObject("available");
            TextView redCount_tv = holder.redCount;

            redCount_tv.setText(Integer.toString(availableSaps.getInt("red")));
            TextView blackCount_tv = holder.blackCount;
            blackCount_tv.setText(Integer.toString(availableSaps.getInt("black")));
            TextView whiteCount_tv = holder.whiteCount;
            whiteCount_tv.setText(Integer.toString(availableSaps.getInt("white")));

            holder.nurseryCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConstraintLayout card = holder.nurseryCard;
                    card.setBackgroundResource(R.drawable.green_box);
                    Activity parent = (Activity) mContext;
                    ConstraintLayout nextButton = parent.findViewById(R.id.bottomButton);
                    nextButton.setBackgroundResource(R.drawable.plant_process_button);
                    ImageView icon = holder.nurseryIcon;
                    icon.setBackgroundResource(R.drawable.ic_nursery_map_selected);
//                    ArrayList<Marker> test = fragment.getMarkers();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArray.length();
    }
}
