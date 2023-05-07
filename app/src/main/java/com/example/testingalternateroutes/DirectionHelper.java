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

        List<PatternItem> pattern = Arrays.asList(
                new Dash(10),    // 10px dash
                new Gap(20),     // 20px gap
                new Dash(10),    // 10px dash
                new Gap(20));    // 20px gap

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
                            .width(10f)
                            .pattern(pattern));

                    if (i == shortestRouteIndex) {
                        polyline.setColor(Color.BLUE);
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

                getNearestRouteChecker(origin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getNearestRouteChecker(LatLng destination){
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
        LatLng[] nboundCoordinates = LatLngData.getLatLngNboundPoints();

        for (LatLng point : nboundCoordinates) {
            if (areCoordinatesEqual(point, coordinate)) {
                return true;
            }
        }

        return false;
    }
    private boolean isCoordinateOnSanagRoute(LatLng coordinate) {
        LatLng[] sanagCoordinates = LatLngData.getLatLngSanagPoints();

        for (LatLng point : sanagCoordinates) {
            if (areCoordinatesEqual(point, coordinate)) {
                return true;
            }
        }

        return false;
    }
    private boolean isCoordinateOnBataRoute(LatLng coordinate) {
        LatLng[] bataCoordinates = LatLngData.getLatLngBataPoints();

        for (LatLng point : bataCoordinates) {
            if (areCoordinatesEqual(point, coordinate)) {
                return true;
            }
        }

        return false;
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

    private boolean areCoordinatesEqual(LatLng coordinate1, LatLng coordinate2) {
        double latitudeDifference = Math.abs(coordinate1.latitude - coordinate2.latitude);
        double longitudeDifference = Math.abs(coordinate1.longitude - coordinate2.longitude);

        return latitudeDifference < 1e-6 && longitudeDifference < 1e-6;
    }
}
