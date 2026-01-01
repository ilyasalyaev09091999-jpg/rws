package com.geography.importer.business.importpbf.core.filetodb;

import com.geography.importer.access_data.db.jpa.model.NodeEntity;
import com.geography.importer.access_data.db.jpa.model.WayEntity;
import com.geography.importer.access_data.db.jpa.model.WayNodeEntity;
import com.geography.importer.access_data.db.jpa.model.WayNodeId;
import com.geography.importer.access_data.db.jpa.service.OsmService;
import com.geography.importer.business.importpbf.core.util.GeometryUtil;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.pbf.seq.PbfIterator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OsmImportService {

    private final OsmService osmService;

    public void importWaterways() throws IOException {
        Path osmFile = null;
        try (Stream<Path> stream = Files.list(Paths.get("geography-importer/src/main/resources"))) {
            Optional<Path> file = stream
                    .filter(p -> p.getFileName().toString().endsWith(".osm.pbf"))
                    .findFirst();

            if (file.isPresent()) {
                osmFile = file.get();
            }
        }

        if (osmFile == null) {
            throw new NoSuchFileException("Not find file for import");
        }

        System.out.println("file " + osmFile.getFileName());


        // Очищаем базу
        osmService.clearDatabase();


        Set<Long> neededNodeIds = new HashSet<>();
        try (InputStream input = Files.newInputStream(osmFile)) {
            OsmIterator iterator = new PbfIterator(input, false);
            for (EntityContainer c : iterator) {
                if (c.getType() == EntityType.Way) {
                    OsmWay way = (OsmWay) c.getEntity();
                    Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
                    String waterway = tags.get("waterway");
                    if (waterway == null || (!waterway.equals("river") && !waterway.equals("canal"))) continue;

                    for (int i = 0; i < way.getNumberOfNodes(); i++) {
                        neededNodeIds.add(way.getNodeId(i));
                    }
                }
            }
        }
        System.out.println("neededNodeIds size: " + neededNodeIds.size());


        Map<Long, NodeEntity> nodeEntities = new HashMap<>();
        try (InputStream input = Files.newInputStream(osmFile)) {
            OsmIterator iterator = new PbfIterator(input, false);
            for (EntityContainer c : iterator) {
                if (c.getType() == EntityType.Node) {
                    OsmNode n = (OsmNode) c.getEntity();
                    if (neededNodeIds.contains(n.getId())) {
                        NodeEntity node = new NodeEntity();
                        node.setId(n.getId());
                        node.setLatitude(n.getLatitude());
                        node.setLongitude(n.getLongitude());
                        node.setGeom(GeometryUtil.point(n.getLongitude(), n.getLatitude()));

                        nodeEntities.put(n.getId(), node);
                    }
                }
            }
        }
        System.out.println("nodeEntities size: " + nodeEntities.size());


        int BATCH_SIZE = 500;
        List<WayEntity> batch = new ArrayList<>(BATCH_SIZE);

        try (InputStream input = Files.newInputStream(osmFile)) {
            OsmIterator iterator = new PbfIterator(input, false);

            for (EntityContainer c : iterator) {
                if (c.getType() != EntityType.Way) continue;

                OsmWay way = (OsmWay) c.getEntity();
                Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
                String waterway = tags.get("waterway");
                if (waterway == null || (!waterway.equals("river") && !waterway.equals("canal"))) continue;

                WayEntity wayEntity = new WayEntity();

                wayEntity.setId(way.getId());

                // Список WayNodeEntity
                List<WayNodeEntity> wnList = wayEntity.getNodes();

                for (int i = 0; i < way.getNumberOfNodes(); i++) {
                    long nodeId = way.getNodeId(i);
                    NodeEntity node = nodeEntities.get(nodeId);
                    if (node == null) {
                        continue;
                    }

                    WayNodeId wid = new WayNodeId(way.getId(), i);

                    WayNodeEntity wne = new WayNodeEntity();
                    wne.setId(wid);
                    wne.setWay(wayEntity);
                    wne.setNode(node);

                    wnList.add(wne);
                }

                batch.add(wayEntity);

                if (batch.size() >= BATCH_SIZE) {
                    osmService.saveWaysBatch(batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                osmService.saveWaysBatch(batch);
            }
        }

        System.out.println("importWaterways finish");
    }
}

