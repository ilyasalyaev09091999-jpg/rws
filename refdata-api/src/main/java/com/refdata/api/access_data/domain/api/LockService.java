package com.refdata.api.access_data.domain.api;

import com.refdata.api.access_data.domain.entities.Lock;
import java.util.List;
import java.util.Optional;

/**
 * Общий интерфейс для доступа к данным "шлюзы", который будет использоваться в бизнес-логики. Он позволяет не
 * привязываться к конкретной реализации.
 */
public interface LockService {

    List<Lock> findAll();

    Optional<Lock> findById(String id);

}
