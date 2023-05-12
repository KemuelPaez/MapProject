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
import android.widget.TextView;
import android.widget.Toast;

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
    public TextView jeepRoute;
    private ImageButton btnClear;

    private DirectionHelper directionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDirections = findViewById(R.id.btn_directions);
        btnDirections.setEnabled(false);
        btnClear = findViewById(R.id.btn_clear);
        jeepRoute = findViewById(R.id.jeep_route);
        destinationSpinner = findViewById(R.id.spinner_destination);

        ArrayAdapter<String> destinationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        destinationSpinner.setAdapter(destinationAdapter);

        // Placeholder for hotspot destinations
        destinationAdapter.add("Choose a destination . . .");
        // Terminals
        destinationAdapter.add("Northbound Terminal");
        destinationAdapter.add("Southbound Terminal");
        // Schools
        destinationAdapter.add("University of St. La Salle");
        destinationAdapter.add("Riverside College");
        destinationAdapter.add("Colegio San-Agustin");
        destinationAdapter.add("STI West Negros");
        // Malls
        destinationAdapter.add("Robinsons Triangle");
        destinationAdapter.add("SM City Bacolod");
        destinationAdapter.add("Lopues San Sebastian");
        destinationAdapter.add("Ayala Malls Capitol");
        // Hospitals
        destinationAdapter.add("Corazon Locsin Montelibano Hospital");
        destinationAdapter.add("Metro Bacolod Hospital");
        // Church
        destinationAdapter.add("San Sebastian Cathedral");

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
        mMap.setMaxZoomPreference(12);

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

        btnClear.setOnClickListener(v -> {
            mMap.clear();
            jeepRoute.setText("- - - -");
            Toast.makeText(MainActivity.this, "Map Cleared!", Toast.LENGTH_SHORT).show();
        });

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
                    directionHelper.showDirections(origin, destination, jeepRoute);
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
                                LatLng sbound = new LatLng(10.66756766693566, 122.95817815957436);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sbound, 12));
                                mMap.addMarker(new MarkerOptions().position(sbound).title("Southbound Terminal"));
                                btnDirections.setEnabled(true);
                                break;

                            case "University of St. La Salle":
                                LatLng usls = new LatLng(10.6789, 122.9622);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(usls, 12));
                                mMap.addMarker(new MarkerOptions().position(usls).title("University of St. La Salle"));
                                btnDirections.setEnabled(true);
                                break;

                            case "Riverside College":
                                LatLng river = new LatLng(10.670306561428967, 122.96512790774199);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(river, 12));
                                mMap.addMarker(new MarkerOptions().position(river).title("Riverside College"));
                                btnDirections.setEnabled(true);
                                break;

                            case "Colegio San-Agustin":
                                LatLng sanag = new LatLng(10.681414072379999, 122.95811847249533);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sanag, 12));
                                mMap.addMarker(new MarkerOptions().position(sanag).title("Colegio San-Agustin"));
                                btnDirections.setEnabled(true);
                                break;

                            case "STI West Negros":
                                LatLng sti = new LatLng(10.668829817338267, 122.95701733496746);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sti, 12));
                                mMap.addMarker(new MarkerOptions().position(sti).title("STI West Negros"));
                                btnDirections.setEnabled(true);
                                break;

                            case "Robinsons Triangle":
                                LatLng rob = new LatLng(10.674525150258413, 122.96130982243098);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rob, 12));
                                mMap.addMarker(new MarkerOptions().position(rob).title("Robinsons Triangle"));
                                btnDirections.setEnabled(true);
                                break;

                            case "SM City Bacolod":
                                LatLng sm = new LatLng(10.670991978094452, 122.94419443642798);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sm, 12));
                                mMap.addMarker(new MarkerOptions().position(sm).title("SM City Bacolod"));
                                btnDirections.setEnabled(true);
                                break;

                            case "Lopues San Sebastian":
                                LatLng lopues = new LatLng(10.666409116421695, 122.94431732174264);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lopues, 12));
                                mMap.addMarker(new MarkerOptions().position(lopues).title("Lopues San Sebastian"));
                                btnDirections.setEnabled(true);
                                break;

                            case "Ayala Malls Capitol":
                                LatLng ayala = new LatLng(10.676798889527337, 122.95070771290528);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ayala, 12));
                                mMap.addMarker(new MarkerOptions().position(ayala).title("Ayala Malls Capitol"));
                                btnDirections.setEnabled(true);
                                break;

                            case "Corazon Locsin Montelibano Hospital":
                                LatLng clmHospital = new LatLng(10.67213087800615, 122.95135751709138);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clmHospital, 12));
                                mMap.addMarker(new MarkerOptions().position(clmHospital).title("Corazon Locsin Montelibano Hospital"));
                                btnDirections.setEnabled(true);
                                break;

                            case "Metro Bacolod Hospital":
                                LatLng mbh = new LatLng(10.660875612177502, 122.98509013687979);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mbh, 12));
                                mMap.addMarker(new MarkerOptions().position(mbh).title("Metro Bacolod Hospital"));
                                btnDirections.setEnabled(true);
                                break;

                            case "San Sebastian Cathedral":
                                LatLng sanSeb = new LatLng(10.66984235523919, 122.94686393646654);
                                mMap.clear();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sanSeb, 12));
                                mMap.addMarker(new MarkerOptions().position(sanSeb).title("San Sebastian Cathedral"));
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
                coordinates = new LatLng(10.66756766693566, 122.95817815957436);
                break;

            case "University of St. La Salle":
                coordinates = new LatLng(10.678991164524232, 122.9622192813976);
                break;

            case "Riverside College":
                coordinates = new LatLng(10.683244545105675, 122.95884682268951);
                break;

            case "Colegio San-Agustin":
                coordinates = new LatLng(10.681414072379999, 122.95811847249533);
                break;

            case "STI West Negros":
                coordinates = new LatLng(10.668829817338267, 122.95701733496746);
                break;

            case "Robinsons Triangle":
                coordinates = new LatLng(10.674525150258413, 122.96130982243098);
                break;

            case "SM City Bacolod":
                coordinates = new LatLng(10.670991978094452, 122.94419443642798);
                break;

            case "Lopues San Sebastian":
                coordinates = new LatLng(10.666409116421695, 122.94431732174264);
                break;

            case "Ayala Malls Capitol":
                coordinates = new LatLng(10.676798889527337, 122.95070771290528);
                break;

            case "Corazon Locsin Montelibano Hospital":
                coordinates = new LatLng(10.67213087800615, 122.95135751709138);
                break;

            case "Metro Bacolod Hospital":
                coordinates = new LatLng(10.660875612177502, 122.98509013687979);
                break;

            case "San Sebastian Cathedral":
                coordinates = new LatLng(10.66984235523919, 122.94686393646654);
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
