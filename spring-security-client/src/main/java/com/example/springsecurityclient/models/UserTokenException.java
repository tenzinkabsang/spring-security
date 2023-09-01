package com.example.springsecurityclient.models;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserTokenException extends Exception {
    public UserTokenException(String token) {
        super(token);
        log.error("No record found for token {}", token);
    }
}
