package com.example.springsecurityclient.events;

import com.example.springsecurityclient.entities.User;
import com.example.springsecurityclient.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // Create the Verification token for the user
        User user = event.getUser();
        String token = UUID.randomUUID().toString();

        userService.saveVerificationTokenForUser(token, user);

        // Send email to user
        String url = event.getApplicationUrl() + "verifyRegistration?token=" + token;
        log.info("Click the link to verify your account: {}", url);
    }
}


