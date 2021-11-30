package com.floridahacksibm.mangrovetracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyTreesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTreesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TabLayout treesTab;
    JSONArray donated;
    JSONArray planted;
    JSONArray collected;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyTreesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyTreesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTreesFragment newInstance(String param1, String param2) {
        MyTreesFragment fragment = new MyTreesFragment();
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
        return inflater.inflate(R.layout.fragment_my_trees, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        treesTab = getView().findViewById(R.id.trees_tab);

        treesTab.addTab(treesTab.newTab().setText("Donated"));
        treesTab.addTab(treesTab.newTab().setText("Planted"));
        treesTab.addTab(treesTab.newTab().setText("Collected"));

        treesTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category = (String) tab.getText();
                switch(category) {
                    case "Donated":
                        setupRecyclerView(donated);
                        break;
                    case "Planted":
                        setupRecyclerView(planted);
                        break;
                    case "Collected":
                        setupRecyclerView(collected);
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        getTrees("445d3b280fe4627a97213755eda236a0");
    }

    private void setupRecyclerView(JSONArray array) {
        RecyclerView myTreesList = getView().findViewById(R.id.trees_list);
        MyTreesAdapter adapter = new MyTreesAdapter(array, getActivity());
        myTreesList.setAdapter(adapter);
        myTreesList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }

    private void setInfoView(int donated, int planted, int collected, String userName, String userURL) {
        TextView donatedCount = getView().findViewById(R.id.donatedCount);
        donatedCount.setText(String.valueOf(donated));
        TextView plantedCount = getView().findViewById(R.id.plantedCount);
        plantedCount.setText(String.valueOf(planted));
        TextView collectedCount = getView().findViewById(R.id.collectedCount);
        collectedCount.setText(String.valueOf(collected));
        TextView myTreesTitle = getView().findViewById(R.id.myTreesTitle);
        String title = userName + "'s Little Florida";
        myTreesTitle.setText(title);
        new DownloadImageTask(getView().findViewById(R.id.profilePicture)).execute(userURL);
    }

    private void getTrees(String userID) {
        String url = "https://mangrove-backend-api.us-south.cf.appdomain.cloud/api/users/" + userID + "/trees_short";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        call.cancel();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String s = response.body().string();
                            JSONObject obj = new JSONObject(s);
                            donated = obj.getJSONArray("donated");
                            collected = obj.getJSONArray("collected");
                            planted = obj.getJSONArray("planted");
                            String userName = obj.getString("username");
                            String userURL = obj.getString("profilePicture");
                            setupRecyclerView(donated);
                            setInfoView(donated.length(), planted.length(), collected.length(), userName, userURL);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
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