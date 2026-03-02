package com.refdata.api.domain.api;
import com.refdata.api.domain.entities.route_api.LockForRoute;
import com.refdata.api.domain.entities.rws_api.LockForRws;

import java.util.List;

/**
 * Общий интерфейс для доступа к данным "шлюзы", который будет использоваться в бизнес-логики. Он позволяет не
 * привязываться к конкретной реализации.
 */
public interface LockService {

    List<LockForRws> getAllLocksForRws();

    List<LockForRoute> getAllLocksForRoute();
}
