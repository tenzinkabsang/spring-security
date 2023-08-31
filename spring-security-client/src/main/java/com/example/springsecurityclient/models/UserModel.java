package com.example.springsecurityclient.models;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserModel {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
