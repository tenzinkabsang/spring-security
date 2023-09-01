package com.example.springsecurityclient.models;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundException extends Exception {

    private static final String msg = "User with email %s not found";
    public UserNotFoundException(String email) {
        super(String.format(msg, email));

        log.error(String.format(msg, email));
    }
}
