package com.refdata.api.access_data.db.jpa.service;

import com.refdata.api.access_data.db.jpa.model.LockEntity;
import com.refdata.api.access_data.db.jpa.repository.LockJpaRepository;
import com.refdata.api.domain.api.LockService;
import com.refdata.api.domain.entities.route_api.LockForRoute;
import com.refdata.api.domain.entities.rws_api.LockForRws;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;

/**
 * Сервис для работы с данными "шлюзы" через Spring Data JPA
 */
@Slf4j
@Service("LockJpaService")
@RequiredArgsConstructor
public class LockJpaService implements LockService {

    private final LockJpaRepository repository;
    private final ModelMapper modelMapper;

    public List<LockForRws> getAllLocksForRws() {
        List<LockEntity> entities = repository.findAll();
        log.info("Found locks for RWS API count: {}", entities);
        return entities.stream().map(e -> modelMapper.map(e, LockForRws.class)).toList();
    }


    @Transactional
    public List<LockForRoute> getAllLocksForRoute() {
        List<LockEntity> entities = repository.findAllWithNodeIds();
        log.info("Found locks for ROUTE API: {}", entities);
        return entities.stream()
                .map(e -> {
                    LockForRoute dto = new LockForRoute();
                    dto.setName(e.getName());
                    dto.setNodeIds(
                            e.getNodeIds() != null
                                    ? new HashSet<>(e.getNodeIds())
                                    : new HashSet<>()
                    );
                    return dto;
                })
                .toList();

    }
}
