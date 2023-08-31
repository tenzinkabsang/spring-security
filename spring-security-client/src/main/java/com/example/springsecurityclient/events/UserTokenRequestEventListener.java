package com.example.springsecurityclient.events;

import com.example.springsecurityclient.entities.VerificationToken;
import com.example.springsecurityclient.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class UserTokenRequestEventListener implements ApplicationListener<UserTokenRequestEvent> {

    private final UserService userService;

    @Autowired
    public UserTokenRequestEventListener(UserService userService){
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(UserTokenRequestEvent event) {
        String newToken = UUID.randomUUID().toString();

        VerificationToken verificationToken = event.getToken();
        verificationToken.setToken(newToken);
        verificationToken.setExpired(false);
        userService.updateVerificationToken(verificationToken);

        // Send email to user
        String url = event.getApplicationUrl() + "verifyRegistration?token=" + newToken;
        log.info("New token created for user: {}", url);
    }
}
