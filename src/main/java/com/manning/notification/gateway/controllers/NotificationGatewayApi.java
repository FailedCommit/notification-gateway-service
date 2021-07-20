package com.manning.notification.gateway.controllers;

import com.manning.notification.gateway.model.NotificationGatewayRequest;
import com.manning.notification.gateway.model.NotificationGatewayResponse;
import com.manning.notification.gateway.services.NotificationGatewayService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(("/api/notifications/send"))
@AllArgsConstructor
public class NotificationGatewayApi {
    private final NotificationGatewayService gatewayService;

    @PostMapping
    public NotificationGatewayResponse sendNotification(@RequestBody NotificationGatewayRequest request) {
        return gatewayService.sendNotification(request);
    }

    @GetMapping("/healthcheck")
    public String healthCheck (){
        return "UP";
    }
}
