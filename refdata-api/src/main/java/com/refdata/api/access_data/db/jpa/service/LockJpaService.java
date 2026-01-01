package com.refdata.api.access_data.db.jpa.service;

import com.refdata.api.access_data.db.jpa.model.LockEntity;
import com.refdata.api.access_data.db.jpa.repository.LockJpaRepository;
import com.refdata.api.access_data.domain.api.LockService;
import com.refdata.api.access_data.domain.entities.Lock;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с данными "шлюзы" через Spring Data JPA
 */
@Service("LockJpaService")
@RequiredArgsConstructor
public class LockJpaService implements LockService {

    private final LockJpaRepository repository;
    private final ModelMapper modelMapper;

    public List<Lock> findAll() {
        List<LockEntity> entities = repository.findAll();
        return entities.stream().map(e -> modelMapper.map(e, Lock.class)).toList();
    }

    public Optional<Lock> findById(String id) {
        Optional<LockEntity> entity = repository.findById(id);
        return Optional.of(modelMapper.map(entity, Lock.class));
    }
}
