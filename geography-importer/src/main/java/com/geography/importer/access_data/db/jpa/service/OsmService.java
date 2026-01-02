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


@Service
@RequiredArgsConstructor
public class OsmService {
    private final WayRepository wayRepository;
    private final NodeRepository nodeRepository;
    private final WayNodeRepository wayNodeRepository;

    @Transactional
    public void clearDatabase() {
        wayNodeRepository.deleteAllInBatch();
        wayRepository.deleteAllInBatch();
        nodeRepository.deleteAllInBatch();
    }



    public void saveWaysBatch(List<WayEntity> ways) {
        // 1. Собираем все ноды, на которые ссылаются пути
        Set<NodeEntity> allNodes = new HashSet<>();
        for (WayEntity way : ways) {
            for (WayNodeEntity wn : way.getNodes()) {
                allNodes.add(wn.getNode());
            }
        }
        // 2. Сохраняем ноды
        nodeRepository.saveAll(allNodes);
        // 3. Сохраняем пути вместе с WayNodeEntity (если cascade = ALL)
        wayRepository.saveAll(ways);
    }
}
