package com.geography.importer.business.importpbf.core.util;

import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@UtilityClass
public class GeometryUtil {

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * Создаёт Point из долготы и широты
     *
     * @param lon долгота
     * @param lat широта
     * @return JTS Point
     */
    public static Point point(double lon, double lat) {
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }
}
