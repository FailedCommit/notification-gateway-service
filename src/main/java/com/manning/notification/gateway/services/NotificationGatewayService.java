package com.manning.notification.gateway.services;

import com.manning.notification.gateway.model.NotificationGatewayRequest;
import com.manning.notification.gateway.model.NotificationGatewayResponse;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class NotificationGatewayService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationGatewayService.class);

    private final JavaMailSender emailSender;

    //TODO: Move them to environment variables
    @Value("${email.from}")
    private String notificationReceiver;
    @Value("${spring.mail.username}")
    private String notificationSender;
    @Value("${twilio.username}")
    private String accountSID;
    @Value("${twilio.password}")
    private String authToken;
    @Value("${sms.from}")
    private String smsFrom;

    @Autowired
    public NotificationGatewayService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public NotificationGatewayResponse sendNotification(NotificationGatewayRequest request) {
        NotificationGatewayResponse response = createNotificationGatewaySuccessResponse();
        if("EMAIL".equalsIgnoreCase(request.getNotificationMode())) {
            sendEmailNotification(request, response);
        } else if("SMS".equalsIgnoreCase(request.getNotificationMode())) {
            sendSMSNotification(request, response);
        }
        return response;
    }

    private void sendSMSNotification(NotificationGatewayRequest request, NotificationGatewayResponse response) {
        Twilio.init(accountSID, authToken);
        Message message = Message
                .creator(new PhoneNumber(request.getPhoneNumber()),
                        new PhoneNumber(smsFrom),
                        request.getNotificationContent())
                .create();
        logger.info("Message sent successfully. Message Id: {}", message.getSid());
    }

    private void sendEmailNotification(NotificationGatewayRequest request, NotificationGatewayResponse response) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.addAttachment("CitizenBank.png", new ClassPathResource("CitizenBank.png"));
            helper.setTo(notificationSender);
            helper.setFrom(notificationReceiver);
            helper.setText(request.getNotificationContent(), true);
            helper.setSubject(request.getEmailSubject());
            emailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send notification with exception: {}",  e.getStackTrace());
            response.setStatus("FAILURE");
            response.setStatusDescription("Notification Failed");
        }
    }

    private NotificationGatewayResponse createNotificationGatewaySuccessResponse() {
        NotificationGatewayResponse response = new NotificationGatewayResponse();
        response.setStatus("SUCCESS");
        response.setStatusDescription("Notification Received Successfully");
        return response;
    }
}