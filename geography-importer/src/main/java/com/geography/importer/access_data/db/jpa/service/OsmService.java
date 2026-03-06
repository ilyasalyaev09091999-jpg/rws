package com.geography.importer.access_data.db.jpa.service;

import com.geography.importer.access_data.db.jpa.model.NodeEntity;
import com.geography.importer.access_data.db.jpa.model.WayEntity;
import com.geography.importer.access_data.db.jpa.model.WayNodeEntity;
import com.geography.importer.access_data.db.jpa.repository.NodeRepository;
import com.geography.importer.access_data.db.jpa.repository.WayNodeRepository;
import com.geography.importer.access_data.db.jpa.repository.WayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сервис доступа к JPA-слою для сохранения импортированных OSM-сущностей.
 */
@Service
@RequiredArgsConstructor
public class OsmService {
    private final WayRepository wayRepository;
    private final NodeRepository nodeRepository;
    private final WayNodeRepository wayNodeRepository;

    /**
     * Полностью очищает таблицы импорта в корректном порядке зависимостей:
     * {@code way_nodes -> ways -> nodes}.
     */
    @Transactional
    public void clearDatabase() {
        wayNodeRepository.deleteAllInBatch();
        wayRepository.deleteAllInBatch();
        nodeRepository.deleteAllInBatch();
    }

    /**
     * Сохраняет батч путей вместе с узлами и связями путь-узел.
     * <p>
     * Метод предварительно собирает уникальные {@link NodeEntity},
     * затем сохраняет ноды, после чего сохраняет {@link WayEntity}
     * с каскадной записью {@link WayNodeEntity}.
     * </p>
     *
     * @param ways список путей OSM, подготовленных к записи.
     */
    public void saveWaysBatch(List<WayEntity> ways) {
        Set<NodeEntity> allNodes = new HashSet<>();
        for (WayEntity way : ways) {
            for (WayNodeEntity wn : way.getNodes()) {
                allNodes.add(wn.getNode());
            }
        }

        nodeRepository.saveAll(allNodes);
        wayRepository.saveAll(ways);
    }
}
