package com.example.springsecurityclient.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RegistrationCompleteEmailHandler implements ApplicationListener<RegistrationCompleteEvent> {

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        // Send email
        log.info("Email Handler called for user: " + event.getUser().getEmail());

    }
}
