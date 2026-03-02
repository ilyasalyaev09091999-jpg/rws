package com.refdata.api.domain.api;

import com.refdata.api.domain.entities.route_api.PortForRoute;
import com.refdata.api.domain.entities.rws_api.PortForRws;

import java.util.List;

/**
 * Общий интерфейс для доступа к данным "порты", который будет использоваться в бизнес-логики. Он позволяет не
 * привязываться к конкретной реализации.
 */
public interface PortService {

    List<PortForRws> getAllLocksForRws();

    List<PortForRoute> getAllPortsForRoute();
}
