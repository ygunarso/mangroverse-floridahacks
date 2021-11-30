package com.floridahacksibm.mangrovetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TreeViewActivity extends AppCompatActivity {
    Integer[] levels = new Integer[]{1, 5, 20, 50, 150, 365, 730};

    String[] months = new String[]{"Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};

    String treeID;
    ImageView treeImage;
    TextView treeNameView;
    TextView donorNameView;
    TextView planterNameView;
    TextView treeTypeLabel;

    ImageView treeTypeView;
    ImageView donorPicture;
    ImageView planterPicture;

    TextView levelLabel;
    ProgressBar levelBar;
    TextView completedProgress;
    TextView remainingProgress;

    ImageView backButton;
    ImageView shareButton;
    ImageView moreButton;

    TabLayout treeInfoTab;

    ImageView speciesIconView;
    TextView speciesNameView;

    TextView widthBeforeView;
    TextView widthAfterView;
    TextView heightBeforeView;
    TextView heightAfterView;

    TextView donatedDate;
    TextView plantedDate;
    TextView ageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_view);

        treeImage = findViewById(R.id.tree_image);
        treeNameView = findViewById(R.id.treeName);
        donorNameView = findViewById(R.id.donorName);
        planterNameView = findViewById(R.id.planterName);
        treeTypeLabel = findViewById(R.id.treeTypeLabel);
        treeTypeView = findViewById(R.id.treeTypeIcon);

        donorPicture = findViewById(R.id.donorPicture);
        planterPicture = findViewById(R.id.planterPicture);

        levelLabel = findViewById(R.id.levelLabel);
        levelBar = findViewById(R.id.levelBar);
        completedProgress = findViewById(R.id.completedProgress);
        remainingProgress = findViewById(R.id.remainingProgress);

        speciesIconView = findViewById(R.id.speciesIcon);
        speciesNameView = findViewById(R.id.speciesName);

        widthBeforeView = findViewById(R.id.widthBefore);
        widthAfterView = findViewById(R.id.widthAfter);
        heightBeforeView = findViewById(R.id.heightBefore);
        heightAfterView = findViewById(R.id.heightAfter);

        donatedDate = findViewById(R.id.donatedTime);
        plantedDate = findViewById(R.id.plantedTime);
        ageView = findViewById(R.id.ageTime);

        backButton = findViewById(R.id.backIcon);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        shareButton = findViewById(R.id.shareIcon);
        moreButton = findViewById(R.id.moreIcon);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            treeID = extras.getString("treeID");
        }
        setUpTabs();

        getTree();
    }

    private void setUpTabs() {
        treeInfoTab = findViewById(R.id.treeInfoTab);

        treeInfoTab.addTab(treeInfoTab.newTab().setText("Mangrove"));
        treeInfoTab.addTab(treeInfoTab.newTab().setText("Plant Zone"));
        treeInfoTab.addTab(treeInfoTab.newTab().setText("NFT"));

        treeInfoTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
    private void setUpViews(JSONObject tree) {
        try {
            treeNameView.setText(tree.getString("name"));
            donorNameView.setText(tree.getString("donorName"));
            planterNameView.setText(tree.getString("planterName"));
            String treeType = "";
            if (tree.getString("special").equals("true")) {
                treeType += "Special ";
            }
            String species = tree.getString("species");
            if (!species.isEmpty()) {
                species = species.substring(0, 1).toUpperCase() + species.substring(1);
                treeType += species;
                treeType += " Mangrove";
                treeTypeLabel.setText(treeType);
            }
            new TreeViewActivity.DownloadImageTask(donorPicture).execute(tree.getString("donorPicture"));
            String planterURL = tree.getString("planterPicture");
            if (!planterURL.isEmpty()) {
                new TreeViewActivity.DownloadImageTask(planterPicture).execute(planterURL);
            }
            int level = tree.getInt("level");
            String levelText = "Lv. " + level;
            levelLabel.setText(levelText);
            boolean isSpecial = tree.getString("special").equals("true");
            switch(level) {
                case 0:
                    if (isSpecial) {
                        treeImage.setBackgroundResource(R.drawable.tree_0_special);
                    } else {
                        treeImage.setBackgroundResource(R.drawable.tree_0);
                    }
                    break;
                case 1:
                    if (isSpecial) {
                        treeImage.setBackgroundResource(R.drawable.tree_1_special);
                    } else {
                        treeImage.setBackgroundResource(R.drawable.tree_1);
                    }
                    break;
                case 2:
                    treeImage.setBackgroundResource(R.drawable.tree_2);
                    break;
                case 3:
                    treeImage.setBackgroundResource(R.drawable.tree_3);
                    break;
                case 4:
                    treeImage.setBackgroundResource(R.drawable.tree_4);
                    break;
                case 5:
                    treeImage.setBackgroundResource(R.drawable.tree_5);
                    break;
                case 6:
                    if (!isSpecial) {
                        treeImage.setBackgroundResource(R.drawable.tree_6);
                        break;
                    }
                    if (treeID.substring(0,1).compareTo("a") > 0) {
                        treeImage.setBackgroundResource(R.drawable.tree_6_special1);
                    } else if (treeID.substring(0,1).compareTo("5") > 0) {
                        treeImage.setBackgroundResource(R.drawable.tree_6_special2);
                    } else {
                        treeImage.setBackgroundResource(R.drawable.tree_6_special3);
                    }
                    break;
                default:
                    treeImage.setBackgroundResource(R.drawable.tree_0);
                    break;
            }

            int days_on = tree.getInt("days");
            completedProgress.setText(String.valueOf(days_on));
            int days_total = levels[level];
            remainingProgress.setText(String.valueOf(days_total));
            levelBar.setProgress(days_on*100/days_total);

            String type = tree.getString("special") + tree.getString("species");
            switch(type) {
                case "trueblack":
                    treeTypeView.setBackgroundResource(R.drawable.ic_tree_black_special);
                    break;
                case "falseblack":
                    treeTypeView.setBackgroundResource(R.drawable.ic_tree_black);
                    break;
                case "truered":
                    treeTypeView.setBackgroundResource(R.drawable.ic_tree_red_special);
                    break;
                case "falsered":
                    treeTypeView.setBackgroundResource(R.drawable.ic_tree_red);
                    break;
                case "truewhite":
                    treeTypeView.setBackgroundResource(R.drawable.ic_tree_white_special);
                    break;
                case "falsewhite":
                    treeTypeView.setBackgroundResource(R.drawable.ic_tree_white);
                    break;
                default:
                    break;
            }

            String speciesName = tree.getString("species");
            switch(speciesName) {
                case "red":
                    speciesIconView.setBackgroundResource(R.drawable.ic_tree_red);
                    speciesNameView.setText("Red (Rhizophora mangle)");
                    break;
                case "black":
                    speciesIconView.setBackgroundResource(R.drawable.ic_tree_black);
                    speciesNameView.setText("Black (Avicennia germinans)");
                    break;
                case "white":
                    speciesIconView.setBackgroundResource(R.drawable.ic_tree_white);
                    speciesNameView.setText("White (Laguncularia racemosa)");
                    break;
                default:
                    break;
            }

            JSONArray widths = tree.getJSONArray("widths");
            JSONArray heights = tree.getJSONArray("heights");

            if (widths.length() > 0) {
                String widthBeforeText = widths.getDouble(0) + "\" (when planted)";
                String widthAfterText = widths.getDouble(widths.length()-1) + "\" (now est.)";
                widthBeforeView.setText(widthBeforeText);
                widthAfterView.setText(widthAfterText);
                String heightBeforeText = heights.getDouble(0) + "\" (when planted)";
                String heightAfterText = heights.getDouble(heights.length()-1) + "\" (now est.)";
                heightBeforeView.setText(heightBeforeText);
                heightAfterView.setText(heightAfterText);
            }

            Calendar cal = Calendar.getInstance();
            double donatedEpoch = tree.getDouble("donatedTime");
            Date donatedDateObj = new Date((long)donatedEpoch*1000);
            cal.setTime(donatedDateObj);
            String donatedDateText = cal.get(Calendar.DAY_OF_MONTH)+" "+months[cal.get(Calendar.MONTH)-1]+" "+cal.get(Calendar.YEAR);
            donatedDate.setText(donatedDateText);
            double plantedEpoch = tree.getDouble("plantedTime");
            if (plantedEpoch != 0) {
                Date plantedDateObj = new Date((long)plantedEpoch*1000);
                cal.setTime(plantedDateObj);
                String plantedDateText = cal.get(Calendar.DAY_OF_MONTH)+" "+months[cal.get(Calendar.MONTH)-1]+" "+cal.get(Calendar.YEAR);
                plantedDate.setText(plantedDateText);

            }

            JSONObject dateDiff = tree.getJSONObject("dateDiff");
            if (dateDiff.getInt("years") > 0) {
                String ageText = dateDiff.getInt("years") + " Years";
                ageView.setText(ageText);
            } else if (dateDiff.getInt("months") > 0) {
                String ageText = dateDiff.getInt("months") + " Months";
                ageView.setText(ageText);
            } else {
                String ageText = dateDiff.getInt("days") + " Days";
                ageView.setText(ageText);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView(JSONArray array) {
        RecyclerView picturesList = findViewById(R.id.picturesList);
        PicturesAdapter adapter = new PicturesAdapter(array);
        picturesList.setAdapter(adapter);
        picturesList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void getTree() {
        String url = "https://mangrove-backend-api.us-south.cf.appdomain.cloud/api/trees/" + treeID;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TreeViewActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        call.cancel();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String s = response.body().string();
                            JSONObject obj = new JSONObject(s);
                            setUpViews(obj);
                            JSONArray pictures = obj.getJSONArray("pictures");
                            pictures.put("end");
                            setupRecyclerView(pictures);
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

    private int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}