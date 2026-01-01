package com.refdata.api.access_data.db.jpa.service;

import com.refdata.api.access_data.db.jpa.model.PortEntity;
import com.refdata.api.access_data.db.jpa.repository.PortJpaRepository;
import com.refdata.api.access_data.domain.api.PortService;
import com.refdata.api.access_data.domain.entities.Port;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с данными "порты" через Spring Data JPA
 */
@Service("PortJpaService")
@RequiredArgsConstructor
public class PortJpaService implements PortService {

    private final PortJpaRepository repository;
    private final ModelMapper modelMapper;

    public List<Port> findAll() {
        List<PortEntity> entities = repository.findAll();
        return entities.stream().map(e -> modelMapper.map(e, Port.class)).toList();
    }

    public Optional<Port> findById(String id) {
        Optional<PortEntity> entity = repository.findById(id);
        return Optional.of(modelMapper.map(entity, Port.class));
    }
}
