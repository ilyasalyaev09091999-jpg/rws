package com.rws.api.rest.route.controller;

import com.rws.api.rest.route.controller.error.RouteNotFoundException;
import com.rws.api.rest.route.dto.RouteFinderRequest;
import com.rws.api.rest.route.dto.RouteFinderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class FindRouteController {


    @GetMapping("/find")
    public ResponseEntity<RouteFinderResponse> findRoute(@RequestParam double startLon, @RequestParam double startLat, @RequestParam double endLon,
                                                         @RequestParam double endLat, @RequestParam LocalDateTime departureTime, @RequestParam int speed)
            throws RouteNotFoundException {
        RouteFinderRequest request = new RouteFinderRequest(startLon, startLat, endLon, endLat, departureTime, speed);
    }
}
