package com.example.testingalternateroutes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Spinner destinationSpinner;
    private Button btnDirections, btnClear;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private DirectionHelper directionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDirections = findViewById(R.id.btn_directions);
        btnClear = findViewById(R.id.btn_clear);

        destinationSpinner = findViewById(R.id.spinner_destination);
        ArrayAdapter<String> destinationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        destinationSpinner.setAdapter(destinationAdapter);

        destinationAdapter.add("Northbound Terminal");
        destinationAdapter.add("San-Agustin Route");
        destinationAdapter.add("Bata Route");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingScreen();
                btnDirections.setEnabled(false);

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    String selectedDestination = destinationSpinner.getSelectedItem().toString();
                    getCurrentLocation(selectedDestination);
                }
            }
        });


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("Map", "Map is ready");
        // Set camera bounds in Bacolod
        double bacolodMinLatitude = 10.6289;
        double bacolodMaxLatitude = 10.7213;
        double bacolodMinLongitude = 122.9191;
        double bacolodMaxLongitude = 123.0043;

        mMap = googleMap;

        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable the "My Location" button
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        // Create the LatLngBounds object to bacolod
        LatLngBounds bacolodBounds = new LatLngBounds(
                new LatLng(bacolodMinLatitude, bacolodMinLongitude),
                new LatLng(bacolodMaxLatitude, bacolodMaxLongitude)
        );

        // Set the camera position to show the bounds while waiting for the map to load
        mMap.setOnMapLoadedCallback(() -> mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bacolodBounds, 100)));

        btnClear.setOnClickListener(v -> mMap.clear());

        selectRouteSpinner();

        // Initialize the directionHelper object
        directionHelper = new DirectionHelper(mMap, this);
        hideLoadingScreen();
    }

    private void getCurrentLocation(String selectedDestination) {
        Log.d("Location", "Getting current location");
        showLoadingScreen();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double currentLatitude = location.getLatitude();
                    double currentLongitude = location.getLongitude();

                    // Set the origin based on the current location
                    LatLng origin = new LatLng(currentLatitude, currentLongitude);

                    // Set the destination based on the selected option
                    LatLng destination = getDestinationLatLng(selectedDestination, origin);

                    // Call showDirections() with the updated origin and destination
                    directionHelper.showDirections(origin, destination);
                }

            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void selectRouteSpinner() {
        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean firstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!firstSelection) {
                    String selectedValue = parent.getItemAtPosition(position).toString();
                    PolylineOptions polylineOptions = new PolylineOptions().width(10f).color(Color.RED);

                    if (!TextUtils.isEmpty(selectedValue)) {
                        switch (selectedValue) {
                            case "Northbound Terminal":
                                break;

                            case "San-Agustin Route":

                                break;

                            case "Bata Route":

                                break;

                        }
                    }
                } else {
                    firstSelection = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private LatLng getDestinationLatLng(String selectedDestination, LatLng userLocation) {
        LatLng nearestCoordinate = LatLngData.getNearestCoordinate(userLocation);
        LatLng coordinates;
        if (selectedDestination.equals("Northbound Terminal") ||
                selectedDestination.equals("San-Agustin Route") ||
                selectedDestination.equals("Bata Route")) {
            coordinates = nearestCoordinate;
        } else {
            coordinates = new LatLng(0, 0); // Default coordinates if destination is not found
        }

        return coordinates;
    }

    private void showLoadingScreen() {
        findViewById(R.id.loading_screen).setVisibility(View.VISIBLE);
    }

    private void hideLoadingScreen() {
        findViewById(R.id.loading_screen).setVisibility(View.GONE);
    }

    public void hideLoadingScreenAndEnableButton() {
        hideLoadingScreen();
        btnDirections.setEnabled(true);
    }


}
