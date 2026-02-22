package com.refdata.api.access_data.db.jpa.service;

import com.refdata.api.access_data.db.jpa.model.LockEntity;
import com.refdata.api.access_data.db.jpa.repository.LockJpaRepository;
import com.refdata.api.access_data.domain.api.LockService;
import com.refdata.api.access_data.domain.entities.Lock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
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

    public List<Lock> findAll() {
        List<LockEntity> entities = repository.findAll();
        log.info("Found locks count: {}", entities.size());
        return entities.stream().map(e -> modelMapper.map(e, Lock.class)).toList();
    }
}
