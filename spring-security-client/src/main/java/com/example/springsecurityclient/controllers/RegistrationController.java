package com.example.springsecurityclient.controllers;

import com.example.springsecurityclient.entities.User;
import com.example.springsecurityclient.entities.VerificationToken;
import com.example.springsecurityclient.events.RegistrationCompleteEvent;
import com.example.springsecurityclient.events.UserTokenRequestEvent;
import com.example.springsecurityclient.models.UserModel;
import com.example.springsecurityclient.models.UserTokenException;
import com.example.springsecurityclient.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.*;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping("/hello")
    public String hello() {
        return "Hello dude!";
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {

        User user = userService.registerUser(userModel);

        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(request)
        ));

        return "success";
    }

    @GetMapping("/resendVerifyToken")
    public ResponseEntity<String> resendVerificationToken(@RequestParam("token") String oldToken,
                                                         final HttpServletRequest request) throws UserTokenException {
        VerificationToken token = userService.getExistingToken(oldToken);
        publisher.publishEvent(new UserTokenRequestEvent(
                token,
                applicationUrl(request)
        ));
        String success = "Check your email";

        return ok(success);
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam ("token") String token) {
        boolean success = userService.validateVerificationToken(token);

        if(!success)
            return "Verification failed. Link is expired!";

        return "Validated";
    }

    private String applicationUrl(HttpServletRequest request) {
        return String.format("http://%s:%s/%s",
                request.getServerName(),
                request.getServerPort(),
                request.getContextPath());
    }
}
