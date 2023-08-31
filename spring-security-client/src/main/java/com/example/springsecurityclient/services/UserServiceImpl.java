package com.example.springsecurityclient.services;

import com.example.springsecurityclient.entities.User;
import com.example.springsecurityclient.entities.VerificationToken;
import com.example.springsecurityclient.models.UserModel;
import com.example.springsecurityclient.models.UserTokenException;
import com.example.springsecurityclient.repositories.UserRepository;
import com.example.springsecurityclient.repositories.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public VerificationToken getExistingToken(String oldToken) throws UserTokenException {

        var verificationToken = verificationTokenRepo.findByToken(oldToken);

        VerificationToken token = verificationToken.orElseThrow(
                () -> {
                    log.info("No record found for token: " + oldToken);
                    return new UserTokenException("Invalid token");
                }
        );

        return token;
    }

    @Override
    public void updateVerificationToken(VerificationToken token) {
        verificationTokenRepo.save(token);
    }
}
