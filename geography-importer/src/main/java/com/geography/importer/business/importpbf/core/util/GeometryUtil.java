package com.geography.importer.business.importpbf.core.util;

import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * Утилиты работы с геометрией для импорта OSM-нод.
 */
@UtilityClass
public class GeometryUtil {

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * Создаёт JTS {@link Point} в системе координат WGS84 (SRID 4326).
     *
     * @param lon долгота точки.
     * @param lat широта точки.
     * @return геометрическая точка для последующего сохранения в PostGIS.
     */
    public static Point point(double lon, double lat) {
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }
}
