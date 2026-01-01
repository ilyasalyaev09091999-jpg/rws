package com.refdata.api.access_data.domain.api;

import com.refdata.api.access_data.domain.entities.Port;

import java.util.List;
import java.util.Optional;

/**
 * Общий интерфейс для доступа к данным "порты", который будет использоваться в бизнес-логики. Он позволяет не
 * привязываться к конкретной реализации.
 */
public interface PortService {

    List<Port> findAll();

    Optional<Port> findById(String id);

}
