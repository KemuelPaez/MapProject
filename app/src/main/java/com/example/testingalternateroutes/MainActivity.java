package com.example.testingalternateroutes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker currentLocationMarker;
    private Spinner destinationSpinner;
    private ArrayAdapter<String> destinationAdapter;
    private Button btnDirections;

    private DirectionHelper directionHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDirections = findViewById(R.id.btn_directions);

        destinationSpinner = findViewById(R.id.spinner_destination);
        destinationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        destinationSpinner.setAdapter(destinationAdapter);

        destinationAdapter.add("University of St. La Salle");
        destinationAdapter.add("Ayala Malls Capitol Center");
        destinationAdapter.add("SM City Bacolod");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
    public void onMapReady(GoogleMap googleMap) {
        Log.d("Map", "Map is ready");
        mMap = googleMap;

        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable the "My Location" button
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        // Set the bounds for the map Hardcoded for now
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(4.5870511, 114.1819611), // Southwest bound
                new LatLng(21.0571461, 127.6188563) // Northeast bound
        );

        // Set the camera position to show the bounds while waiting for the map to load
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        });

        // Initialize the directionHelper object
        directionHelper = new DirectionHelper(mMap, this);
        hideLoadingScreen();
    }

    private void getCurrentLocation(String selectedDestination) {
        Log.d("Location", "Getting current location");
        showLoadingScreen();

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double currentLatitude = location.getLatitude();
                    double currentLongitude = location.getLongitude();

                    // Set the origin based on the current location
                    LatLng origin = new LatLng(currentLatitude, currentLongitude);

                    // Update marker for current location
                    LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);
                    if (currentLocationMarker == null) {
                        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                    } else {
                        currentLocationMarker.setPosition(currentLatLng);
                    }

                    // Set the destination based on the selected option
                    LatLng destination = getDestinationLatLng(selectedDestination);

                    // Call showDirections() with the updated origin and destination
                    directionHelper.showDirections(origin, destination);
                }

            }
        };

        client.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private LatLng getDestinationLatLng(String selectedDestination) {
        // Implement logic to return the LatLng coordinates for the selected destination
        LatLng coordinates;
        switch (selectedDestination) {
            case "University of St. La Salle":
                coordinates = new LatLng(10.679891694036304, 122.96120524406435);
                break;
            case "Ayala Malls Capitol Center":
                coordinates = new LatLng(10.676554819361021, 122.95060515403749);
                break;
            case "SM City Bacolod":
                coordinates = new LatLng(10.671177787826965, 122.94367432594301);
                break;
            default:
                coordinates = new LatLng(0, 0); // Default coordinates if destination is not found
                break;
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
