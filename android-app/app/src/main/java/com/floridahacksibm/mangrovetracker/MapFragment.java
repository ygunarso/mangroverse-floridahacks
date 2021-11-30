package com.floridahacksibm.mangrovetracker;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private View fragmentView;
    private GoogleMap map;

    private final LatLng defaultLocation = new LatLng(37.87, -122.26);
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = MapFragment.class.getSimpleName();

    private String API_KEY = "AIzaSyB7MFeRvdRLzEKqbwj-jxac3IKPDLleWjE";

    private AlertDialog.Builder builder;

    private LatLng[] plant_shops = { new LatLng(37.8665501678077, -122.26713716381234),
            new LatLng(37.86326381223994, -122.2584253494755),
            new LatLng(37.861823867951486, -122.2674161138852)
    };

    private String[] labels = { "Tree", "Zone", "Nursery" };

    private FusedLocationProviderClient fusedLocationClient;

    private ConstraintLayout markerPreview;
    private TextView markerName;
    private Button markerButton;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        builder = new AlertDialog.Builder(getContext());
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_map, container, false);
        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get the button view
        View locationButton = ((View) fragmentView.findViewById(1).getParent()).findViewById(2);

        // and next place it, for example, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 0, 130);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        markerPreview = getView().findViewById(R.id.markerPreview);
        markerName = getView().findViewById(R.id.markerTitle);
        markerButton = getView().findViewById(R.id.markerButton);
        markerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject markerObj = (JSONObject) markerButton.getTag();
                try {
                    markerButtonAction(markerObj.getString("type"), markerObj.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // Draw zones
        getZones();
    }

    public void markerButtonAction(String type, String id) {
        builder.setMessage("Are you sure?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch(type) {
                            case "Tree":
                                // go to tree view
                                System.out.println("Tree view");
                                startActivity(new Intent(getActivity(), PlantInstructionsActivity.class));
                                break;
                            case "Zone":
                                // go to plant instructions
                                startActivity(new Intent(getActivity(), PlantInstructionsActivity.class));
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        AlertDialog alert = builder.create();
        switch(type) {
            case "Tree":
                alert.setTitle("View this tree?");
                break;
            case "Nursery":
                alert.setTitle("Buy from this nursery?");
                break;
            case "Zone":
                alert.setTitle("Plant here?");
                break;
            default:
                break;
        }
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        map.setPadding(30, 0, 0, 0);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMap();
        // Draw markers on map
        for (int i = 0; i < plant_shops.length; i++) {
            Marker marker = map.addMarker(new MarkerOptions().position(plant_shops[i]).title(labels[i]));
            try {
                marker.setTag(createMarkerObject(labels[i], labels[i], labels[i]));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                markerPreview.setVisibility(View.VISIBLE);
                markerName.setText(title);
                markerButton.setTag(marker.getTag());
                if (title.equals("Tree")) {
                    markerButton.setText("View");
                } else if (title.equals("Nursery")) {
                    markerButton.setText("Buy");
                } else if (title.equals("Zone")) {
                    markerButton.setText("Plant");
                }
                return false;
            }
        });
    }

    private void getZones() {
//        String url = "https://mangrove-backend-api.us-south.cf.appdomain.cloud/api/nurseries/order";
        String url = "http://192.168.1.186:8000/api/zones";

        OkHttpClient client = new OkHttpClient();
        Request postRequest = new Request.Builder()
                .url(url)
                .build();
        client.newCall(postRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(e.getMessage());
                        Toast.makeText(getActivity(), "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        call.cancel();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String s = response.body().string();
                            JSONArray array = new JSONArray(s);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = new JSONObject();
                                try {
                                    obj = array.getJSONObject(i);
                                    double lat = obj.getDouble("latitude");
                                    double lon = obj.getDouble("longitude");
                                    int radius = obj.getInt("radius");
                                    drawZone(lat, lon, radius);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void drawZone(double lat, double lon, int radius) {
        CircleOptions circleOptions = new CircleOptions()
                                        .center(new LatLng(lat, lon))
                                        .radius(radius)
                                        .strokeWidth(2)
                                        .fillColor(Color.argb(80, 85, 119, 240))
                                        .strokeColor(Color.rgb(85, 119, 240))
                                        .clickable(true); // In meters
        Circle circle = map.addCircle(circleOptions);
    }

    private JSONObject createMarkerObject(String id, String name, String type) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("name", name);
        obj.put("type", type);
        return obj;
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private void enableMap() {
        if (map != null) {
            map.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                            } else {
                                Toast.makeText(getActivity(), "Turn on your location and click on the top left button please", Toast.LENGTH_LONG).show();
                            }
                        }

                    });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        if (map != null) {
            map.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Toast.makeText(getActivity(), "Redirecting you to your location", Toast.LENGTH_SHORT).show();
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                            } else {
                                Toast.makeText(getActivity(), "Turn on your location and click on the top left button please", Toast.LENGTH_LONG).show();
                            }
                        }

                    });
        }
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
}