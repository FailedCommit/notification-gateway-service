package com.manning.notification.gateway.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NotificationGatewayRequest {

    private String customerId;
    private String notificationMode;
    private String notificationContent;
    private String emailSubject;
    private String emailAddress;
    private String phoneNumber;

}
