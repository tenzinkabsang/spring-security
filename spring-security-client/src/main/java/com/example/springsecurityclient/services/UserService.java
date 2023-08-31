package com.example.springsecurityclient.services;

import com.example.springsecurityclient.entities.User;
import com.example.springsecurityclient.entities.VerificationToken;
import com.example.springsecurityclient.models.UserModel;
import com.example.springsecurityclient.models.UserTokenException;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    boolean validateVerificationToken(String token);

    VerificationToken getExistingToken(String oldToken) throws UserTokenException;

    void updateVerificationToken(VerificationToken token);
}


