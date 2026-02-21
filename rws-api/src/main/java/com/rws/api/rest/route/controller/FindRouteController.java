package com.route.api.rest.controller;

import com.route.api.business.core.exceptions.RouteNotFoundException;
import com.route.api.business.manager.FindRouteManager;
import com.route.api.rest.dto.RouteFinderRequest;
import com.route.api.rest.dto.RouteFinderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class FindRouteController {

    private final FindRouteManager findRouteManager;

    @GetMapping("/find")
    public ResponseEntity<RouteFinderResponse> findRoute(@RequestParam double startLon, @RequestParam double startLat, @RequestParam double endLon,
                                                         @RequestParam double endLat, @RequestParam LocalDateTime departureTime, @RequestParam int speed)
            throws RouteNotFoundException {
        RouteFinderRequest request = new RouteFinderRequest(startLon, startLat, endLon, endLat, departureTime, speed);
        return ResponseEntity.ok(findRouteManager.findRoute(request));
    }
}
