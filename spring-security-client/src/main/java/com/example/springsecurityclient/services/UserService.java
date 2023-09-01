package com.example.springsecurityclient.services;

import com.example.springsecurityclient.entities.PasswordResetToken;
import com.example.springsecurityclient.entities.User;
import com.example.springsecurityclient.entities.VerificationToken;
import com.example.springsecurityclient.models.*;

import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    boolean validateVerificationToken(String token);

    VerificationToken getExistingVerificationToken(String oldToken) throws UserTokenException;

    void updateVerificationToken(VerificationToken token);

    User findUserByEmail(String email) throws UserNotFoundException;

    void createPasswordResetToken(PasswordResetToken passwordResetToken);

    Tuple<Boolean, User> isTokenValid(String token);

    void changePassword(User user, PasswordModel passwordModel);

    Optional<PasswordResetToken> getUserPasswordResetToken(Long userId);

    void refreshPasswordResetToken(PasswordResetToken token);

    boolean checkIfOldPasswordValid(User user, String oldPassword);
}


