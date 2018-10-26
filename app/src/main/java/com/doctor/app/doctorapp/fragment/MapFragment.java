package com.doctor.app.doctorapp.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.doctor.app.doctorapp.R;
import com.doctor.app.doctorapp.Utilitario.Common;
import com.doctor.app.doctorapp.Utilitario.Constantes;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = MapFragment.class.getName();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GeoFire geoDoctor;
    private DatabaseReference mlocations,onlineDoctor,currentDoctor;
    private LocationCallback mlocationCallback;
    private LocationRequest mlocationRequest;
    private Location mLastLocation;
    private Marker mDoctor;
    private Switch locationSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        locationSwitch = view.findViewById(R.id.switch_map);

        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        //Present System
        onlineDoctor = FirebaseDatabase.getInstance().getReference().child(Common.TABLE_DOCTOR_LOCATION);
        currentDoctor = FirebaseDatabase.getInstance().getReference(Common.TABLE_DOCTOR_GEO)
                .child(FirebaseAuth.getInstance().getUid());
        onlineDoctor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentDoctor.onDisconnect().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseDatabase.getInstance().goOnline();
                    startLocationUpdates();
                    displayLocation();
                    Snackbar.make(mapFragment.getView(),getString(R.string.map_fragment_connected),Snackbar.LENGTH_SHORT).show();
                }else{
                    FirebaseDatabase.getInstance().goOffline();
                    if (mDoctor != null) {
                        mDoctor.remove();
                        mMap.clear();
                    }
                    stopLocationUpdates();
                    Snackbar.make(mapFragment.getView(),getString(R.string.map_fragment_disconnected),Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        mlocations = FirebaseDatabase.getInstance().getReference(Common.TABLE_DOCTOR_GEO);
        geoDoctor = new GeoFire(mlocations);
        setUpLocation();

        return view;
    }

    public void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    Constantes.MY_PERMISION_REQUEST_CODE);
        } else {
            buildLocationCallBack();
            createLocationRequest();
            if(locationSwitch.isChecked())
                displayLocation();
        }
    }

    private void stopLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.removeLocationUpdates(mlocationCallback);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(mlocationRequest, mlocationCallback, Looper.myLooper());
    }

    private void buildLocationCallBack() {
        mlocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLocations().get(locationResult.getLocations().size() - 1);
                //displayLocation();
            }
        };
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mLastLocation = location;
                if (mLastLocation != null) {
                    if(locationSwitch.isChecked()){
                        final double latitud = mLastLocation.getLatitude();
                        final double longitud = mLastLocation.getLongitude();

                        geoDoctor.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitud, longitud),
                                new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (mDoctor != null)
                                    mDoctor.remove();

                                mDoctor = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitud, longitud))
                                        .title("You"));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitud, longitud), 15.0f));
                            }
                        });
                    }
                }else {
                    Log.i(TAG, getString(R.string.map_location_notfound));
                }
            }
        });
    }

    private void createLocationRequest() {
        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(Constantes.UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(Constantes.FASTEST_INTERVAL);
        mlocationRequest.setSmallestDisplacement(Constantes.DISTANCE);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onStop() {
        Log.i(TAG,"onStop");
        super.onStop();
        mapFragment.onStop();
    }

    @Override
    public void onStart() {
        Log.i(TAG,"onStart");
        super.onStart();
        mapFragment.onStart();
    }

    @Override
    public void onResume() {
        Log.i(TAG,"onResume");
        super.onResume();
        mapFragment.onResume();
    }
}
