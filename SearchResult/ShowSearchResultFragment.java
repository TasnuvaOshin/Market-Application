package com.joytechnologies.market.SearchResult;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.joytechnologies.market.Market.Market_Home_Fragment;
import com.joytechnologies.market.Model.SearchDataModel;
import com.joytechnologies.market.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class ShowSearchResultFragment extends Fragment implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ArrayList<SearchDataModel> arrayList;
    private GoogleMap gMap;
    private Location location;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Double currentLang,currentLong;
    Marker mCurrLocationMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_search_result, container, false);
        Log.d("call","call");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.f_map);
        supportMapFragment.getMapAsync(this);
        arrayList = new ArrayList<SearchDataModel>();
        arrayList.clear();
        new CallApiData().execute();


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(checkLocationPermission()) {
            gMap = googleMap;
            gMap.setMyLocationEnabled(true);
            //for getting the current Location for my place
            GetCurrentLocation();
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
                String key = getArguments().getString("key");
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

                Log.d("data",myFile);

                JSONArray parent = new JSONArray(myFile);
                int j =0;

                while (j <= parent.length()){

                    JSONObject child = parent.getJSONObject(j);

                    String Name = child.getString("Name");
                    String item = child.getString("item");
                    String latitude = child.getString("latitude");
                    String longitude = child.getString("longitude");
                    String Address  = child.getString("Address");
                    String Website = child.getString("Website");
                    String Phone_no = child.getString("Phone_no");
                    String Offday = child.getString("Offday");



                    arrayList.add(new SearchDataModel(Name,item,latitude,longitude,Address,Website,Phone_no,Offday));
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

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list

                // Getting latitude of the place
                double lat = Double.parseDouble(arrayList.get(i).getLatitude());

                // Getting longitude of the place
                double lng = Double.parseDouble(arrayList.get(i).getLongitude());
                Log.d("lat", String.valueOf(lat));
                Log.d("lat", String.valueOf(lng));

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

                markerOptions.title(name + " : " + vicinity);

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                // Placing a marker on the touched position
                Marker m = gMap.addMarker(markerOptions);

                //for showing the current Position ALso
                LatLng latLngs= new LatLng(location.getLatitude(), location.getLongitude());
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
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Permission")
                        .setMessage("Please Share/on Your Location")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}
