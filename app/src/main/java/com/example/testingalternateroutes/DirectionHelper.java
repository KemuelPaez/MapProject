package com.example.testingalternateroutes;

import android.app.AlertDialog;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
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
import java.util.Arrays;
import java.util.List;

public class DirectionHelper {

    private final GoogleMap mMap;
    private final MainActivity mainActivity;
    private Polyline[] polylines;

    public DirectionHelper(GoogleMap googleMap, MainActivity activity) {
        // Initiate maps
        mMap = googleMap;
        mainActivity = activity;
    }

    public void showDirections(LatLng origin, LatLng destination) {

        // Request from the GeoAPI
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAioKwOOSrMpBuYj80F-eIsquHfaXMlOtI")
                .build();

        // Request from the DirectionsAPI
        DirectionsApiRequest request = DirectionsApi.newRequest(context)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .mode(TravelMode.DRIVING)
                .alternatives(false)
                .region("ph");

        // Start calculating the routes
        try {
            DirectionsResult result = request.await();
            if (result.routes != null && result.routes.length > 0) {
                polylines = new Polyline[result.routes.length];
                for (int i = 0; i < result.routes.length; i++) {
                    DirectionsRoute route = result.routes[i];
                    Polyline polyline = createPolyline(route);
                    if (i == getShortestRouteIndex(result.routes)) {
                        polyline.setColor(Color.BLUE);
                        polyline.setZIndex(0);
                    } else {
                        polyline.setColor(Color.GRAY);
                        polyline.setZIndex(0);
                    }
                    polylines[i] = polyline;
                }
                LatLngBounds bounds = getLatLngBounds(polylines);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                mainActivity.hideLoadingScreenAndEnableButton();
                getNearestRouteChecker(origin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Polyline createPolyline(DirectionsRoute route){
        // Dash pattern polyline settings
        List<PatternItem> pattern = Arrays.asList(
                new Dash(10),    // 10px dash
                new Gap(20),     // 20px gap
                new Dash(10),    // 10px dash
                new Gap(20));    // 20px gap

        // create polyline options
        List<LatLng> path = decodePolyline(route.overviewPolyline.getEncodedPath());
        return mMap.addPolyline(new PolylineOptions()
                .addAll(path)
                .width(10f)
                .pattern(pattern));
    }
    private List<LatLng> decodePolyline(String encodedPath) {
        // decode the polylines on the array
        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(encodedPath);
        List<LatLng> path = new ArrayList<>();
        for (com.google.maps.model.LatLng latLng : decodedPath) {
            path.add(new LatLng(latLng.lat, latLng.lng));
        }
        return path;
    }
    private LatLngBounds getLatLngBounds(Polyline[] polylines) {
        // calculate the bounds of the polylines and then zoom the camera to fit all of them.
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Polyline polyline : polylines) {
            List<LatLng> points = polyline.getPoints();
            for (LatLng point : points) {
                builder.include(point);
            }
        }
        return builder.build();
    }
    private int getShortestRouteIndex(DirectionsRoute[] routes) {
        // Greedy Search algo for calculating the shortest route based on the plotted coordinates
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
    private void getNearestRouteChecker(LatLng destination){
        // A checker method if the user is within the nearest route to be recommended
        LatLng nearestCoordinate = LatLngData.getNearestCoordinate(destination);
        PolylineOptions polylineOptions = new PolylineOptions().width(10f).color(Color.RED);

        if (nearestCoordinate != null) {
            // Check if the nearest coordinate is on the nearest point of route & display the route
            if (isCoordinateOnNboundRoute(nearestCoordinate)) {
                showRouteSuggestionDialog("Northbound");
                LatLng[] nboundPoints = LatLngData.getLatLngNboundPoints();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nboundPoints[0], 12));
                mMap.addMarker(new MarkerOptions().position(nboundPoints[0]).title("Northbound Terminal"));

                for (LatLng point : nboundPoints) {
                    polylineOptions.add(point);
                }

                mMap.addPolyline(polylineOptions);

            } else if (isCoordinateOnSanagRoute(nearestCoordinate)){
                showRouteSuggestionDialog("San-Agustin");
                LatLng[] sanagPoints = LatLngData.getLatLngSanagPoints();
                //  mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sanagPoints[0], 12));
                mMap.addMarker(new MarkerOptions().position(sanagPoints[0]).title("San-Agustin Start"));

                for (LatLng point : sanagPoints) {
                    polylineOptions.add(point);
                }
                mMap.addPolyline(polylineOptions);

            } else if (isCoordinateOnBataRoute(nearestCoordinate)){
                showRouteSuggestionDialog("Bata");

                LatLng[] bataPoints = LatLngData.getLatLngBataPoints();
                // mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bataPoints[0], 12));
                mMap.addMarker(new MarkerOptions().position(bataPoints[0]).title("Bata Start"));

                for (LatLng point : bataPoints) {
                    polylineOptions.add(point);
                }
                mMap.addPolyline(polylineOptions);
                
            } else {
                return;
            }
        }
    }
    private void showRouteSuggestionDialog(String routeName) {
        // placeholder if nearest route is suggested
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Route Suggestion")
                .setMessage("We suggest taking the " + routeName + " jeepney route. The red line routes indicates the route of the "+ routeName
                        +" jeepneys. Possible double ride if you want to get to your specific location.")
                .setPositiveButton("OK", (dialog, which) -> {

                })
                .setCancelable(false)
                .show();
    }
    private boolean isCoordinateOnNboundRoute(LatLng coordinate) {
        // check if coordinate is on the northbound route
        LatLng[] nboundCoordinates = LatLngData.getLatLngNboundPoints();

        for (LatLng point : nboundCoordinates) {
            if (areCoordinatesEqual(point, coordinate)) {
                return true;
            }
        }

        return false;
    }
    private boolean isCoordinateOnSanagRoute(LatLng coordinate) {
        // check if coordinate is on the san-ag route
        LatLng[] sanagCoordinates = LatLngData.getLatLngSanagPoints();

        for (LatLng point : sanagCoordinates) {
            if (areCoordinatesEqual(point, coordinate)) {
                return true;
            }
        }

        return false;
    }
    private boolean isCoordinateOnBataRoute(LatLng coordinate) {
        // check if coordinate is on the bata route
        LatLng[] bataCoordinates = LatLngData.getLatLngBataPoints();

        for (LatLng point : bataCoordinates) {
            if (areCoordinatesEqual(point, coordinate)) {
                return true;
            }
        }

        return false;
    }
    private boolean areCoordinatesEqual(LatLng coordinate1, LatLng coordinate2) {
        // The purpose of this method is likely to compare LatLng coordinates when checking for the nearest route in getNearestRouteChecker() method.
        double latitudeDifference = Math.abs(coordinate1.latitude - coordinate2.latitude);
        double longitudeDifference = Math.abs(coordinate1.longitude - coordinate2.longitude);

        return latitudeDifference < 1e-6 && longitudeDifference < 1e-6;
    }

}
