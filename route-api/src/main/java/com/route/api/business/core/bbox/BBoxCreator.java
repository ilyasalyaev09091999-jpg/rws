package com.route.api.business.core.bbox;

import org.springframework.stereotype.Service;

@Service
public class BBoxCreator {

    public double[] findBBox(double startLat, double startLon, double endLat, double endLon) {
        double buffer = 3;

        double minLon = Math.min(startLon, endLon) - buffer;
        double minLat = Math.min(startLat, endLat) - buffer;
        double maxLon = Math.max(startLon, endLon) + buffer;
        double maxLat = Math.max(startLat, endLat) + buffer;

        return new double[] {minLat, minLon, maxLat, maxLon};
    }
}
