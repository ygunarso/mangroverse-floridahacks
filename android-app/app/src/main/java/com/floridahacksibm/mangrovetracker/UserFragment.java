package com.floridahacksibm.mangrovetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button signout_button;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signout_button = getView().findViewById(R.id.signout);

        signout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signout();
            }
        });
        String userID = "185e3483fbd97f5613833efcd09311ba";
        getContributions(userID);
    }

    private void setUpViews(JSONArray array) {
        ConstraintLayout parentLayout = (ConstraintLayout)getView().findViewById(R.id.contributionsList);
        ConstraintSet set = new ConstraintSet();

        ArrayList<Integer> viewIDArray = new ArrayList<Integer>();
        ArrayList<Integer> treeNameIDArray = new ArrayList<Integer>();
        ArrayList<Integer> buttonArray = new ArrayList<Integer>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = new JSONObject();
            try {
                obj = array.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Action TextView
            TextView actionView = new TextView(getActivity());
            try {
                String actionInput = obj.getString("type");
                String actionOutput = actionInput.substring(0,1).toUpperCase() + actionInput.substring(1);
                actionView.setText(actionOutput);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            actionView.setId(View.generateViewId());
            parentLayout.addView(actionView, 0);
            viewIDArray.add(actionView.getId());
            // TreeName TextView
            TextView treeNameView = new TextView(getActivity());
            try {
                String treeInput = obj.getString("treeName");
                String treeOutput = treeInput.substring(0,1).toUpperCase() + treeInput.substring(1);
                actionView.setText(treeOutput);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            treeNameView.setId(View.generateViewId());
            parentLayout.addView(treeNameView, 0);
            treeNameIDArray.add(treeNameView.getId());
            // Buttons
            Button button = new Button(getActivity());
            button.setText("View");
            button.setId(View.generateViewId());
            parentLayout.addView(button, 0);
            buttonArray.add(button.getId());
            String treeID = "";
            try {
                treeID = obj.getString("treeID");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String finalTreeID = treeID;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Open treeview activity
                    Intent intent = new Intent(getActivity(), TreeViewActivity.class);
                    intent.putExtra("treeID", finalTreeID);
                    startActivity(intent);
                }
            });
        }

        set.clone(parentLayout);
        // connect start and end point of views, in this case top of child to top of parent.
        set.connect(viewIDArray.get(0), ConstraintSet.TOP, parentLayout.getId(), ConstraintSet.TOP, 60);
        set.connect(treeNameIDArray.get(0), ConstraintSet.TOP, viewIDArray.get(0), ConstraintSet.TOP);
        set.connect(treeNameIDArray.get(0), ConstraintSet.START, viewIDArray.get(0), ConstraintSet.END);
        set.connect(buttonArray.get(0), ConstraintSet.TOP, viewIDArray.get(0), ConstraintSet.TOP);
        set.connect(buttonArray.get(0), ConstraintSet.BOTTOM, viewIDArray.get(0), ConstraintSet.BOTTOM);
        set.connect(buttonArray.get(0), ConstraintSet.END, parentLayout.getId(), ConstraintSet.END);
        for (int i = 1; i < array.length(); i++) {
            set.connect(viewIDArray.get(i), ConstraintSet.TOP, viewIDArray.get(i-1), ConstraintSet.BOTTOM, 100);
            set.connect(treeNameIDArray.get(i), ConstraintSet.TOP, viewIDArray.get(i), ConstraintSet.TOP);
            set.connect(treeNameIDArray.get(i), ConstraintSet.START, viewIDArray.get(i), ConstraintSet.END);
            set.connect(buttonArray.get(i), ConstraintSet.TOP, viewIDArray.get(i), ConstraintSet.TOP);
            set.connect(buttonArray.get(i), ConstraintSet.BOTTOM, viewIDArray.get(i), ConstraintSet.BOTTOM);
            set.connect(buttonArray.get(i), ConstraintSet.END, parentLayout.getId(), ConstraintSet.END);
        }
        set.applyTo(parentLayout);
    }

    private void getContributions(String userID) {
//        String url = "https://mangrove-backend-api.us-south.cf.appdomain.cloud/api/orders/create";
        String url = "http://192.168.1.186:8000/api/users/"+userID+"/contributions";

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
                            JSONArray array = new JSONArray(s);
                            setUpViews(array);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void signout() {
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
    }
}