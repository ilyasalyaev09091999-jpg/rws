package com.refdata.api.domain.api;

import com.refdata.api.domain.entities.route_api.LockForRoute;
import com.refdata.api.domain.entities.rws_api.LockForRws;

import java.util.List;

/**
 * Доменный контракт чтения справочной информации о шлюзах.
 * <p>
 * Отдает две проекции, так как downstream-сервисам нужен разный набор полей.
 * </p>
 */
public interface LockService {

    /**
     * Возвращает все шлюзы в проекции для {@code rws-api}.
     *
     * @return список DTO шлюзов для {@code rws-api}.
     */
    List<LockForRws> getAllLocksForRws();

    /**
     * Возвращает все шлюзы в проекции для {@code route-api}.
     *
     * @return список DTO шлюзов с привязкой к узлам графа.
     */
    List<LockForRoute> getAllLocksForRoute();
}
