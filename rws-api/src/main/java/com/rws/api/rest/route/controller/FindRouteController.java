package com.rws.api.rest.route.controller;

import com.rws.api.rest.route.controller.error.RouteNotFoundException;
import com.rws.api.rest.route.dto.RouteFinderRequest;
import com.rws.api.rest.route.dto.RouteFinderResponse;
import com.rws.api.rest.route.grpcclient.RouteGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST-контроллер построения маршрута.
 * <p>
 * Принимает входные параметры из HTTP-запроса, формирует
 * {@link RouteFinderRequest}, делегирует расчёт маршрута в
 * {@link RouteGrpcClient} (вызов {@code route-api} по gRPC) и возвращает
 * результат клиенту в виде {@link RouteFinderResponse}.
 * </p>
 */
@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class FindRouteController {

    private final RouteGrpcClient routeGrpcClient;

    /**
     * Выполняет поиск маршрута между двумя географическими точками.
     *
     * @param startLon долгота точки отправления.
     * @param startLat широта точки отправления.
     * @param endLon долгота точки назначения.
     * @param endLat широта точки назначения.
     * @param departureTime планируемое время отправления.
     * @param speed скорость движения по маршруту.
     * @return HTTP 200 с рассчитанным маршрутом и метаданными.
     * @throws RouteNotFoundException если маршрут между точками не найден.
     */
    @GetMapping("/find")
    public ResponseEntity<RouteFinderResponse> findRoute(
            @RequestParam double startLon,
            @RequestParam double startLat,
            @RequestParam double endLon,
            @RequestParam double endLat,
            @RequestParam LocalDateTime departureTime,
            @RequestParam int speed) throws RouteNotFoundException {
        log.info("Find route request");
        RouteFinderRequest request = new RouteFinderRequest(startLon, startLat, endLon, endLat, departureTime, speed);
        RouteFinderResponse response = routeGrpcClient.findRoute(request);
        log.info("Route successfully found: {}", response);
        return ResponseEntity.ok(response);
    }
}
