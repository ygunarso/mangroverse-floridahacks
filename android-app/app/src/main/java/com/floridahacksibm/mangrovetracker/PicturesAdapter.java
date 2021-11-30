package com.floridahacksibm.mangrovetracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.ViewHolder>{
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView treePictureView;
        public ConstraintLayout addPhotoView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            treePictureView = itemView.findViewById(R.id.treePicture);
            addPhotoView = itemView.findViewById(R.id.addPhotoImage);
        }
    }

    // Store a member variable for the contacts
    private JSONArray mArray;

    // Pass in the contact array into the constructor
    public PicturesAdapter(JSONArray array) {
        mArray = array;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public PicturesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View treeView = inflater.inflate(R.layout.item_picture, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(treeView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PicturesAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        try {
            String pictureURL = mArray.getString(position);

            // Set item views based on your views and data model
            ImageView image = holder.treePictureView;
            image.setRotation(90);
            if (pictureURL.equals("end")) {
                holder.addPhotoView.setVisibility(View.VISIBLE);
                return;
            }
            new PicturesAdapter.DownloadImageTask(image).execute(pictureURL);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArray.length();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
