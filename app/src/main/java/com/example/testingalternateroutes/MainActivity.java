package com.example.testingalternateroutes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Spinner destinationSpinner;
    public Button btnDirections;
    private ImageButton btnClear;

    private DirectionHelper directionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDirections = findViewById(R.id.btn_directions);
        btnDirections.setEnabled(false);
        btnClear = findViewById(R.id.btn_clear);

        destinationSpinner = findViewById(R.id.spinner_destination);
        ArrayAdapter<String> destinationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        destinationSpinner.setAdapter(destinationAdapter);

        // Placeholder for hotspot destinations
        destinationAdapter.add("Choose a destination . . .");
        destinationAdapter.add("Northbound Terminal");
        destinationAdapter.add("Southbound Terminal");
        destinationAdapter.add("Robinsons Mandalagan");
        destinationAdapter.add("Riverside College");
        destinationAdapter.add("San-Agustin");
        destinationAdapter.add("University of St. La Salle");
        destinationAdapter.add("SM City Bacolod");
        destinationAdapter.add("888 China-Town");
        destinationAdapter.add("Cyber Center");
        destinationAdapter.add("City Mall - Mandalagan");

        // Call the map fragment
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

                // check if location permission is enable/disabled
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
        LatLngBounds BACOLOD_BOUNDS = new LatLngBounds(
                new LatLng(10.5809, 122.9014),  // Southwest corner
                new LatLng(10.7278, 123.0105)   // Northeast corner
        );
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
        mMap.setLatLngBoundsForCameraTarget(BACOLOD_BOUNDS);
        mMap.setMinZoomPreference(12);
        mMap.setMaxZoomPreference(18);

        // Set the camera position to show the bounds while waiting for the map to load
        mMap.setOnMapLoadedCallback(() -> mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bacolodBounds, 100)));

        btnClear.setOnClickListener(v -> mMap.clear());

        // Display the spinner
        selectRouteSpinner();

        // Initialize the directionHelper object
        directionHelper = new DirectionHelper(mMap, this);
        hideLoadingScreen();
    }

    private void getCurrentLocation(String selectedDestination) {
        Log.d("Location", "Getting current location");
        showLoadingScreen();

        // FusedLocation is enabled in order to track the users current position on the map
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the origin based on the current location
        // Set the destination based on the selected option
        LocationCallback locationCallback = new LocationCallback() {
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

                    // Call showDirections()
                    directionHelper.showDirections(origin, destination);
                }

            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void selectRouteSpinner() {
        // Method for the hotspot places in bacolod and show them on the map
        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean firstSelection = false;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!firstSelection) {
                    String selectedValue = parent.getItemAtPosition(position).toString();
                    // PolylineOptions polylineOptions = new PolylineOptions().width(10f).color(Color.RED);

                    if (!TextUtils.isEmpty(selectedValue)) {
                        switch (selectedValue) {
                            case "Choose a destination . . .":
                                mMap.clear();
                                btnDirections.setEnabled(false);
                                break;

                            case "Northbound Terminal":
                                LatLng nbound = new LatLng(10.707659998737006, 122.96218916932654);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nbound, 12));
                                mMap.addMarker(new MarkerOptions().position(nbound).title("Northbound Terminal"));
                                btnDirections.setEnabled(true);
                                break;

                            case "Southbound Terminal":
                                LatLng sbound = new LatLng(10.66245869801277, 122.95577918356845);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sbound, 12));
                                mMap.addMarker(new MarkerOptions().position(sbound).title("Southbound Terminal"));
                                btnDirections.setEnabled(true);
                                break;

                            case "Robinsons Mandalagan":
                                LatLng rob = new LatLng(10.690927897527251, 122.95898764824393);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rob, 12));
                                mMap.addMarker(new MarkerOptions().position(rob).title("Robinsons Mandalagan"));
                                btnDirections.setEnabled(true);
                                break;

                            case "Riverside College":
                                LatLng riverCollege = new LatLng(10.682625481061589, 122.95778065418398);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(riverCollege, 12));
                                mMap.addMarker(new MarkerOptions().position(riverCollege).title("Riverside College"));
                                btnDirections.setEnabled(true);
                                break;

                            case "San-Agustin":
                                LatLng sanag = new LatLng(10.681739876603684, 122.95790940022039);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sanag, 12));
                                mMap.addMarker(new MarkerOptions().position(sanag).title("San-Agustin"));
                                btnDirections.setEnabled(true);
                                break;

                            case "University of St. La Salle":
                                LatLng usls = new LatLng(10.6789, 122.9622);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(usls, 12));
                                mMap.addMarker(new MarkerOptions().position(usls).title("University of St. La Salle"));
                                btnDirections.setEnabled(true);
                                break;

                            case "SM City Bacolod":
                                LatLng sm = new LatLng(10.67028960557052, 122.94326470877894);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sm, 12));
                                mMap.addMarker(new MarkerOptions().position(sm).title("SM City Bacolod"));
                                btnDirections.setEnabled(true);
                                break;

                            case "888 China-Town":
                                LatLng chinaT = new LatLng(10.673431658688017, 122.94928373383625);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(chinaT, 12));
                                mMap.addMarker(new MarkerOptions().position(chinaT).title("888 China-Town"));
                                btnDirections.setEnabled(true);
                                break;

                            case "Cyber Center":
                                LatLng cc = new LatLng(10.661777088993766, 122.9471210932099);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cc, 12));
                                mMap.addMarker(new MarkerOptions().position(cc).title("Cyber Center"));
                                btnDirections.setEnabled(true);
                                break;

                            case "City Mall - Mandalagan":
                                LatLng cmM = new LatLng(10.695527446631967, 122.96122069253477);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cmM, 12));
                                mMap.addMarker(new MarkerOptions().position(cmM).title("City Mall - Mandalagan"));
                                btnDirections.setEnabled(true);
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
        // Method for getting the destination based on the users selection
        // Then pass it to the DirectionHelper class
        LatLng coordinates;
        switch (selectedDestination) {
            case "Northbound Terminal":
                coordinates = new LatLng(10.707659998737006, 122.96218916932654);
                break;

            case "Southbound Terminal":
                coordinates = new LatLng(10.66245869801277, 122.95577918356845);
                break;

            case "Robinsons Mandalagan":
                coordinates = new LatLng(10.690927897527251, 122.95898764824393);
                break;

            case "Riverside College":
                coordinates = new LatLng(10.683331854215057, 122.95919686054253);
                break;

            case "San-Agustin":
                coordinates = new LatLng(10.681739876603684, 122.95790940022039);
                break;

            case "University of St. La Salle":
                coordinates = new LatLng(10.678991164524232, 122.9622192813976);
                break;

            case "SM City Bacolod":
                coordinates = new LatLng(10.67028960557052, 122.94326470877894);
                break;

            case "888 China-Town":
                coordinates = new LatLng(10.673431658688017, 122.94928373383625);
                break;

            case "Cyber Center":
                coordinates = new LatLng(10.661777088993766, 122.9471210932099);
                break;

            case "City Mall - Mandalagan":
                coordinates = new LatLng(10.695527446631967, 122.96122069253477);
                break;

            default:
                coordinates = new LatLng(0, 0); // Default coordinates if destination is not found
                break;
        }

        return coordinates;
    }

    // hide/show Loading screen methods
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
