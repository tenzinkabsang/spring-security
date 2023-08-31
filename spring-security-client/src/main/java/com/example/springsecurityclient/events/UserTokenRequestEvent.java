package com.example.springsecurityclient.events;

import com.example.springsecurityclient.entities.VerificationToken;
import com.example.springsecurityclient.services.UserService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Getter
public class UserTokenRequestEvent extends ApplicationEvent {
    private final VerificationToken token;
    private final String applicationUrl;

    public UserTokenRequestEvent(VerificationToken token, String applicationUrl) {
        super(token);
        this.token = token;
        this.applicationUrl = applicationUrl;
    }
}


