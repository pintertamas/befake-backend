package com.pintertamas.befake.notificationservice.listener;

import com.pintertamas.befake.notificationservice.config.CustomPropertyConfig;
import com.pintertamas.befake.notificationservice.model.Mail;
import com.pintertamas.befake.notificationservice.service.EmailService;
import com.pintertamas.befake.notificationservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class KafkaUserEventListener {

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CustomPropertyConfig customPropertyConfig;

    @KafkaListener(topics = "registration")
    public void listenToRegistrations(String message) {
        log.info("Message received: " + message);
        String email = message.split(",")[0];
        log.info("email: " + email);
        String username = message.split(",")[1];
        log.info("username: " + username);

        try {
            Mail mail = createMail(email, username);
            emailService.sendEmail(mail);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

    public Mail createMail(String email, String username) {
        Mail mail = new Mail();
        mail.setFrom(customPropertyConfig.mailFrom);
        mail.setMailTo(email);
        mail.setSubject("Registration");
        Map<String, Object> model = new HashMap<>();
        model.put("username", username);
        mail.setProps(model);
        return mail;
    }

    @KafkaListener(topics = "friend")
    public void listenToFriendRequests(String message) {
        log.info("Message received: " + message);

        try {
            Long friendId = Long.valueOf(message);
            notificationService.sendFriendRequestNotification(friendId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
