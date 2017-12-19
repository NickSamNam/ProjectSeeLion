package com.ags.projectseelion;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private final static float DEFAULT_ZOOM = 18f;
    private final static String KEY_LOCATION = "LOCATION";
    private final static String KEY_CAMERA_POSITION = "CAMERA_POSITION";
    public final static String KEY_ROUTE = "ROUTE";
    private final static int ZOOM_THRESHOLD = 10;

    private boolean fresh = true;
    private GoogleMap mMap;
    private Location lastKnownLocation = null;
    private CameraPosition cameraPosition = null;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng defaultLocation = new LatLng(32.676149, -117.157703);
    private Route route;
    private SparseArray<Marker> visibleMarkers = new SparseArray<>();

    List<POI> pois = MapController.getInstance().getPOIs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            fresh = false;
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        route = Route.values()[(getIntent().getIntExtra(KEY_ROUTE, 0))];

        setContentView(R.layout.activity_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
        outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        super.onSaveInstanceState(outState);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this::onMarkerClick);
        mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Marker in Sydney"));
        mMap.setOnCameraIdleListener(() -> {
            for (POI poi : pois) {
                addMarkerForRoute(poi);
            }
        });

        if (fresh) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
            getLocationPermission();
        }
        updateLocationUI();
        getDeviceLocation();

        for (POI poi : pois) {
            addMarkerForRoute(poi);
        }
    }

    private void addMarker(POI poi) {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        if (bounds.contains(new LatLng(poi.getLatitude(), poi.getLongitude())) && mMap.getCameraPosition().zoom >= ZOOM_THRESHOLD) {
            if (visibleMarkers.get(poi.getNumber()) == null) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .title(poi.getName())
                        .position(new LatLng(poi.getLatitude(), poi.getLongitude())));
                marker.setTag(poi.getNumber());
                visibleMarkers.put(poi.getNumber(), marker);
            }
        } else {
            if (visibleMarkers.get(poi.getNumber()) != null) {
                visibleMarkers.get(poi.getNumber()).remove();
                visibleMarkers.remove(poi.getNumber());
            }
        }
    }

    private void addMarkerForRoute(POI poi) {
        if (poi.getCategory() == Category.Building) {
            switch (route) {
                case Custom:
                    if (poi.isToVisit()) addMarker(poi);
                    break;
                case Historic:
                    if (poi.getCategory().equals(Category.Building)) addMarker(poi);
                    break;
            }
        }
    }

    private void getDeviceLocation() {
        try {
            if (hasLocationPermission()) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, (task) -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (cameraPosition == null)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        if (cameraPosition == null)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateLocationUI() {
        try {
            if (hasLocationPermission()) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void getLocationPermission() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{(android.Manifest.permission.ACCESS_FINE_LOCATION)},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocationUI();
                    getDeviceLocation();
                }
            }
        }
    }

    public boolean onMarkerClick(Marker marker) {
        Bundle args = new Bundle();
        args.putInt(POIFragment.KEY_POI, (Integer) marker.getTag());
        POIFragment poiFragment = new POIFragment();
        poiFragment.setArguments(args);
        poiFragment.show(getSupportFragmentManager(), "POI");
        return true;
    }
}
