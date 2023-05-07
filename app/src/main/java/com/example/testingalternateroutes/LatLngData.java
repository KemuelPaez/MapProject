package com.example.testingalternateroutes;

import com.google.android.gms.maps.model.LatLng;

public class LatLngData {

    // Remember to add every new points method into getNearestCoordinate method
    public static LatLng getNearestCoordinate(LatLng userLocation) {
        // An array of arrays, where each sub-array represents the coordinates of a different route
        LatLng[][] routeCoordinates = {
                getLatLngNboundPoints(),
                getLatLngSanagPoints(),
                getLatLngBataPoints(),
                // Add new route coordinates here
        };

        // Check if there are any route coordinates
        boolean hasCoordinates = false;
        for (LatLng[] coordinates : routeCoordinates) {
            if (coordinates.length > 0) {
                hasCoordinates = true;
                break;
            }
        }

        if (!hasCoordinates) {
            return null; // Return null if there are no route coordinates
        }

        // Initialize the nearestCoordinate and shortestDistance variables to the first coordinate in the first route
        LatLng nearestCoordinate = routeCoordinates[0][0];
        double shortestDistance = calculateDistance(userLocation, nearestCoordinate);

        // Iterate through all the route coordinates and find the nearest one to the user's location
        for (LatLng[] coordinates : routeCoordinates) {
            for (LatLng coordinate : coordinates) {
                double distance = calculateDistance(userLocation, coordinate);

                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    nearestCoordinate = coordinate;
                }
            }
        }

        return nearestCoordinate;
    }
    private static double calculateDistance(LatLng point1, LatLng point2) {
        // using Haversine formula:
        double lat1 = Math.toRadians(point1.latitude);
        double lon1 = Math.toRadians(point1.longitude);
        double lat2 = Math.toRadians(point2.latitude);
        double lon2 = Math.toRadians(point2.longitude);

        double earthRadius = 6371; // Radius of the Earth in kilometers
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    // Method of manually plotted coordinates to be connected with polylines.
    public static LatLng[] getLatLngNboundPoints() {
        return new LatLng[]{
                // northbound
                new LatLng(10.707486, 122.962302),
                new LatLng(10.707486, 122.962302),
                new LatLng(10.706395, 122.962270),
                new LatLng(10.705454, 122.962265),
                new LatLng(10.704766, 122.962235),
                new LatLng(10.703759, 122.962238),
                new LatLng(10.703042, 122.962278),
                new LatLng(10.702317, 122.962240),
                new LatLng(10.701658, 122.962232),
                new LatLng(10.701039, 122.962213),
                new LatLng(10.700290, 122.962213),
                new LatLng(10.699737, 122.962194),
                new LatLng(10.699210, 122.962189),
                new LatLng(10.698722, 122.962127),
                new LatLng(10.698145, 122.961993),
                new LatLng(10.697610, 122.961821),
                new LatLng(10.697059, 122.961596),
                new LatLng(10.696477, 122.961365),
                new LatLng(10.695960, 122.961148),
                new LatLng(10.695325, 122.960912),
                new LatLng(10.694750, 122.960684),
                new LatLng(10.694236, 122.960486),
                new LatLng(10.693695, 122.960256),
                new LatLng(10.693147, 122.960041),
                new LatLng(10.692596, 122.959805),
                new LatLng(10.692053, 122.959588),
                new LatLng(10.691347, 122.959304),
                new LatLng(10.690791, 122.959076),
                new LatLng(10.690079, 122.958786),
                new LatLng(10.689547, 122.958588),
                new LatLng(10.689115, 122.958408),
                new LatLng(10.688530, 122.958169),
                new LatLng(10.688013, 122.957946),
                new LatLng(10.687446, 122.957737),
                new LatLng(10.687217, 122.957638),
                new LatLng(10.686785, 122.957480),
                new LatLng(10.686289, 122.957257),
                new LatLng(10.685612, 122.956981),
                new LatLng(10.685056, 122.956758),
                new LatLng(10.684729, 122.956613),
                new LatLng(10.684257, 122.956415),
                new LatLng(10.684033, 122.956332),
                new LatLng(10.683925, 122.956608),
                new LatLng(10.683727, 122.956975),
                new LatLng(10.683353, 122.957262),
                new LatLng(10.682810, 122.957538),
                new LatLng(10.682354, 122.957707),
                new LatLng(10.681974, 122.957892),
                new LatLng(10.681336, 122.958241),
                new LatLng(10.680632, 122.958574),
                new LatLng(10.680015, 122.958877),
                new LatLng(10.679303, 122.959253),
                new LatLng(10.678615, 122.959607),
                new LatLng(10.678022, 122.959897),
                new LatLng(10.678022, 122.959897),
                new LatLng(10.676939, 122.960420),
                new LatLng(10.676422, 122.960720),
                new LatLng(10.675850, 122.960967),
                new LatLng(10.675428, 122.961120),
                new LatLng(10.675162, 122.961168),
                new LatLng(10.674782, 122.961184),
                new LatLng(10.674318, 122.961077),
                new LatLng(10.673670, 122.960822),
                new LatLng(10.673048, 122.960573),
                new LatLng(10.672402, 122.960265),
                new LatLng(10.671672, 122.959943),
                new LatLng(10.670939, 122.959656),
                new LatLng(10.670082, 122.959240),
                new LatLng(10.669220, 122.958883),
                new LatLng(10.668543, 122.958569),
                new LatLng(10.668210, 122.958413),
                new LatLng(10.668455, 122.957850),
                new LatLng(10.668674, 122.957343),
                new LatLng(10.668822, 122.956930),
                new LatLng(10.669012, 122.956530),
                new LatLng(10.669244, 122.956007),
                new LatLng(10.669384, 122.955693),
                new LatLng(10.669737, 122.955854),
                new LatLng(10.670433, 122.956160),
                new LatLng(10.670929, 122.956409),
                new LatLng(10.671169, 122.956557),
                new LatLng(10.671124, 122.956664),
                new LatLng(10.671003, 122.957010),
                new LatLng(10.670818, 122.957488),
                new LatLng(10.670730, 122.957718),
                new LatLng(10.670582, 122.958176),
                new LatLng(10.670440, 122.958635),
                new LatLng(10.670337, 122.958950),
                new LatLng(10.670226, 122.959355)
        };
    }

    public static LatLng[] getLatLngSanagPoints() {
        return new LatLng[]{
                // San-agustin
                new LatLng(10.682333725205815, 122.95767946854444),
                new LatLng(10.679547709861753, 122.95909882730442),
                new LatLng(10.678573871582884, 122.95962536889091),
                new LatLng(10.677082605078187, 122.96034505236199),
                new LatLng(10.675905403163132, 122.96092217872571),
                new LatLng(10.675232068730768, 122.96117280332291),
                new LatLng(10.67461130547355, 122.96116140186187),
                new LatLng(10.673403243247314, 122.96070006275654),
                new LatLng(10.672288352537398, 122.96018716068725),
                new LatLng(10.6695135859811, 122.9589773795523),
                new LatLng(10.66821041042915, 122.95841186600875),
                new LatLng(10.66796660986726, 122.95836447831643),
                new LatLng(10.666898165254635, 122.95788748521241),
                new LatLng(10.664807227645973, 122.95697570683046),
                new LatLng(10.663410770059304, 122.95637848546643),
                new LatLng(10.662641004941115, 122.95607743425035),
                new LatLng(10.662032861723944, 122.95582098321414),
                new LatLng(10.66178083804747, 122.95567324511494),
                new LatLng(10.661665783689276, 122.95549205687874),
                new LatLng(10.661389105186574, 122.95446067771675),
                new LatLng(10.661208087766916, 122.95377557394997),
                new LatLng(10.660955010423566, 122.95205339209518),
                new LatLng(10.660738086817378, 122.95092213244911),
                new LatLng(10.660321994694758, 122.95058899569058),
                new LatLng(10.660472553832143, 122.95021613244208),
                new LatLng(10.660820032472353, 122.94944057610925),
                new LatLng(10.661535902981647, 122.94980009716082),
                new LatLng(10.66199866945143, 122.95004571390265),
                new LatLng(10.663170188069099, 122.95064332915986),
                new LatLng(10.664057279878199, 122.9510328741866),
                new LatLng(10.665099647879595, 122.9515209787971),
                new LatLng(10.666249359276806, 122.9520124333374),
                new LatLng(10.667324850078975, 122.94944966029938),
                new LatLng(10.66781065784526, 122.94821263246261),
                new LatLng(10.66897207418349, 122.94872537819685),
                new LatLng(10.669528315733626, 122.94892866423001),
                new LatLng(10.670400581795345, 122.94927953684895),
                new LatLng(10.671826395944864, 122.94986748557196),
                new LatLng(10.67137676750953, 122.95095394804443),
                new LatLng(10.6712627682153, 122.95110616340635),
                new LatLng(10.670633486703931, 122.95253046169233),
                new LatLng(10.670051454023744, 122.95397470121868),
                new LatLng(10.669605844543703, 122.95491991974343),
                new LatLng(10.669338402115036, 122.95568361055818),
                new LatLng(10.668479941183968, 122.95769791359676),
                new LatLng(10.668118268864461, 122.95855941432754),
                new LatLng(10.669860877404929, 122.95925364138613),
                new LatLng(10.672377018939295, 122.96038781296633),
                new LatLng(10.674099166041884, 122.96115404260091),
                new LatLng(10.674494289326951, 122.9612830119455),
                new LatLng(10.675359085654492, 122.96127921873007),
                new LatLng(10.676730826503958, 122.96065713131534),
                new LatLng(10.67864305274898, 122.95973158661833),
                new LatLng(10.681197128220129, 122.95840756097428),
                new LatLng(10.681320135477636, 122.95875274362237),
                new LatLng(10.6828865451059, 122.95940912620891),
                new LatLng(10.68320863648021, 122.9587484195964),
                new LatLng(10.683497757572459, 122.95864776507356),
                new LatLng(10.683731083165531, 122.95863486064758),
                new LatLng(10.684103896519765, 122.95774703612486),
                new LatLng(10.682528947206645, 122.95713536632692),
                new LatLng(10.682336199220503, 122.95764896248222)
        };
    }

    public static LatLng[] getLatLngBataPoints() {
        return new LatLng[]{
                // BATA
                new LatLng(10.702267, 122.974835),
                new LatLng(10.702383, 122.974197),
                new LatLng(10.702591, 122.973164),
                new LatLng(10.702812, 122.972027),
                new LatLng(10.702996, 122.971075),
                new LatLng(10.703154, 122.970163),
                new LatLng(10.703336, 122.969281),
                new LatLng(10.703486, 122.968600),
                new LatLng(10.703641, 122.967852),
                new LatLng(10.703783, 122.967240),
                new LatLng(10.703944, 122.966457),
                new LatLng(10.703944, 122.966457),
                new LatLng(10.704268, 122.964955),
                new LatLng(10.704406, 122.964284),
                new LatLng(10.704572, 122.963503),
                new LatLng(10.704643, 122.963058),
                new LatLng(10.704754, 122.962564),
                new LatLng(10.704770, 122.962384),
                new LatLng(10.705938, 122.962405),
                new LatLng(10.706903, 122.962402),
                new LatLng(10.707702, 122.962375),
                new LatLng(10.708234, 122.962362),
                new LatLng(10.708948, 122.962268),
                new LatLng(10.708937, 122.962150),
                new LatLng(10.708302, 122.962206),
                new LatLng(10.707812, 122.962241),
                new LatLng(10.707486, 122.962302),
                new LatLng(10.707486, 122.962302),
                new LatLng(10.706395, 122.962270),
                new LatLng(10.705454, 122.962265),
                new LatLng(10.704766, 122.962235),
                new LatLng(10.703759, 122.962238),
                new LatLng(10.703042, 122.962278),
                new LatLng(10.702317, 122.962240),
                new LatLng(10.701658, 122.962232),
                new LatLng(10.701039, 122.962213),
                new LatLng(10.700290, 122.962213),
                new LatLng(10.699737, 122.962194),
                new LatLng(10.699210, 122.962189),
                new LatLng(10.698722, 122.962127),
                new LatLng(10.698145, 122.961993),
                new LatLng(10.697610, 122.961821),
                new LatLng(10.697059, 122.961596),
                new LatLng(10.696477, 122.961365),
                new LatLng(10.695960, 122.961148),
                new LatLng(10.695325, 122.960912),
                new LatLng(10.694750, 122.960684),
                new LatLng(10.694236, 122.960486),
                new LatLng(10.693695, 122.960256),
                new LatLng(10.693147, 122.960041),
                new LatLng(10.692596, 122.959805),
                new LatLng(10.692053, 122.959588),
                new LatLng(10.691347, 122.959304),
                new LatLng(10.690791, 122.959076),
                new LatLng(10.690079, 122.958786),
                new LatLng(10.689547, 122.958588),
                new LatLng(10.689115, 122.958408),
                new LatLng(10.688530, 122.958169),
                new LatLng(10.688013, 122.957946),
                new LatLng(10.687446, 122.957737),
                new LatLng(10.687217, 122.957638),
                new LatLng(10.686785, 122.957480),
                new LatLng(10.686289, 122.957257),
                new LatLng(10.685612, 122.956981),
                new LatLng(10.685056, 122.956758),
                new LatLng(10.684729, 122.956613),
                new LatLng(10.684257, 122.956415),
                new LatLng(10.684033, 122.956332),
                new LatLng(10.684033, 122.956332),
                new LatLng(10.683291, 122.956021),
                new LatLng(10.683030, 122.955911),
                new LatLng(10.682645, 122.955772),
                new LatLng(10.682292, 122.955608),
                new LatLng(10.681907, 122.955452),
                new LatLng(10.681549, 122.955291),
                new LatLng(10.681038, 122.955093),
                new LatLng(10.680564, 122.954903),
                new LatLng(10.680084, 122.954680),
                new LatLng(10.679383, 122.954380),
                new LatLng(10.678919, 122.954195),
                new LatLng(10.678471, 122.954018),
                new LatLng(10.677860, 122.953742),
                new LatLng(10.677277, 122.953506),
                new LatLng(10.676942, 122.953372),
                new LatLng(10.676549, 122.953227),
                new LatLng(10.676461, 122.953185),
                new LatLng(10.676601, 122.952842),
                new LatLng(10.676772, 122.952416),
                new LatLng(10.676925, 122.952032),
                new LatLng(10.677070, 122.951630),
                new LatLng(10.677249, 122.951228),
                new LatLng(10.677344, 122.950967),
                new LatLng(10.677140, 122.950878),
                new LatLng(10.676874, 122.950763),
                new LatLng(10.676516, 122.950610),
                new LatLng(10.676084, 122.950430),
                new LatLng(10.676084, 122.950430),
                new LatLng(10.675536, 122.950124),
                new LatLng(10.674758, 122.949799),
                new LatLng(10.674252, 122.949582),
                new LatLng(10.673709, 122.949351),
                new LatLng(10.673492, 122.949251),
                new LatLng(10.673087, 122.949062),
                new LatLng(10.672636, 122.948862),
                new LatLng(10.672217, 122.948692),
                new LatLng(10.672131, 122.948677),
                new LatLng(10.671813, 122.948521),
                new LatLng(10.671487, 122.948359),
                new LatLng(10.671227, 122.948237),
                new LatLng(10.670983, 122.948119),
                new LatLng(10.670751, 122.948030),
                new LatLng(10.670511, 122.947936),
                new LatLng(10.670216, 122.947819),
                new LatLng(10.670006, 122.947726),
                new LatLng(10.669716, 122.947584),
                new LatLng(10.669547, 122.947489),
                new LatLng(10.669712, 122.947103),
                new LatLng(10.669826, 122.946868),
                new LatLng(10.669976, 122.946523),
                new LatLng(10.670087, 122.946273),
                new LatLng(10.670173, 122.946073),
                new LatLng(10.669917, 122.945866),
                new LatLng(10.669649, 122.945661),
                new LatLng(10.669275, 122.945374),
                new LatLng(10.668932, 122.945111),
                new LatLng(10.668552, 122.944848),
                new LatLng(10.668178, 122.944566),
                new LatLng(10.667835, 122.944335),
                new LatLng(10.667583, 122.944169),
                new LatLng(10.667254, 122.943949),
                new LatLng(10.666944, 122.943728),
                new LatLng(10.666740, 122.943579),
                new LatLng(10.666683, 122.943684),
                new LatLng(10.666538, 122.943979),
                new LatLng(10.666419, 122.944235),
                new LatLng(10.666306, 122.944470),
                new LatLng(10.666224, 122.944648),
                new LatLng(10.666163, 122.944762),
                new LatLng(10.665683, 122.944511),
                new LatLng(10.665364, 122.944329),
                new LatLng(10.665056, 122.944165),
                new LatLng(10.664748, 122.944026),
                new LatLng(10.664748, 122.944026),
                new LatLng(10.664139, 122.943713),
                new LatLng(10.663730, 122.943517),
                new LatLng(10.663308, 122.943294),
                new LatLng(10.662886, 122.943082),
                new LatLng(10.662886, 122.943082),
                new LatLng(10.661871, 122.942589),
                new LatLng(10.661558, 122.942412),
                new LatLng(10.661424, 122.942350),
                new LatLng(10.661364, 122.942456),
                new LatLng(10.661245, 122.942716),
                new LatLng(10.661184, 122.942868),
                new LatLng(10.661080, 122.943105),
                new LatLng(10.660992, 122.943319),
                new LatLng(10.660864, 122.943602),
                new LatLng(10.660711, 122.943917),
                new LatLng(10.660569, 122.944217),
                new LatLng(10.660439, 122.944494),
                new LatLng(10.660378, 122.944624),
                new LatLng(10.660249, 122.944892),
                new LatLng(10.660149, 122.945093),
                new LatLng(10.660033, 122.945334),
                new LatLng(10.659914, 122.945575),
                new LatLng(10.659745, 122.945929),
                new LatLng(10.659566, 122.946318),
                new LatLng(10.659355, 122.946782),
                new LatLng(10.659184, 122.947176),
                new LatLng(10.658989, 122.947586),
                new LatLng(10.658812, 122.947916),
                new LatLng(10.658630, 122.948257),
                new LatLng(10.658474, 122.948622),
                new LatLng(10.658257, 122.949099),
                new LatLng(10.658086, 122.949491),
                new LatLng(10.658045, 122.949688),
                new LatLng(10.658100, 122.949867),
                new LatLng(10.658192, 122.950138),
                new LatLng(10.658274, 122.950418),
                new LatLng(10.658346, 122.950775),
                new LatLng(10.658454, 122.951049),
                new LatLng(10.658586, 122.951315),
                new LatLng(10.658886, 122.951682),
                new LatLng(10.659320, 122.952106),
                new LatLng(10.659570, 122.952296),
                new LatLng(10.659668, 122.952383),
                new LatLng(10.659668, 122.952383),
                new LatLng(10.660041, 122.951357),
                new LatLng(10.660204, 122.950906),
                new LatLng(10.660418, 122.950323),
                new LatLng(10.660621, 122.949875),
                new LatLng(10.660821, 122.949400),
                new LatLng(10.661156, 122.948662),
                new LatLng(10.661459, 122.947989),
                new LatLng(10.661662, 122.947525),
                new LatLng(10.661866, 122.947053),
                new LatLng(10.662080, 122.946584),
                new LatLng(10.662312, 122.946131),
                new LatLng(10.662384, 122.946010),
                new LatLng(10.662904, 122.946227),
                new LatLng(10.663228, 122.946359),
                new LatLng(10.663629, 122.946528),
                new LatLng(10.664105, 122.946722),
                new LatLng(10.664495, 122.946876),
                new LatLng(10.664739, 122.946971),
                new LatLng(10.664954, 122.947060),
                new LatLng(10.665168, 122.947153),
                new LatLng(10.665459, 122.947269),
                new LatLng(10.665736, 122.947384),
                new LatLng(10.665990, 122.947484),
                new LatLng(10.666290, 122.947613),
                new LatLng(10.666521, 122.947710),
                new LatLng(10.666783, 122.947815),
                new LatLng(10.667132, 122.947949),
                new LatLng(10.667396, 122.948052),
                new LatLng(10.667636, 122.948150),
                new LatLng(10.667797, 122.948213),
                new LatLng(10.667949, 122.947816),
                new LatLng(10.668074, 122.947512),
                new LatLng(10.668169, 122.947274),
                new LatLng(10.668272, 122.947022),
                new LatLng(10.668350, 122.947014),
                new LatLng(10.668386, 122.946977),
                new LatLng(10.668646, 122.947074),
                new LatLng(10.668904, 122.947176),
                new LatLng(10.669152, 122.947278),
                new LatLng(10.669381, 122.947404),
                new LatLng(10.669543, 122.947495),
                new LatLng(10.669391, 122.947824),
                new LatLng(10.669257, 122.948123),
                new LatLng(10.669100, 122.948460),
                new LatLng(10.668956, 122.948786),
                new LatLng(10.668824, 122.949092),
                new LatLng(10.668738, 122.949321),
                new LatLng(10.668592, 122.949642),
                new LatLng(10.668592, 122.949642),
                new LatLng(10.668470, 122.949923),
                new LatLng(10.668834, 122.950071),
                new LatLng(10.669314, 122.950272),
                new LatLng(10.669670, 122.950422),
                new LatLng(10.670150, 122.950607),
                new LatLng(10.670661, 122.950808),
                new LatLng(10.671101, 122.950974),
                new LatLng(10.671350, 122.951104),
                new LatLng(10.671967, 122.951343),
                new LatLng(10.672608, 122.951592),
                new LatLng(10.673148, 122.951820),
                new LatLng(10.673736, 122.952069),
                new LatLng(10.674434, 122.952353),
                new LatLng(10.675193, 122.952672),
                new LatLng(10.675836, 122.952951),
                new LatLng(10.676437, 122.953200),
                new LatLng(10.676574, 122.953243)
        };
    }


}