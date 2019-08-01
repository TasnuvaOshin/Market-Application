package com.joytechnologies.market.SearchWithRoute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.joytechnologies.market.Model.SearchDataModel;
import com.joytechnologies.market.R;
import com.joytechnologies.market.SearchResult.FetchURL;
import com.joytechnologies.market.SearchResult.ShowSearchResultFragment;
import com.joytechnologies.market.SearchResult.TaskLoadedCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchRouteActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ArrayList<SearchDataModel> arrayList;
    private GoogleMap gMap;
    private Location location;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Double currentLang, currentLong;
    Marker mCurrLocationMarker, marker;
    private Button btDirection;
    List<MarkerOptions> markerOptionsDirection;

    private MarkerOptions origin, destination;
    private Polyline currentLine;
    int count = 0;
    private MarkerOptions markerOptions;
    private Double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_route);
        // Creating a marker
        markerOptions = new MarkerOptions();
        btDirection = findViewById(R.id.bt_getDirection);
        markerOptionsDirection = new ArrayList<>();
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.f_map);
        supportMapFragment.getMapAsync(SearchRouteActivity.this);
        arrayList = new ArrayList<SearchDataModel>();
        arrayList.clear();
        new CallApiData().execute();

    }


    //for getting the route we need to call the GetUrl Method
    private String getUrl(Double startLat, Double startLng, Double endLat, Double endLng, String direction) {
        String Origin = "origin=" + startLat + "," + startLng;
        String Destination = "destination=" + endLat + "," + endLng;
        String mode = "mode=driving";
        String parameter = Origin + "&" + Destination + "&" + mode;
        //this is the main api key for direction
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameter + "&key=AIzaSyAycZ7yEq_SjhoFHcq60NptoLBTN-f2lwc";
        //https://maps.googleapis.com/maps/api/directions/json?origin=23.7746465,90.3944036&destination=23.7746465,90.3944036&mode=driving&key=AIzaSyAhje3XitH37m7iMYmVh0U1hnzsGmCLaSI

        return url;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (checkLocationPermission()) {
            gMap = googleMap;
            gMap.setMyLocationEnabled(true);
            //for getting the current Location for my place
            GetCurrentLocation();


            gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    //we need the api url for getting the route
                    String url = getUrl(currentLang, currentLong, marker.getPosition(), "driving");

        /*
        Fetchurl is the class that will get the value from the url
         */
                    new FetchURL(SearchRouteActivity.this).execute(url, "driving");
                    // Toast.makeText(SearchRouteActivity.this, "Infowindow clicked", Toast.LENGTH_SHORT).show();
                }
            });
            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                   // new CallApiData().execute();

                    if (count == 0) {
                        // Toast.makeText(SearchRouteActivity.this, "Marker Clicked", Toast.LENGTH_SHORT).show();
                        //we need the api url for getting the route

                        String url = getUrl(currentLang, currentLong, marker.getPosition(), "driving");
        /*
        Fetchurl is the class that will get the value from the url
         */
                        new FetchURL(SearchRouteActivity.this).execute(url, "driving");
                        // Toast.makeText(SearchRouteActivity.this, "Infowindow clicked", Toast.LENGTH_SHORT).show();
                        count++;
                    } else {
                        //already map route is there so we need to

                        count = 0;
                        Intent i = new Intent(SearchRouteActivity.this, SearchRouteActivity.class);
                        i.putExtra("key", getIntent().getStringExtra("key"));
                        SearchRouteActivity.this.overridePendingTransition(0, 0);
                        startActivity(i);


                    }
                    return false;
                }
            });
        }
    }

    private String getUrl(Double originLat, Double originLan, LatLng destination, String driving) {


        String start = "origin=" + originLat + "," + originLan;
        String end = "destination=" + destination.latitude + "," + destination.longitude;
        String mode = "mode=" + driving;
        String format = start + "&" + end + "&" + mode;
        String apiUrl = "https://maps.googleapis.com/maps/api/directions/json?" + format + "&key=AIzaSyAycZ7yEq_SjhoFHcq60NptoLBTN-f2lwc";
        return apiUrl;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentLine != null) {
            currentLine.remove();
        } else {

            currentLine = gMap.addPolyline((PolylineOptions) values[0]);

        }
    }

    @SuppressLint("StaticFieldLeak")
    public class CallApiData extends AsyncTask<String, String, String> {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                String key = getIntent().getStringExtra("key");
                url = new URL("http://v-tube.xyz/market/item.php?key=" + key);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String line = "";
                String myFile;
                int i;

                while ((line = bufferedReader.readLine()) != null) {

                    stringBuffer.append(line);

                }

                myFile = stringBuffer.toString();

                Log.d("data", myFile);

                JSONArray parent = new JSONArray(myFile);
                int j = 0;

                while (j <= parent.length()) {

                    JSONObject child = parent.getJSONObject(j);

                    String Name = child.getString("Name");
                    String item = child.getString("item");
                    String latitude = child.getString("latitude");
                    String longitude = child.getString("longitude");
                    String Address = child.getString("Address");
                    String Website = child.getString("Website");
                    String Phone_no = child.getString("Phone_no");
                    String Offday = child.getString("Offday");


                    arrayList.add(new SearchDataModel(Name, item, latitude, longitude, Address, Website, Phone_no, Offday));
                    j++;
                }

            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("size", String.valueOf(arrayList.size()));
            gMap.clear();

            for (int i = 0; i < arrayList.size(); i++) {

//                // Creating a marker
//                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list

                // Getting latitude of the place

                double lat = Double.parseDouble(arrayList.get(i).getLatitude());

                // Getting longitude of the place
                double lng = Double.parseDouble(arrayList.get(i).getLongitude());
                Log.d("lat", String.valueOf(lat));
                Log.d("lat", String.valueOf(lng));


                //for counting distance

                float results[] = new float[10];
                Location.distanceBetween(currentLang, currentLong, lat, lng, results);
                markerOptions.snippet("Distance=" + results[0] / 1000 + "km");
                distance = (double) (results[0] / 1000);
                Log.d("d", String.valueOf(distance));

                // Getting name
                String name = arrayList.get(i).getName();

                Log.d("Map", "place: " + name);

                // Getting vicinity
                String vicinity = arrayList.get(i).getAddress();

                LatLng latLng = new LatLng(lat, lng);
                float zoomLevel = 12.0f; //This goes up to 21
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                // Setting the position for the marker
                markerOptions.position(latLng);

                markerOptions.title(name + " : " + vicinity + " : " + distance + " Km");

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

                // Placing a marker on the touched position
                Marker m = gMap.addMarker(markerOptions);
                m.showInfoWindow();
                //for showing the current Position ALso
                LatLng latLngs = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions2 = new MarkerOptions();
                markerOptions2.position(latLngs);
                markerOptions2.title("Current Position");
                markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mCurrLocationMarker = gMap.addMarker(markerOptions2);

                //  gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));

            }

        }
    }


    private void GetCurrentLocation() {
        if (checkLocationPermission()) {
            Log.d("debug", "Now We want to show our current location");
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SearchRouteActivity.this);
            @SuppressLint("MissingPermission") Task LocationTask = fusedLocationProviderClient.getLastLocation();

            LocationTask.addOnCompleteListener(new OnCompleteListener() {

                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                        Log.d("debug", "Now We get our Current Location");

                        location = (Location) task.getResult();

                        currentLang = location.getLatitude();
                        currentLong = location.getLongitude();
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f));
                        Log.d("lat", String.valueOf(currentLang));
                        Log.d("long", String.valueOf(currentLong));


                        // UpdateCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM, "Location");
                          /*
                          now we will get the api from which we can the nearby market
                           */


                    }
                }
            });
        }

    }

    //for checking the location Permission

//checking the permission before the full process starts


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(SearchRouteActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SearchRouteActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(SearchRouteActivity.this)
                        .setTitle("Permission")
                        .setMessage("Please Share/on Your Location")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SearchRouteActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(SearchRouteActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


}
