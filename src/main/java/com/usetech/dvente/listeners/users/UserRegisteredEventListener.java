package com.usetech.dvente.listeners.users;

import com.usetech.dvente.events.users.UserRegisteredEvent;
import com.usetech.dvente.services.notifs.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventListener {
    private final EmailService emailService;

    public UserRegisteredEventListener(EmailService emailService) {
        this.emailService = emailService;
    }


    @Async
    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        emailService.sendWelcomeEmail(event.getUser());
    }
}
