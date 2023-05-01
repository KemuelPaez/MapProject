package com.example.testingalternateroutes;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

public class DirectionHelper {

    private GoogleMap mMap;
    private MainActivity mainActivity;
    private Polyline[] polylines;

    public DirectionHelper(GoogleMap googleMap, MainActivity activity) {
        mMap = googleMap;
        mainActivity = activity;
    }

    public void showDirections(LatLng origin, LatLng destination) {
        clearDirections();

        // Create a GeoApiContext with API key
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAioKwOOSrMpBuYj80F-eIsquHfaXMlOtI")
                .build();

        // Set the travel mode and other options and request for Direction API
        DirectionsApiRequest request = DirectionsApi.newRequest(context)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .mode(TravelMode.DRIVING)
                .alternatives(true);

        // Call the API and handle the response
        try {
            DirectionsResult result = request.await();
            if (result.routes != null && result.routes.length > 0) {
                int shortestRouteIndex = getShortestRouteIndex(result.routes);
                polylines = new Polyline[result.routes.length];
                for (int i = 0; i < result.routes.length; i++) {
                    DirectionsRoute route = result.routes[i];
                    List<LatLng> path = new ArrayList<>();

                    // Get the polyline path for each route
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    for (com.google.maps.model.LatLng latLng : decodedPath) {
                        path.add(new LatLng(latLng.lat, latLng.lng));
                    }

                    // Draw the polyline on the map
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .addAll(path)
                            .width(8f));

                    // prioritize the primary route which is the blue (shortest calculated route)
                    if (i == shortestRouteIndex) {
                        polyline.setColor(Color.BLUE);
                        polyline.setZIndex(1);
                    } else {
                        polyline.setColor(Color.GRAY);
                        polyline.setZIndex(0);
                    }

                    polylines[i] = polyline;
                }

                // Update camera bounds to show all the routes
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                for (Polyline polyline : polylines) {
                    for (LatLng point : polyline.getPoints()) {
                        boundsBuilder.include(point);
                    }
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));

                // Add a marker for the destination
                mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

                mainActivity.hideLoadingScreenAndEnableButton();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getShortestRouteIndex(DirectionsRoute[] routes) {
        int shortestRouteIndex = 0;
        long shortestDistance = Long.MAX_VALUE;
        for (int i = 0; i < routes.length; i++) {
            DirectionsRoute route = routes[i];
            long distance = route.legs[0].distance.inMeters;
            if (distance < shortestDistance) {
                shortestDistance = distance;
                shortestRouteIndex = i;
            }
        }
        return shortestRouteIndex;
    }

    private void clearDirections() {
        if (polylines != null) {
            for (Polyline polyline : polylines) {
                if (polyline != null) {
                    polyline.remove();
                }
            }
        }
        polylines = null; // Reset the polylines array to null
        mMap.clear();
    }
}