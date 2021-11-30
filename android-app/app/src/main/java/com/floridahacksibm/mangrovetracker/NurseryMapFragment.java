package com.floridahacksibm.mangrovetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NurseryMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NurseryMapFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private LatLng myNursery = new LatLng(37.86823867951486,-122.2674161138852);
    private LatLng myNursery2 = new LatLng(37.86326381223994, -122.2584253494755);
    private View fragmentView;
    private GoogleMap map;

    private ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();

    private Marker mSelectedMarker;

    ArrayList<Marker> getMarkers() {
        return mMarkerArray;
    }

    private final LatLng defaultLocation = new LatLng(37.87, -122.26);
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = MapFragment.class.getSimpleName();

    private String API_KEY = "AIzaSyB7MFeRvdRLzEKqbwj-jxac3IKPDLleWjE";

    private FusedLocationProviderClient fusedLocationClient;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NurseryMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NurseryMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NurseryMapFragment newInstance(String param1, String param2) {
        NurseryMapFragment fragment = new NurseryMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_nursery_map, container, false);
        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get the button view
        @SuppressLint("ResourceType") View locationButton = ((View) fragmentView.findViewById(1).getParent()).findViewById(2);

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
        getNurseries();
    }

    private void setupRecyclerView(JSONArray array) {
        RecyclerView nurseriesList = getView().findViewById(R.id.nurseriesList);
        NurseryMapAdapter adapter = new NurseryMapAdapter(array, getActivity());
        nurseriesList.setAdapter(adapter);
        nurseriesList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    private void getNurseries() {
        String url = "https://mangrove-backend-api.us-south.cf.appdomain.cloud/api/nurseries";

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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String s = response.body().string();
                            System.out.println(s);
                            JSONArray arr = new JSONArray(s);
                            setupRecyclerView(arr);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
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
        Marker marker = map.addMarker(new MarkerOptions().position(myNursery).title("Steven's Baby Forest").icon(BitmapFromVector(getActivity(), R.drawable.ic_nursery_map_unselected)));
        mMarkerArray.add(marker);
        Marker marker2 = map.addMarker(new MarkerOptions().position(myNursery2).title("Marine Resource Council").icon(BitmapFromVector(getActivity(), R.drawable.ic_nursery_map_unselected)));
        mMarkerArray.add(marker2);
        map.moveCamera(CameraUpdateFactory.newLatLng(myNursery));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (null != mSelectedMarker) {
                    mSelectedMarker.setIcon(BitmapFromVector(getActivity(),R.drawable.ic_nursery_map_unselected));
                }
                mSelectedMarker = marker;
                mSelectedMarker.setIcon(BitmapFromVector(getActivity(), R.drawable.ic_nursery_map_selected));
                return false;
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(null != mSelectedMarker) {
                    mSelectedMarker.setIcon(BitmapFromVector(getActivity(), R.drawable.ic_nursery_map_unselected));
                }
                mSelectedMarker = null;
            }
        });
    }

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

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}