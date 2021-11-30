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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MyTreesAdapter extends RecyclerView.Adapter<MyTreesAdapter.ViewHolder>{
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ImageView treeImageView;
        public ConstraintLayout treeCardView;
        public TextView levelTextView;
        public ImageView treeTypeView;
        public ImageView profilePicture;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.tree_name);
            treeImageView = itemView.findViewById(R.id.tree_image);
            treeCardView = itemView.findViewById(R.id.tree_card);
            levelTextView = itemView.findViewById(R.id.levelText);
            treeTypeView = itemView.findViewById(R.id.treeTypeIcon);
            profilePicture = itemView.findViewById(R.id.planterPicture);
        }
    }

    // Store a member variable for the contacts
    private JSONArray mArray;
    private Context mContext;

    // Pass in the contact array into the constructor
    public MyTreesAdapter(JSONArray array, Context c) {
        mContext = c;
        mArray = array;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public MyTreesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View treeView = inflater.inflate(R.layout.item_tree, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(treeView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(MyTreesAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        try {
            JSONObject tree = mArray.getJSONObject(position);
            // Set item views based on your views and data model
            TextView name_tv = holder.nameTextView;
            name_tv.setText(tree.getString("name"));
            ImageView type_iv = holder.treeTypeView;
            String type = tree.getString("special") + tree.getString("species");
            switch(type) {
                case "trueblack":
                    type_iv.setBackgroundResource(R.drawable.ic_tree_black_special);
                    break;
                case "falseblack":
                    type_iv.setBackgroundResource(R.drawable.ic_tree_black);
                    break;
                case "truered":
                    type_iv.setBackgroundResource(R.drawable.ic_tree_red_special);
                    break;
                case "falsered":
                    type_iv.setBackgroundResource(R.drawable.ic_tree_red);
                    System.out.println(tree.getString("name") + type);
                    break;
                case "truewhite":
                    type_iv.setBackgroundResource(R.drawable.ic_tree_white_special);
                    break;
                case "falsewhite":
                    type_iv.setBackgroundResource(R.drawable.ic_tree_white);
                    break;
                default:
                    break;
            }
            ImageView profile_iv = holder.profilePicture;
            String pictureURL = tree.getString("profilePicture");
            if (!pictureURL.isEmpty()){
                new MyTreesAdapter.DownloadImageTask(profile_iv).execute(pictureURL);
            }
            ImageView tree_iv = holder.treeImageView;
            String treeID = tree.getString("_id");
            int level = tree.getInt("level");
            TextView level_tv = holder.levelTextView;
            String levelText = "Lv. " + level;
            level_tv.setText(levelText);
            boolean isSpecial = tree.getString("special").equals("true");
            switch(level) {
                case 0:
                    if (isSpecial) {
                        tree_iv.setBackgroundResource(R.drawable.tree_0_special);
                    } else {
                        tree_iv.setBackgroundResource(R.drawable.tree_0);
                    }
                    break;
                case 1:
                    if (isSpecial) {
                        tree_iv.setBackgroundResource(R.drawable.tree_1_special);
                    } else {
                        tree_iv.setBackgroundResource(R.drawable.tree_1);
                    }
                    break;
                case 2:
                    tree_iv.setBackgroundResource(R.drawable.tree_2);
                    break;
                case 3:
                    tree_iv.setBackgroundResource(R.drawable.tree_3);
                    break;
                case 4:
                    tree_iv.setBackgroundResource(R.drawable.tree_4);
                    break;
                case 5:
                    tree_iv.setBackgroundResource(R.drawable.tree_5);
                    break;
                case 6:
                    if (!isSpecial) {
                        tree_iv.setBackgroundResource(R.drawable.tree_6);
                        break;
                    }
                    if (treeID.substring(0,1).compareTo("a") > 0) {
                        tree_iv.setBackgroundResource(R.drawable.tree_6_special1);
                    } else if (treeID.substring(0,1).compareTo("5") > 0) {
                        tree_iv.setBackgroundResource(R.drawable.tree_6_special2);
                    } else {
                        tree_iv.setBackgroundResource(R.drawable.tree_6_special3);
                    }
                    break;
                default:
                    break;
            }
            holder.treeCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, TreeViewActivity.class);
                    intent.putExtra("treeID", treeID);
                    mContext.startActivity(intent);
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
