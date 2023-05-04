package com.example.testingalternateroutes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAioKwOOSrMpBuYj80F-eIsquHfaXMlOtI")
                .build();

        DirectionsApiRequest request = DirectionsApi.newRequest(context)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .mode(TravelMode.DRIVING)
                .alternatives(false)
                .region("ph");

        try {
            DirectionsResult result = request.await();
            if (result.routes != null && result.routes.length > 0) {
                int shortestRouteIndex = getShortestRouteIndex(result.routes);
                polylines = new Polyline[result.routes.length];
                for (int i = 0; i < result.routes.length; i++) {
                    DirectionsRoute route = result.routes[i];
                    List<LatLng> path = new ArrayList<>();
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    for (com.google.maps.model.LatLng latLng : decodedPath) {
                        path.add(new LatLng(latLng.lat, latLng.lng));
                    }

                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .addAll(path)
                            .width(10f));

                    if (i == shortestRouteIndex) {
                        polyline.setColor(Color.TRANSPARENT);
                        polyline.setZIndex(1);
                    } else {
                        polyline.setColor(Color.GRAY);
                        polyline.setZIndex(0);
                    }

                    polylines[i] = polyline;
                }

                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                for (Polyline polyline : polylines) {
                    for (LatLng point : polyline.getPoints()) {
                        boundsBuilder.include(point);
                    }
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));

                mainActivity.hideLoadingScreenAndEnableButton();

                LatLng nearestCoordinate = LatLngData.getNearestCoordinate(origin);
                if (nearestCoordinate != null) {
                    // Check if the nearest coordinate is on the Nbound jeepney route
                    if (isCoordinateOnNboundRoute(nearestCoordinate)) {
                        showRouteSuggestionDialog("Northbound");
                    }
                }
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

    private boolean isCoordinateOnNboundRoute(LatLng coordinate) {
        LatLng[] nboundCoordinates = LatLngData.getLatLngNboundPoints();

        for (LatLng point : nboundCoordinates) {
            if (areCoordinatesEqual(point, coordinate)) {
                return true;
            }
        }

        return false;
    }

    private boolean areCoordinatesEqual(LatLng coordinate1, LatLng coordinate2) {
        double latitudeDifference = Math.abs(coordinate1.latitude - coordinate2.latitude);
        double longitudeDifference = Math.abs(coordinate1.longitude - coordinate2.longitude);

        return latitudeDifference < 1e-6 && longitudeDifference < 1e-6;
    }

    private void showRouteSuggestionDialog(String routeName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Route Suggestion")
                .setMessage("We suggest taking the " + routeName + " jeepney route. The red line routes indicates the route of the "+ routeName
                        +" jeepneys. Possible double ride if you want to get to your specific location.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the OK button click if needed
                    }
                })
                .setCancelable(false)
                .show();
    }
}


//    private void clearDirections() {
//        if (polylines != null) {
//            for (Polyline polyline : polylines) {
//                if (polyline != null) {
//                    polyline.remove();
//                }
//            }
//        }
//        polylines = null; // Reset the polylines array to null
//        mMap.clear();
//    }
