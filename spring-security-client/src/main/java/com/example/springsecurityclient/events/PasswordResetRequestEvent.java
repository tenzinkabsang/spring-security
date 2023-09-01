package com.example.springsecurityclient.events;

import com.example.springsecurityclient.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PasswordResetRequestEvent extends ApplicationEvent {
    private final User user;
    private final HttpServletRequest request;

    public PasswordResetRequestEvent(User user, HttpServletRequest request) {
        super(user);
        this.user = user;
        this.request = request;
    }
}

