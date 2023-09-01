package com.example.springsecurityclient.services;

import com.example.springsecurityclient.entities.PasswordResetToken;
import com.example.springsecurityclient.entities.User;
import com.example.springsecurityclient.entities.VerificationToken;
import com.example.springsecurityclient.models.*;
import com.example.springsecurityclient.repositories.PasswordResetTokenRepository;
import com.example.springsecurityclient.repositories.UserRepository;
import com.example.springsecurityclient.repositories.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public User registerUser(UserModel userModel) {
        User newUser = new User();
        newUser.setFirstName(userModel.getFirstName());
        newUser.setLastName(userModel.getLastName());
        newUser.setEmail(userModel.getEmail());
        newUser.setRole("USER");

        String encodedPassword = passwordEncoder.encode(userModel.getPassword());
        newUser.setPassword(encodedPassword);

        return userRepository.save(newUser);
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {

        VerificationToken verificationToken = new VerificationToken(user, token);

        verificationTokenRepo.save(verificationToken);
    }

    @Override
    public boolean validateVerificationToken(String token) {
        var verificationToken =
                verificationTokenRepo.findVerificationTokenByExpiredFalseAndToken(token);

        if(verificationToken.isEmpty())
            return false;

        VerificationToken foundToken = verificationToken.get();
        if(foundToken.isExpired()) {
            log.info("Token expired: " + foundToken.getToken());
            foundToken.setExpired(true);
            verificationTokenRepo.save(foundToken);
            return false;
        }

        User user = foundToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        return true;
    }

    @Override
    public Tuple<Boolean, User> isTokenValid(String token) {
        Optional<PasswordResetToken> passwordResetToken =
                passwordResetTokenRepository.findTokenByExpiredAndToken(false, token);

        if(passwordResetToken.isEmpty())
            return new Tuple<>(false, null);

        PasswordResetToken foundToken = passwordResetToken.get();
        if(foundToken.hasExpired()) {
            log.info("Password token expired: " + foundToken.getToken());
            foundToken.setExpired(true);;
            passwordResetTokenRepository.save(foundToken);
            return new Tuple<>(false, null);
        }

        return new Tuple<>(true, foundToken.getUser());
    }

    @Override
    public void changePassword(User user, PasswordModel passwordModel) {

        String newPassword = passwordModel.getNewPassword();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        log.info("User: {}, password: {}", user.getEmail(), encodedPassword);
    }

    @Override
    public Optional<PasswordResetToken> getUserPasswordResetToken(Long userId) {

        Optional<PasswordResetToken> passwordResetToken =
                passwordResetTokenRepository.findPasswordResetTokenByUser(userId);

        return passwordResetToken;
    }

    @Override
    public void refreshPasswordResetToken(PasswordResetToken token) {
        token.setExpirationTime(token.calculateExpirationDate());
        token.setExpired(false);
        passwordResetTokenRepository.save(token);
    }

    @Override
    public boolean checkIfOldPasswordValid(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public VerificationToken getExistingVerificationToken(String oldToken) throws UserTokenException {

        var verificationToken = verificationTokenRepo.findByToken(oldToken);

        return verificationToken.orElseThrow(() -> new UserTokenException(oldToken));
    }

    @Override
    public void updateVerificationToken(VerificationToken token) {
        verificationTokenRepo.save(token);
    }

    @Override
    public User findUserByEmail(String email) throws UserNotFoundException {
        Optional<User> user = userRepository.findByEmailAndEnabled(email, true);

        return user.orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public void createPasswordResetToken(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.save(passwordResetToken);
    }

}
