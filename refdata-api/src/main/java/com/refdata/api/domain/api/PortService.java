package com.refdata.api.domain.api;

import com.refdata.api.domain.entities.route_api.PortForRoute;
import com.refdata.api.domain.entities.rws_api.PortForRws;

import java.util.List;

/**
 * Доменный контракт чтения справочной информации о портах.
 * <p>
 * Интерфейс намеренно отдает две проекции данных для разных потребителей:
 * </p>
 * <ul>
 *   <li>{@link PortForRws} для {@code rws-api} (внешний API/клиентские нужды),</li>
 *   <li>{@link PortForRoute} для {@code route-api} (данные для маршрутизации).</li>
 * </ul>
 */
public interface PortService {

    /**
     * Возвращает все порты в проекции для {@code rws-api}.
     *
     * @return список портов с полями, нужными {@code rws-api}.
     */
    List<PortForRws> getAllLocksForRws();

    /**
     * Возвращает все порты в проекции для {@code route-api}.
     *
     * @return компактный список портов с полями, нужными {@code route-api}.
     */
    List<PortForRoute> getAllPortsForRoute();
}
