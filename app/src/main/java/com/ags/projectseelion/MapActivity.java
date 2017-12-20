package com.ags.projectseelion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

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
    private Route routeType;
    private ArrayList<POI> chosenList;
    private SparseArray<Marker> visibleMarkers = new SparseArray<>();
    private LocationCallback locationCallback;
    private List<List<LatLng>> route = new ArrayList<>();
    private Polyline lineToVisit;
    private Polyline lineVisited;
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient googleApiClient;

    private LatLng northEastBound = null;
    private LatLng southWestBound = null;

    List<POI> pois = MapController.getInstance().getPOIs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            fresh = false;
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        mGeofenceList = new ArrayList<>();

        routeType = Route.values()[(getIntent().getIntExtra(KEY_ROUTE, 0))];

        if(routeType==Route.Historic)
            MapController.getInstance().setAllpoisToChosen();



        setContentView(R.layout.activity_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGeofencingClient = LocationServices.getGeofencingClient(this);

        LocalBroadcastManager lbc = LocalBroadcastManager.getInstance(this);
        GoogleReceiver receiver = new GoogleReceiver(this);
        lbc.registerReceiver(receiver, new IntentFilter("googlegeofence"));

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        createGoogleApi();
    }

    private void createGoogleApi() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
        outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        registerLocationUpdates();
        super.onResume();
    }

    @Override
    protected void onPause() {
        deregisterLocationUpdates();
        super.onPause();
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
        addPOIsToChosenList();

        for (POI poi : pois) {
            addMarkerForRoute(poi);
        }

        try {
            if (hasLocationPermission() && fresh) {
                mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                        .addOnSuccessListener(this, aVoid -> {
                            Log.d("SUC", "succes");
                        })
                        .addOnFailureListener(this, e -> {
                            Log.d("FAI", "failure");
                        });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

        createRoute();
    }

    private void addPOIsToChosenList(){
        chosenList = new ArrayList<>();
        switch(routeType){
            case Historic:{
                Log.d("ROUTE", "Historic Route started");
                for(POI poi : pois){
                    chosenList.add(poi);
                    Log.d("ROUTE", "Naam: "+poi.getName());
                }
            }break;
            case Custom:{
                Log.d("ROUTE", "Custom Route started");
                for(POI poi : pois){
                    if (poi.isChosen() && (poi.getCategory() == Category.Building)) {
                        chosenList.add(poi);
                        Log.d("ROUTE", "Naam: "+poi.getName());
                    }
                }



            }break;
        }
        Log.d("Size", "onMapReady: " + chosenList.size());
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
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(String.valueOf(poi.getNumber()))

                .setCircularRegion(
                        poi.getLatitude(),
                        poi.getLongitude(),
                        30
                )
                .setExpirationDuration(NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());
    }

    private void addMarkerForRoute(POI poi) {
        if (poi.getCategory() == Category.Building) {
            switch (routeType) {
                case Custom:
                    if (poi.isChosen()) addMarker(poi);
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
                        if (cameraPosition == null && lastKnownLocation != null)
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

    @SuppressLint("MissingPermission")
    private void registerLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setSmallestDisplacement(10)
                .setMaxWaitTime(1000)
                .setFastestInterval(10000);

        if (hasLocationPermission() && locationCallback != null)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void deregisterLocationUpdates() {
        if (locationCallback != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void onLocationChanged(Location lastLocation) {
        Log.i("MAP", "Location Changed");
        lastKnownLocation = lastLocation;
        if (route.size() > 0) {
            Log.i("Route", route.toString());
            drawRoute(route);
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

    private void drawRoute(List<List<LatLng>> route) {
        Log.i("MAP", "Drawing Route");

        PolylineOptions lineOptionsVisited = new PolylineOptions();
        PolylineOptions lineOptionsToVisit = new PolylineOptions();

        LatLng northEast = route.get(0).get(0);
        if (northEastBound != null) {
            if (northEast.longitude > northEastBound.longitude && northEast.latitude > northEastBound.latitude) {
                northEastBound = northEast;
            }
        } else {
            northEastBound = northEast;
        }

        LatLng southWest = route.get(0).get(1);
        if (southWestBound != null) {
            if (southWest.longitude < southWestBound.longitude && southWest.latitude < southWestBound.latitude) {
                southWestBound = southWest;
            }
        } else {
            southWestBound = southWest;
        }

        LatLng start = route.get(1).get(0);
        LatLng finish = route.get(route.size() - 1).get(route.get(route.size() - 1).size() - 1);

        String direction;
        if (Math.abs(northEast.latitude - start.latitude) + Math.abs(northEast.longitude - start.longitude) < Math.abs(northEast.latitude - finish.latitude) + Math.abs(northEast.longitude - finish.longitude)) {
            direction = "SW";
        } else direction = "NE";

        for (int i = 1; i < route.size(); i++) {
            List<LatLng> leg = route.get(i);
            for (LatLng p : leg) {
                if (lastKnownLocation != null && ((direction.equals("SW") && (Math.abs(finish.latitude - lastKnownLocation.getLatitude()) + Math.abs(finish.longitude - lastKnownLocation.getLongitude()) < Math.abs(finish.latitude - p.latitude) + Math.abs(finish.longitude - p.longitude))) || (direction.equals("NE") && (Math.abs(finish.latitude - lastKnownLocation.getLatitude()) + Math.abs(finish.longitude - lastKnownLocation.getLongitude()) < Math.abs(finish.latitude - p.latitude) + Math.abs(finish.longitude - p.longitude))))) {
                    lineOptionsVisited.add(p);
                } else {
                    lineOptionsToVisit.add(p);
                }
            }
        }

        lineOptionsToVisit.width(10);
        lineOptionsToVisit.color(Color.RED);
        lineOptionsVisited.width(10);
        lineOptionsVisited.color(Color.GRAY);

        Log.d("onPostExecute", "onPostExecute lineoptions decoded");

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptionsToVisit != null && lineOptionsVisited != null) {
            LatLngBounds bounds = new LatLngBounds(southWest, northEast);
            int padding = 150;
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

            if (lineToVisit != null)
                lineToVisit.remove();
            if (lineVisited != null)
                lineVisited.remove();

            lineToVisit = mMap.addPolyline(lineOptionsToVisit);
            lineVisited = mMap.addPolyline(lineOptionsVisited);


        } else {
            Log.d("onPostExecute", "without Polylines drawn");
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

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    class GoogleReceiver extends BroadcastReceiver {

        MapActivity mActivity;

        public GoogleReceiver(Activity activity) {
            mActivity = (MapActivity) activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle args = new Bundle();
            args.putInt(POIFragment.KEY_POI, intent.getIntExtra("ID", 0));
            POIFragment poiFragment = new POIFragment();
            poiFragment.setArguments(args);
            poiFragment.show(getSupportFragmentManager(), "POI");
        }
    }

    private void createRoute(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Detination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String trafficMode = "mode=walking";
//        String trafficMode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + trafficMode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        FetchUrl fetch = new FetchUrl();
        fetch.execute(url);
    }

    private void createRoute() {
        List<String> urls = new ArrayList<>();

        // Origin of route
        LatLng originLatLng = new LatLng(chosenList.get(0).getLatitude(), chosenList.get(0).getLongitude());
        String str_origin = "origin=" + originLatLng.latitude + "," + originLatLng.longitude;

        // Detination of route
        LatLng destLatLng = new LatLng(chosenList.get(chosenList.size() - 1).getLatitude(), chosenList.get(chosenList.size() - 1).getLongitude());
        String str_dest = "destination=" + destLatLng.latitude + "," + destLatLng.longitude;

        // Mode of transportation
        String trafficMode = "mode=walking";

        // Waypoints of route
        StringBuilder wayPoints = new StringBuilder("waypoints=optimize:true|");
        


        if (chosenList.size() < 24) {
            for (int i = 1; i < chosenList.size() - 1; i++) {
                LatLng wayPointLatLng = new LatLng(chosenList.get(i).getLatitude(), chosenList.get(i).getLongitude());
                wayPoints.append(wayPointLatLng.latitude).append(",").append(wayPointLatLng.longitude);
                if (i < chosenList.size() - 1 || chosenList.size() == 2)
                    wayPoints.append("|");
            }
            // Url building
            String parameters = str_origin + "&" + str_dest + "&" + wayPoints + "&" + trafficMode;

            String output = "json";

            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
            urls.add(url);
        } else {
            int amountDone = 0;
            int amountLeft;

            while (amountDone < chosenList.size() - 1) {


                if (chosenList.size() - amountDone > 23)
                    amountLeft = 22;
                else
                    amountLeft = chosenList.size() - amountDone - 1;

                originLatLng = new LatLng(chosenList.get(amountDone).getLatitude(), chosenList.get(amountDone).getLongitude());
                str_origin = "origin=" + originLatLng.latitude + "," + originLatLng.longitude;

                destLatLng = new LatLng(chosenList.get(amountDone + amountLeft).getLatitude(), chosenList.get(amountDone + amountLeft).getLongitude());
                str_dest = "destination=" + destLatLng.latitude + "," + destLatLng.longitude;

                for (int j = 0; j < amountLeft; j++) {
                    LatLng wayPointLatLng = new LatLng(chosenList.get(amountDone).getLatitude(), chosenList.get(amountDone).getLongitude());
                    wayPoints.append(wayPointLatLng.latitude).append(",").append(wayPointLatLng.longitude);
                    if (j < amountLeft) {
                        wayPoints.append("|");
                    }
                    amountDone++;
                }

                // Url building
                String parameters = str_origin + "&" + str_dest + "&" + wayPoints + "&" + trafficMode;

                String output = "json";

                String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
                wayPoints.delete(0, wayPoints.length() - 1);
                urls.add(url);
            }
        }





        FetchUrl fetch;
        for (String url : urls) {
            fetch = new FetchUrl();
            fetch.execute(url);
        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }


    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<LatLng>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<LatLng>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<LatLng>> routeData = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                RouteDataParser parser = new RouteDataParser();

                // Starts parsing routes data
                routeData = parser.parseRoutesInfo(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routeData;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<LatLng>> result) {
            route.addAll(result);
            drawRoute(route);
        }
    }
}

